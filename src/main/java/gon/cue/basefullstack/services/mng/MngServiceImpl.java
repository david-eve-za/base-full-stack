package gon.cue.basefullstack.services.mng;

import gon.cue.basefullstack.dao.mng.IBookDao;
import gon.cue.basefullstack.model.mng.Book;
import gon.cue.basefullstack.response.ResponseRest;
import gon.cue.basefullstack.utils.MngMetaData;
import gon.cue.basefullstack.utils.TMOFans;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReferenceArray;

@Service
@Slf4j
public class MngServiceImpl implements IMngService {

    private final IBookDao bookDao;
    private List<Runnable> getMeta = new ArrayList<>();
    private BlockingQueue<Book> queue;

    public MngServiceImpl(IBookDao bookDao) {
        this.bookDao = bookDao;
        queue = new LinkedBlockingQueue<>();
        new Thread(() -> {
            while (true) {
                try {
                    Book book = queue.take();
                    MngMetaData mngMetaData = new MngMetaData(book);
                    mngMetaData.run();
                    bookDao.save(book);
                    mngMetaData.createPDF();
                } catch (InterruptedException | IOException e) {
                    log.error("Book not fetched: ", e);
                }
            }
        }).start();
    }

    @Override
    public ResponseEntity<ResponseRest<Book>> addBook(Book book) {
        ResponseRest<Book> responseRest = new ResponseRest<>();
        try {
            if (bookDao.findByUrl(book.getUrl()) != null) {
                responseRest.setMetadata("warning", "400", "Book already exists");
                return ResponseEntity.badRequest().body(responseRest);
            }
            book = bookDao.save(book);
            queue.put(book);
            responseRest.getData().add(book);
            responseRest.setMetadata("success", "200", "OK");
        } catch (Exception e) {
            log.error("Error adding book: " + book.getUrl(), e);
            responseRest.setMetadata("error", "500", "Internal Server Error");
            return ResponseEntity.status(500).body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }

    @Override
    public void fetchMetadata(Long bookId) {
        log.info("Fetching metadata for book: " + bookId);
        bookDao.findById(bookId).ifPresent(book -> {
            try {
                queue.put(book);
            } catch (InterruptedException e) {
                log.error("Error fetching metadata for book: " + bookId, e);
            }
        });
    }

    @Override
    public ResponseEntity<ResponseRest<Book>> getBook(Long id) {
        ResponseRest<Book> responseRest = new ResponseRest<>();
        try {
            Book book = bookDao.findById(id).orElse(null);
            if (book == null) {
                responseRest.setMetadata("warning", "400", "Book not found");
                return ResponseEntity.badRequest().body(responseRest);
            }
            responseRest.getData().add(book);
            responseRest.setMetadata("success", "200", "OK");
        } catch (Exception e) {
            log.error("Error getting book: " + id, e);
            responseRest.setMetadata("error", "500", "Internal Server Error");
            return ResponseEntity.status(500).body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }

    @Override
    public ResponseEntity<ResponseRest<Book>> getBooks() {
        ResponseRest<Book> responseRest = new ResponseRest<>();
        try {
            responseRest.getData().addAll((Collection<? extends Book>) bookDao.findAll());
            responseRest.setMetadata("success", "200", "OK");
        } catch (Exception e) {
            log.error("Error fetching books", e);
            responseRest.setMetadata("error", "500", "Internal Server Error");
            return ResponseEntity.status(500).body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }

    @Override
    public ResponseEntity<ResponseRest<Book>> updateFromLTMO() {
        ResponseRest<Book> responseRest = new ResponseRest<>();
        responseRest.setMetadata("success", "200", "OK");

        new Thread(() -> {
            TMOFans tmoFans = new TMOFans();
            tmoFans.login();
            List<String> profileList = tmoFans.getProfileList();
            profileList.forEach(book -> {
                try {
                    tmoFans.getBooks(book).stream().forEach(item -> {
                        bookDao.findByUrl(item.getUrl()).ifPresentOrElse(book1 -> {
                            log.info("Book already exists: {}", book1);
                        }, () -> {
                            bookDao.save(item);
                            log.info("Book created: {}", item);
                        });
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            tmoFans.logout();
        }).start();

        return ResponseEntity.ok(responseRest);
    }

    @Override
    public ResponseEntity<byte[]> getBookPDF(Long id, Long chapter) {
        byte[] pdf = null;
        Optional<Book> daoById = this.bookDao.findById(id);
        if (daoById.isPresent()) {
            Book book = daoById.get();
            MngMetaData mngMetaData = new MngMetaData(book);
            pdf = mngMetaData.getPDFByteArray(chapter);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdf);
    }
}

package gon.cue.basefullstack.services.mng;

import gon.cue.basefullstack.dao.mng.IBookDao;
import gon.cue.basefullstack.model.mng.Book;
import gon.cue.basefullstack.response.ResponseRest;
import gon.cue.basefullstack.utils.MngMetaData;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class MngServiceImpl implements IMngService {

    private final IBookDao bookDao;
    private List<Runnable> getMeta = new ArrayList<>();

    public MngServiceImpl(IBookDao bookDao) {
        this.bookDao = bookDao;
    }

    @Override
    public ResponseEntity<ResponseRest<Book>> addBook(Book book) {
        ResponseRest<Book> responseRest = new ResponseRest<>();
        try {
            if (bookDao.findByUrl(book.getUrl()) != null) {
                responseRest.setMetadata("warning", "400", "Book already exists");
                return ResponseEntity.badRequest().body(responseRest);
            }
            bookDao.save(book);
            responseRest.getData().add(book);
            responseRest.setMetadata("success", "200", "OK");
        } catch (Exception e) {
            responseRest.setMetadata("error", "500", "Internal Server Error");
            return ResponseEntity.status(500).body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }

    @Job(name = "Fetch Metadata", retries = 0)
    @Override
    public void fetchMetadata(Long bookId) {
        log.info("Fetching metadata for book: " + bookId);
        bookDao.findById(bookId).ifPresent(book -> {
            try {
                new MngMetaData(book).run();
                bookDao.save(book);
            } catch (IOException e) {
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
            responseRest.setMetadata("error", "500", "Internal Server Error");
            return ResponseEntity.status(500).body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }
}

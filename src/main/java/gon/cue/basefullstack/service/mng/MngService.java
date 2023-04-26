package gon.cue.basefullstack.service.mng;

import gon.cue.basefullstack.entities.mng.Book;
import gon.cue.basefullstack.repository.mng.BookRepository;
import gon.cue.basefullstack.util.mng.MngMetaData;
import gon.cue.basefullstack.util.mng.TMOFans;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Transactional
public class MngService {

    private final BookRepository bookDao;
    private List<Runnable> getMeta = new ArrayList<>();
    private BlockingQueue<Book> queue;

    public MngService(BookRepository bookDao) {
        this.bookDao = bookDao;
        queue = new LinkedBlockingQueue<>();

        startQueueProcessor();
        startFillQueue();

    }

    private void startFillQueue() {
        new Thread(() -> {
            while (true) {
                bookDao.findAll().forEach(book -> {
                    try {
                        LocalDateTime now = LocalDateTime.now();
                        if (book.getLastUpdated() == null) {
                            if (!queue.contains(book))
                                queue.put(book);
                        }else {
                            LocalDateTime lastUpdated = LocalDateTime.ofEpochSecond(book.getLastUpdated(), 0, ZoneOffset.UTC);
                            if (now.minusDays(7).isAfter(lastUpdated)) {
                                if (!queue.contains(book))
                                    queue.put(book);
                            }
                        }
                    } catch (InterruptedException e) {
                        log.error("Error filling queue: ", e);
                    }
                });
                try {
                    TimeUnit.HOURS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startQueueProcessor() {
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

    public Book addBook(Book book) {
        return bookDao.save(book);
    }

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

    public Optional<Book> getBook(Long id) {
        return bookDao.findOneWithChaptersById(id);
    }

    public Page<Book> getBooks(Pageable pageable) {
        return bookDao.findAll(pageable);
    }

    public void updateFromTMO() {

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

    }

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

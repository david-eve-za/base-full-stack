package gon.cue.basefullstack.controller.mng;

import gon.cue.basefullstack.model.mng.Book;
import gon.cue.basefullstack.response.ResponseRest;
import gon.cue.basefullstack.services.mng.IMngService;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mng")
public class MngRestController {

    private final IMngService mngService;
    private final JobScheduler jobScheduler;

    public MngRestController(IMngService mngService, JobScheduler jobScheduler) {
        this.mngService = mngService;
        this.jobScheduler = jobScheduler;
    }

    @GetMapping("/books")
    public ResponseEntity<ResponseRest<Book>> getBooks() {
        return mngService.getBooks();
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<ResponseRest<Book>> getBook(@PathVariable Long id) {
        return mngService.getBook(id);
    }

    @PostMapping("/addBook")
    public ResponseEntity<ResponseRest<Book>> addBook(@RequestParam("url") String url) {
        Book book = new Book();
        book.setUrl(url);
        ResponseEntity<ResponseRest<Book>> entity = mngService.addBook(book);
        if (entity.getStatusCode().is2xxSuccessful())
            jobScheduler.enqueue(() -> mngService.fetchMetadata(entity.getBody().getData().get(0).getId()));
        return entity;
    }

    @PostMapping("/getMeta/{id}")
    public ResponseEntity<ResponseRest<Book>> getMeta(@PathVariable Long id) {
        ResponseEntity<ResponseRest<Book>> book = mngService.getBook(id);
        if (book.getStatusCode().is2xxSuccessful())
            jobScheduler.enqueue(() -> mngService.fetchMetadata(id));
        return book;
    }

    @GetMapping("/getAllMeta")
    public ResponseEntity<ResponseRest<Book>> getAllMeta() {
        List<Book> books = mngService.getBooks().getBody().getData();

        for (Book book : books) {
            jobScheduler.enqueue(() -> mngService.fetchMetadata(book.getId()));
        }

        return mngService.getBooks();

    }

}

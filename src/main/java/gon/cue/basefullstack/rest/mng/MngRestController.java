package gon.cue.basefullstack.rest.mng;

import gon.cue.basefullstack.entities.mng.Book;
import gon.cue.basefullstack.service.mng.MngService;
import gon.cue.basefullstack.util.PaginationUtil;
import gon.cue.basefullstack.util.ResponseUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/mng")
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:8888"})
public class MngRestController {

    private final MngService mngService;

    public MngRestController(MngService mngService) {
        this.mngService = mngService;
    }

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getBooks(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        final Page<Book> page = mngService.getBooks(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(mngService.getBook(id));
    }

    @GetMapping("/books/{id}/pdf/{chapter}")
    public ResponseEntity<byte[]> getBookPDF(@PathVariable Long id, @PathVariable Long chapter) {
        return mngService.getBookPDF(id, chapter);
    }

    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@RequestParam("url") String url) {
        Book book = new Book();
        book.setUrl(url);
        Book result = mngService.addBook(book);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/books/meta/{id}")
    public void getMeta(@PathVariable Long id) {
        Optional<Book> book = mngService.getBook(id);
        if (book.isPresent())
            mngService.fetchMetadata(id);
    }

    @GetMapping("/books/tmo")
    public void getTMOBooks() {
        mngService.updateFromTMO();
    }



}

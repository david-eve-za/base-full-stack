package gon.cue.basefullstack.services.mng;

import gon.cue.basefullstack.model.mng.Book;
import gon.cue.basefullstack.response.ResponseRest;
import org.springframework.http.ResponseEntity;

public interface IMngService {
    ResponseEntity<ResponseRest<Book>> addBook(Book book);
    void fetchMetadata(Long bookId);
    ResponseEntity<ResponseRest<Book>> getBook(Long id);
    ResponseEntity<ResponseRest<Book>> getBooks();
    ResponseEntity<ResponseRest<Book>> updateFromLTMO();

    ResponseEntity<byte[]> getBookPDF(Long id, Long chapter);
}

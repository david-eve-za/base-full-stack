package gon.cue.basefullstack.dao.mng;

import gon.cue.basefullstack.model.mng.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IBookDao extends JpaRepository<Book, Long> {
    Optional<Book> findByUrl(String url);

//    Book findByUrl(String url);
}


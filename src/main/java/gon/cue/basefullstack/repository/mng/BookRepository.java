package gon.cue.basefullstack.repository.mng;

import gon.cue.basefullstack.entities.mng.Book;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Book} entity.
 */

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    String BOOKS_BY_ID_CACHE = "booksById";

    Optional<Book> findByUrl(String url);

    @EntityGraph(attributePaths = "chapters")
    @Cacheable(cacheNames = BOOKS_BY_ID_CACHE)
    Optional<Book> findOneWithChaptersById(Long id);
}


package gon.cue.basefullstack.services;

import gon.cue.basefullstack.model.Category;
import gon.cue.basefullstack.response.ResponseRest;
import org.springframework.http.ResponseEntity;

public interface ICategoryService {
    ResponseEntity<ResponseRest<Category>> search();

    ResponseEntity<ResponseRest<Category>> searchById(Long id);

    ResponseEntity<ResponseRest<Category>> save(Category category);

    ResponseEntity<ResponseRest<Category>> update(Category category, Long id);

    ResponseEntity<ResponseRest<Category>> delete(Long id);
}

package gon.cue.basefullstack.services;

import gon.cue.basefullstack.model.Category;
import gon.cue.basefullstack.response.ResponseRest;
import org.springframework.http.ResponseEntity;

public interface ICategoryService {
    public ResponseEntity<ResponseRest<Category>> search();

    public ResponseEntity<ResponseRest<Category>> searchById(Long id);

    public ResponseEntity<ResponseRest<Category>> save(Category category);

    public ResponseEntity<ResponseRest<Category>> update(Category category, Long id);

    public ResponseEntity<ResponseRest<Category>> delete(Long id);
}

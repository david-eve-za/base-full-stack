package gon.cue.basefullstack.services;

import gon.cue.basefullstack.model.Category;
import gon.cue.basefullstack.response.ResponseRest;
import org.springframework.http.ResponseEntity;

public interface ICategoryService {
    public ResponseEntity<ResponseRest<Category>> search();
}

package gon.cue.basefullstack.services;

import gon.cue.basefullstack.dao.ICategoryDao;
import gon.cue.basefullstack.model.Category;
import gon.cue.basefullstack.response.ResponseRest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements ICategoryService {
    private final ICategoryDao categoryDao;

    public CategoryServiceImpl(ICategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public ResponseEntity<ResponseRest<Category>> search() {
        ResponseRest<Category> responseRest = new ResponseRest<>();
        try {
            List<Category> categories = (List<Category>) categoryDao.findAll();

            responseRest.setData(categories);
            responseRest.setMetadata("success","200","OK");
        } catch (Exception e) {
            responseRest.setMetadata("error","500","Internal Server Error");
            log.error("Error when try to retrieve all Category: ",e);
            return ResponseEntity.status(500).body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }
}

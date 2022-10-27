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

            if (categories.isEmpty()) {
                responseRest.setMetadata("success", "200", "No categories found");
                return ResponseEntity.ok(responseRest);
            }

            responseRest.setData(categories);
            responseRest.setMetadata("success", "200", "OK");
        } catch (Exception e) {
            responseRest.setMetadata("error", "500", "Internal Server Error");
            log.error("Error when try to retrieve all Category: ", e);
            return ResponseEntity.status(500).body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }

    @Override
    public ResponseEntity<ResponseRest<Category>> searchById(Long id) {
        ResponseRest<Category> responseRest = new ResponseRest<>();
        try {
            Category category = categoryDao.findById(id).orElse(null);

            responseRest.getData().add(category);
            responseRest.setMetadata("success", "200", "OK");
        } catch (Exception e) {
            responseRest.setMetadata("error", "500", "Internal Server Error");
            log.error("Error when try to retrieve Category by id: ", e);
            return ResponseEntity.status(500).body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }

    @Override
    public ResponseEntity<ResponseRest<Category>> save(Category category) {
        ResponseRest<Category> responseRest = new ResponseRest<>();
        try {
            Category categorySaved = categoryDao.save(category);

            responseRest.getData().add(categorySaved);
            responseRest.setMetadata("success", "200", "OK");
        } catch (Exception e) {
            responseRest.setMetadata("error", "500", "Internal Server Error");
            log.error("Error when try to save Category: ", e);
            return ResponseEntity.status(500).body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }

    @Override
    public ResponseEntity<ResponseRest<Category>> update(Category category, Long id) {
        ResponseRest<Category> responseRest = new ResponseRest<>();
        try {
            if (categoryDao.findById(id).isPresent()) {
                category.setId(id);
                Category categoryUpdated = categoryDao.save(category);

                responseRest.getData().add(categoryUpdated);
                responseRest.setMetadata("success", "200", "OK");
            } else {
                responseRest.setMetadata("error", "404", "Category not found");
                return ResponseEntity.status(404).body(responseRest);
            }
        } catch (Exception e) {
            responseRest.setMetadata("error", "500", "Internal Server Error");
            log.error("Error when try to update Category: ", e);
            return ResponseEntity.status(500).body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }

    @Override
    public ResponseEntity<ResponseRest<Category>> delete(Long id) {
        ResponseRest<Category> responseRest = new ResponseRest<>();
        try {
            categoryDao.deleteById(id);

            responseRest.setMetadata("success", "200", "OK");
        } catch (Exception e) {
            responseRest.setMetadata("error", "500", "Internal Server Error");
            log.error("Error when try to delete Category: ", e);
            return ResponseEntity.status(500).body(responseRest);
        }
        return ResponseEntity.ok(responseRest);
    }
}

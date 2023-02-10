package gon.cue.basefullstack.controller;

import gon.cue.basefullstack.model.Category;
import gon.cue.basefullstack.response.ResponseRest;
import gon.cue.basefullstack.services.ICategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:8888"})
public class CategoryRestController {

    private final ICategoryService categoryService;

    public CategoryRestController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public ResponseEntity<ResponseRest<Category>> searchCategories() {
        return categoryService.search();
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ResponseRest<Category>> searchCategoryById(@PathVariable Long id) {
        return categoryService.searchById(id);
    }

    @PostMapping("/categories")
    public ResponseEntity<ResponseRest<Category>> createCategory(@RequestBody Category category) {
        return categoryService.save(category);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ResponseRest<Category>> updateCategory(@RequestBody Category category, @PathVariable Long id) {
        return categoryService.update(category, id);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ResponseRest<Category>> deleteCategory(@PathVariable Long id) {
        return categoryService.delete(id);
    }
}

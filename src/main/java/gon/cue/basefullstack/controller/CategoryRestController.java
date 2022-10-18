package gon.cue.basefullstack.controller;

import gon.cue.basefullstack.model.Category;
import gon.cue.basefullstack.response.ResponseRest;
import gon.cue.basefullstack.services.ICategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CategoryRestController {

    private final ICategoryService categoryService;

    public CategoryRestController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public ResponseEntity<ResponseRest<Category>> searchCategories() {
        return categoryService.search();
    }
}

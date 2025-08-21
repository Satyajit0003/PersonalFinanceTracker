package com.category_service.controller;

import com.category_service.dto.CategoryDto;
import com.category_service.entity.Category;
import com.category_service.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create-category")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto) {
        Category category = categoryService.createCategory(categoryDto);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/update-category/{categoryId}")
    public ResponseEntity<Category> updateCategory(@RequestBody CategoryDto categoryDto,@PathVariable String categoryId){
        Category updatedCategory = categoryService.updateCategory(categoryDto, categoryId);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/delete-category/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok("Category deleted successfully");
    }

    @GetMapping("/get-category/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable String categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/get-all-categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/get-category-by-user/{userId}/{categoryName}")
    public ResponseEntity<Category> getCategoriesByUser(@PathVariable String userId, @PathVariable String categoryName) {
        Category category = categoryService.getCategory(userId, categoryName);
        return ResponseEntity.ok(category);
    }
}

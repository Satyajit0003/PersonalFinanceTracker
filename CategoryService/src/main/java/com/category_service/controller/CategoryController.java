package com.category_service.controller;

import com.category_service.dto.CategoryDto;
import com.category_service.entity.Category;
import com.category_service.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create-category")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto) {
        log.info("Received request to create category: {}", categoryDto);
        Category category = categoryService.createCategory(categoryDto);
        log.info("Category created successfully: {}", category);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/update-category/{categoryId}")
    public ResponseEntity<Category> updateCategory(@RequestBody CategoryDto categoryDto, @PathVariable String categoryId) {
        log.info("Received request to update category with ID: {} and data: {}", categoryId, categoryDto);
        Category updatedCategory = categoryService.updateCategory(categoryDto, categoryId);
        log.info("Category updated successfully: {}", updatedCategory);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/delete-category/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable String categoryId) {
        log.info("Received request to delete category with ID: {}", categoryId);
        categoryService.deleteCategory(categoryId);
        log.info("Category deleted successfully with ID: {}", categoryId);
        return ResponseEntity.ok("Category deleted successfully");
    }

    @GetMapping("/get-category/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable String categoryId) {
        log.info("Received request to fetch category with ID: {}", categoryId);
        Category category = categoryService.getCategoryById(categoryId);
        log.info("Fetched category: {}", category);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/get-all-categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        log.info("Received request to fetch all categories");
        List<Category> categories = categoryService.getCategories();
        log.info("Fetched {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/get-category-by-user/{userId}/{categoryName}")
    public ResponseEntity<Category> getCategoriesByUser(@PathVariable String userId, @PathVariable String categoryName) {
        log.info("Received request to fetch category for userId: {} and categoryName: {}", userId, categoryName);
        Category category = categoryService.getCategory(userId, categoryName);
        log.info("Fetched category: {}", category);
        return ResponseEntity.ok(category);
    }
}

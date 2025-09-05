package com.category_service.service;

import com.category_service.dto.CategoryDto;
import com.category_service.entity.Category;
import com.category_service.exception.AmountNotSufficientException;
import com.category_service.exception.CategoryAlreadyExistsException;
import com.category_service.exception.CategoryNotFoundException;
import com.category_service.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @CachePut(value = "category", key = "#result.id")
    public Category createCategory(CategoryDto categoryDto) {
        log.info("Creating category for user ID: {} with name: {}", categoryDto.getUserId(), categoryDto.getCategory());
        Category categoryOptional = categoryRepository.findByUserIdAndCategoryName(categoryDto.getUserId(), categoryDto.getCategory());
        if (categoryOptional != null) {
            log.error("Category already exists for user ID: {}", categoryDto.getUserId());
            throw new CategoryAlreadyExistsException("Category already exists for user: " + categoryDto.getUserId());
        }
        Category category = new Category();
        category.setUserId(categoryDto.getUserId());
        category.setCategoryName(categoryDto.getCategory().toUpperCase());
        category.setLimitAmount(categoryDto.getLimitAmount());
        categoryRepository.save(category);
        log.info("Category created successfully: {}", category);
        return category;
    }

    @Override
    @Cacheable(value = "category", key = "#categoryId")
    public Category getCategoryById(String categoryId) {
        log.info("Fetching category by ID: {}", categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error("Category not found with ID: {}", categoryId);
            return new CategoryNotFoundException("Category not found with id: " + categoryId);
        });
        log.info("Fetched category: {}", category);
        return category;
    }

    @Override
    @CachePut(value = "category", key = "#categoryId")
    public Category updateCategory(CategoryDto categoryDto, String categoryId) {
        log.info("Updating category with ID: {} and data: {}", categoryId, categoryDto);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error("Category not found with ID: {}", categoryId);
            return new CategoryNotFoundException("Category not found with id: " + categoryId);
        });
        category.setUserId(categoryDto.getUserId());
        category.setCategoryName(categoryDto.getCategory().toUpperCase());
        category.setLimitAmount(categoryDto.getLimitAmount());
        categoryRepository.save(category);
        log.info("Category updated successfully: {}", category);
        return category;
    }

    @Override
    @CacheEvict(value = "category", key = "#categoryId")
    public void deleteCategory(String categoryId) {
        log.info("Deleting category with ID: {}", categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error("Category not found with ID: {}", categoryId);
            return new CategoryNotFoundException("Category not found with id: " + categoryId);
        });
        categoryRepository.delete(category);
        log.info("Category deleted successfully with ID: {}", categoryId);
    }

    @Override
    @Cacheable(value = "allCategories")
    public List<Category> getCategories() {
        log.info("Fetching all categories");
        List<Category> categories = categoryRepository.findAll();
        log.info("Fetched {} categories", categories.size());
        return categories;
    }

    @Override
    @Cacheable(value = "categoryByUserId", key = "#userId")
    public Category getCategory(String userId, String categoryName) {
        log.info("Fetching category for user ID: {} and category name: {}", userId, categoryName);
        Category category = categoryRepository.findByUserIdAndCategoryName(userId, categoryName);
        log.info("Fetched category: {}", category);
        return category;
    }

    @Override
    public void categoryLimitUpdate(String categoryId, double amount) {
        log.info("Updating category limit for category ID: {} with amount: {}", categoryId, amount);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error("Category not found with ID: {}", categoryId);
            return new CategoryNotFoundException("Category not found with id: " + categoryId);
        });
        if (category.getLimitAmount() < amount) {
            log.error("Insufficient category limit for category ID: {}. Available: {}, Required: {}", categoryId, category.getLimitAmount(), amount);
            throw new AmountNotSufficientException("Insufficient category limit");
        }
        category.setLimitAmount(category.getLimitAmount() - amount);
        categoryRepository.save(category);
        log.info("Category limit updated successfully for category ID: {}. New limit: {}", categoryId, category.getLimitAmount());
    }

    @Override
    public void categoryLimitUpdateFail(String categoryId, double amount) {
        log.info("Reverting category limit for category ID: {} by amount: {}", categoryId, amount);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {
            log.error("Category not found with ID: {}", categoryId);
            return new CategoryNotFoundException("Category not found with id: " + categoryId);
        });
        category.setLimitAmount(category.getLimitAmount() + amount);
        categoryRepository.save(category);
        log.info("Category limit reverted successfully for category ID: {}. New limit: {}", categoryId, category.getLimitAmount());
    }
}

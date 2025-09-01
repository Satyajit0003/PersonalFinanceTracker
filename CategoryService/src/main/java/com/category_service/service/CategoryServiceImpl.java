package com.category_service.service;

import com.category_service.dto.CategoryDto;
import com.category_service.entity.Category;
import com.category_service.exception.AmountNotSufficientException;
import com.category_service.exception.CategoryAlreadyExistsException;
import com.category_service.exception.CategoryNotFoundException;
import com.category_service.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category createCategory(CategoryDto categoryDto) {
        Category categoryOptional = categoryRepository.findByUserIdAndCategoryName(categoryDto.getUserId(), categoryDto.getCategory());
        if(categoryOptional != null) {
            throw new CategoryAlreadyExistsException("Category already exists for user: " + categoryDto.getUserId());
        }
        Category category = new Category();
        category.setUserId(categoryDto.getUserId());
        category.setCategoryName(categoryDto.getCategory().toUpperCase());
        category.setLimitAmount(categoryDto.getLimitAmount());
        categoryRepository.save(category);
        return category;
    }

    @Override
    public Category getCategoryById(String categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
    }

    @Override
    public Category updateCategory(CategoryDto categoryDto, String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
        category.setUserId(categoryDto.getUserId());
        category.setCategoryName(categoryDto.getCategory().toUpperCase());
        category.setLimitAmount(categoryDto.getLimitAmount());
        categoryRepository.save(category);
        return category;
    }

    @Override
    public void deleteCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
        categoryRepository.delete(category);
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategory(String userId, String categoryName) {
        return categoryRepository.findByUserIdAndCategoryName(userId, categoryName);
    }

    @Override
    public void categoryLimitUpdate(String categoryId, double amount) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
        if(category.getLimitAmount() < amount) {
            throw new AmountNotSufficientException("Insufficient category limit");
        }
        category.setLimitAmount(category.getLimitAmount() - amount);
        categoryRepository.save(category);
    }

    @Override
    public void categoryLimitUpdateFail(String categoryId, double amount) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
        category.setLimitAmount(category.getLimitAmount() + amount);
        categoryRepository.save(category);
    }
}

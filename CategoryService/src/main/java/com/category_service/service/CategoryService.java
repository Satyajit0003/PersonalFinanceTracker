package com.category_service.service;

import com.category_service.dto.CategoryDto;
import com.category_service.entity.Category;

import java.util.List;

public interface CategoryService {

    Category createCategory(CategoryDto categoryDto);

    Category getCategoryById(String categoryId);

    Category updateCategory(CategoryDto categoryDto, String categoryId);

    void deleteCategory(String categoryId);

    List<Category> getCategories();

    Category getCategory(String userId, String categoryName);

    void categoryLimitUpdate(String categoryId, double amount);

    void categoryLimitUpdateFail(String categoryId, double amount);

}

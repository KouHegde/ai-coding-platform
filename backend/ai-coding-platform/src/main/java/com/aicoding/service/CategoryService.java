package com.aicoding.service;

import java.util.List;

import com.aicoding.dto.response.CategoryResponse;
import com.aicoding.model.Category;
import com.aicoding.model.CategoryType;

public interface CategoryService {
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
    List<CategoryResponse> getCategoriesByType(CategoryType type);
    void deleteCategory(Long id);
    boolean existsByName(String name);
}
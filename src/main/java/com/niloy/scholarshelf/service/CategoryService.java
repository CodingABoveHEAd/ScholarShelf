package com.niloy.scholarshelf.service;

import com.niloy.scholarshelf.dto.request.CategoryRequest;
import com.niloy.scholarshelf.dto.response.CategoryResponse;

import java.util.List;

/**
 * Service interface for category management operations.
 */
public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long categoryId, CategoryRequest request);

    void deleteCategory(Long categoryId);

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long categoryId);
}


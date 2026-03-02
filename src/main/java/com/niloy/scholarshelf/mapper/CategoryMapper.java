package com.niloy.scholarshelf.mapper;

import com.niloy.scholarshelf.dto.response.CategoryResponse;
import com.niloy.scholarshelf.entity.Category;

/**
 * Mapper to convert Category entity to CategoryResponse DTO.
 */
public class CategoryMapper {

    private CategoryMapper() {}

    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .bookCount(category.getBooks() != null ? category.getBooks().size() : 0)
                .build();
    }
}


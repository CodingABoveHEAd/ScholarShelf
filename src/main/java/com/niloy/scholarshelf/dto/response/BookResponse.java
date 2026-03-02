package com.niloy.scholarshelf.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for book information in responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private Double price;
    private String bookCondition;
    private String imageUrl;
    private Boolean available;
    private LocalDateTime createdAt;
    private String sellerName;
    private Long sellerId;
    private String categoryName;
    private Long categoryId;
    private Double averageRating;
    private int reviewCount;
}


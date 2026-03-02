package com.niloy.scholarshelf.mapper;

import com.niloy.scholarshelf.dto.response.ReviewResponse;
import com.niloy.scholarshelf.entity.Review;

/**
 * Mapper to convert Review entity to ReviewResponse DTO.
 */
public class ReviewMapper {

    private ReviewMapper() {}

    public static ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .reviewerName(review.getReviewer() != null ? review.getReviewer().getFullName() : null)
                .reviewerId(review.getReviewer() != null ? review.getReviewer().getId() : null)
                .bookId(review.getBook() != null ? review.getBook().getId() : null)
                .build();
    }
}


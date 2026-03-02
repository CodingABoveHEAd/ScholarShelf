package com.niloy.scholarshelf.service;

import com.niloy.scholarshelf.dto.request.ReviewRequest;
import com.niloy.scholarshelf.dto.response.ReviewResponse;

import java.util.List;

/**
 * Service interface for review operations.
 */
public interface ReviewService {

    ReviewResponse createReview(Long bookId, ReviewRequest request, String reviewerEmail);

    List<ReviewResponse> getReviewsByBook(Long bookId);

    Double getAverageRating(Long bookId);
}


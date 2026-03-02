package com.niloy.scholarshelf.controller.api;

import com.niloy.scholarshelf.dto.request.ReviewRequest;
import com.niloy.scholarshelf.dto.response.ReviewResponse;
import com.niloy.scholarshelf.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for review operations.
 */
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Book review and rating APIs")
public class ReviewApiController {

    private final ReviewService reviewService;

    @PostMapping("/book/{bookId}")
    @Operation(summary = "Submit a review", description = "User submits a review for a book")
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long bookId,
                                                        @Valid @RequestBody ReviewRequest request,
                                                        Authentication authentication) {
        return ResponseEntity.ok(reviewService.createReview(bookId, request, authentication.getName()));
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get book reviews", description = "Returns all reviews for a specific book")
    public ResponseEntity<List<ReviewResponse>> getBookReviews(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBook(bookId));
    }
}


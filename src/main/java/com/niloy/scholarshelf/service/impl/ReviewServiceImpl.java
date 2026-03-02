package com.niloy.scholarshelf.service.impl;

import com.niloy.scholarshelf.dto.request.ReviewRequest;
import com.niloy.scholarshelf.dto.response.ReviewResponse;
import com.niloy.scholarshelf.entity.Book;
import com.niloy.scholarshelf.entity.Review;
import com.niloy.scholarshelf.entity.User;
import com.niloy.scholarshelf.exception.DuplicateResourceException;
import com.niloy.scholarshelf.exception.ResourceNotFoundException;
import com.niloy.scholarshelf.mapper.ReviewMapper;
import com.niloy.scholarshelf.repository.BookRepository;
import com.niloy.scholarshelf.repository.ReviewRepository;
import com.niloy.scholarshelf.repository.UserRepository;
import com.niloy.scholarshelf.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of review service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public ReviewResponse createReview(Long bookId, ReviewRequest request, String reviewerEmail) {
        log.info("Creating review for book {} by user: {}", bookId, reviewerEmail);

        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        if (reviewRepository.existsByReviewerIdAndBookId(reviewer.getId(), bookId)) {
            throw new DuplicateResourceException("You have already reviewed this book");
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .reviewer(reviewer)
                .book(book)
                .build();

        review = reviewRepository.save(review);
        log.info("Review created with id: {}", review.getId());

        return ReviewMapper.toResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByBook(Long bookId) {
        return reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId).stream()
                .map(ReviewMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long bookId) {
        Double avg = reviewRepository.findAverageRatingByBookId(bookId);
        return avg != null ? avg : 0.0;
    }
}


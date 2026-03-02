package com.niloy.scholarshelf.service.impl;

import com.niloy.scholarshelf.dto.response.BookResponse;
import com.niloy.scholarshelf.entity.Book;
import com.niloy.scholarshelf.entity.User;
import com.niloy.scholarshelf.exception.ResourceNotFoundException;
import com.niloy.scholarshelf.mapper.BookMapper;
import com.niloy.scholarshelf.repository.BookRepository;
import com.niloy.scholarshelf.repository.ReviewRepository;
import com.niloy.scholarshelf.repository.UserRepository;
import com.niloy.scholarshelf.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of wishlist service (Many-to-Many: User ↔ Book).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public void addToWishlist(Long bookId, String userEmail) {
        log.info("Adding book {} to wishlist for user: {}", bookId, userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        user.getWishlist().add(book);
        userRepository.save(user);
    }

    @Override
    public void removeFromWishlist(Long bookId, String userEmail) {
        log.info("Removing book {} from wishlist for user: {}", bookId, userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.getWishlist().removeIf(b -> b.getId().equals(bookId));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getWishlist(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return user.getWishlist().stream()
                .map(book -> {
                    BookResponse r = BookMapper.toResponse(book);
                    r.setAverageRating(reviewRepository.findAverageRatingByBookId(book.getId()));
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInWishlist(Long bookId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return user.getWishlist().stream().anyMatch(b -> b.getId().equals(bookId));
    }
}


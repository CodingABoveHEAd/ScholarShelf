package com.niloy.scholarshelf.service;

import com.niloy.scholarshelf.dto.response.BookResponse;

import java.util.List;

/**
 * Service interface for wishlist operations.
 */
public interface WishlistService {

    void addToWishlist(Long bookId, String userEmail);

    void removeFromWishlist(Long bookId, String userEmail);

    List<BookResponse> getWishlist(String userEmail);

    boolean isInWishlist(Long bookId, String userEmail);
}


package com.niloy.scholarshelf.controller.api;

import com.niloy.scholarshelf.dto.response.BookResponse;
import com.niloy.scholarshelf.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for wishlist operations.
 */
@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Buyer wishlist management APIs")
public class WishlistApiController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get wishlist", description = "Returns all books in the buyer's wishlist")
    public ResponseEntity<List<BookResponse>> getWishlist(Authentication authentication) {
        return ResponseEntity.ok(wishlistService.getWishlist(authentication.getName()));
    }

    @PostMapping("/{bookId}")
    @Operation(summary = "Add to wishlist", description = "Adds a book to the buyer's wishlist")
    public ResponseEntity<Void> addToWishlist(@PathVariable Long bookId, Authentication authentication) {
        wishlistService.addToWishlist(bookId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{bookId}")
    @Operation(summary = "Remove from wishlist", description = "Removes a book from the buyer's wishlist")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long bookId, Authentication authentication) {
        wishlistService.removeFromWishlist(bookId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}


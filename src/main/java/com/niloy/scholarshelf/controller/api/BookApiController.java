package com.niloy.scholarshelf.controller.api;

import com.niloy.scholarshelf.dto.request.BookRequest;
import com.niloy.scholarshelf.dto.response.BookResponse;
import com.niloy.scholarshelf.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for book operations.
 */
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book listing and management APIs")
public class BookApiController {

    private final BookService bookService;

    @GetMapping("/list")
    @Operation(summary = "List available books", description = "Returns paginated list of available books")
    public ResponseEntity<Page<BookResponse>> listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(bookService.getAllAvailableBooks(
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search books by keyword and/or category")
    public ResponseEntity<Page<BookResponse>> searchBooks(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(bookService.searchBooks(keyword, categoryId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book details", description = "Returns details of a specific book")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    @Operation(summary = "Create a book listing", description = "Seller creates a new book listing")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(bookService.createBook(request, authentication.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book listing", description = "Seller updates their book listing")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id,
                                                    @Valid @RequestBody BookRequest request,
                                                    Authentication authentication) {
        return ResponseEntity.ok(bookService.updateBook(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book listing", description = "Seller deletes their book listing")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id, Authentication authentication) {
        bookService.deleteBook(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-books")
    @Operation(summary = "Get seller's books", description = "Returns all books listed by the authenticated seller")
    public ResponseEntity<List<BookResponse>> myBooks(Authentication authentication) {
        return ResponseEntity.ok(bookService.getBooksBySeller(authentication.getName()));
    }
}


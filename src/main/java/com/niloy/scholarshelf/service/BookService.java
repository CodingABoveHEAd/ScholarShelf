package com.niloy.scholarshelf.service;

import com.niloy.scholarshelf.dto.request.BookRequest;
import com.niloy.scholarshelf.dto.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for book operations.
 */
public interface BookService {

    BookResponse createBook(BookRequest request, String sellerEmail);

    BookResponse updateBook(Long bookId, BookRequest request, String sellerEmail);

    void deleteBook(Long bookId, String sellerEmail);

    BookResponse getBookById(Long bookId);

    Page<BookResponse> getAllAvailableBooks(Pageable pageable);

    Page<BookResponse> searchBooks(String keyword, Long categoryId, Pageable pageable);

    List<BookResponse> getBooksBySeller(String sellerEmail);

    void adminDeleteBook(Long bookId);
}


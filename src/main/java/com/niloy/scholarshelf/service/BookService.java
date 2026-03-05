package com.niloy.scholarshelf.service;

import com.niloy.scholarshelf.dto.request.BookRequest;
import com.niloy.scholarshelf.dto.response.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

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

    // Admin operations
    List<BookResponse> getAllBooks();

    BookResponse adminCreateBook(BookRequest request, Long sellerId);

    BookResponse adminUpdateBook(Long bookId, BookRequest request);

    void adminDeleteBook(Long bookId);

    /**
     * Get books grouped by writer/author for homepage display.
     * Returns a map of author name -> list of BookResponse (limited per author).
     */
    Map<String, List<BookResponse>> getBooksByWriter(int maxAuthors, int booksPerAuthor);

    /**
     * Get books with the best condition (LIKE_NEW or NEW) for homepage display.
     */
    List<BookResponse> getBooksByBestCondition(int limit);

    /**
     * Get suggested books from the same category, excluding the current book.
     */
    List<BookResponse> getSuggestedBooks(Long bookId, Long categoryId, int limit);
}


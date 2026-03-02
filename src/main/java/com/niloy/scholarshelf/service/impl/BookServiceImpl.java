package com.niloy.scholarshelf.service.impl;

import com.niloy.scholarshelf.dto.request.BookRequest;
import com.niloy.scholarshelf.dto.response.BookResponse;
import com.niloy.scholarshelf.entity.Book;
import com.niloy.scholarshelf.entity.Category;
import com.niloy.scholarshelf.entity.User;
import com.niloy.scholarshelf.enums.BookCondition;
import com.niloy.scholarshelf.exception.ResourceNotFoundException;
import com.niloy.scholarshelf.exception.UnauthorizedException;
import com.niloy.scholarshelf.mapper.BookMapper;
import com.niloy.scholarshelf.repository.BookRepository;
import com.niloy.scholarshelf.repository.CategoryRepository;
import com.niloy.scholarshelf.repository.ReviewRepository;
import com.niloy.scholarshelf.repository.UserRepository;
import com.niloy.scholarshelf.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of book service handling CRUD and search operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public BookResponse createBook(BookRequest request, String sellerEmail) {
        log.info("Creating new book listing by seller: {}", sellerEmail);

        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .description(request.getDescription())
                .price(request.getPrice())
                .bookCondition(BookCondition.valueOf(request.getBookCondition().toUpperCase()))
                .imageUrl(request.getImageUrl())
                .available(true)
                .seller(seller)
                .category(category)
                .build();

        book = bookRepository.save(book);
        log.info("Book created successfully with id: {}", book.getId());

        BookResponse response = BookMapper.toResponse(book);
        response.setAverageRating(0.0);
        return response;
    }

    @Override
    public BookResponse updateBook(Long bookId, BookRequest request, String sellerEmail) {
        log.info("Updating book {} by seller: {}", bookId, sellerEmail);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        if (!book.getSeller().getEmail().equals(sellerEmail)) {
            throw new UnauthorizedException("You are not authorized to update this book");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setBookCondition(BookCondition.valueOf(request.getBookCondition().toUpperCase()));
        book.setImageUrl(request.getImageUrl());
        book.setCategory(category);

        book = bookRepository.save(book);
        BookResponse response = BookMapper.toResponse(book);
        response.setAverageRating(reviewRepository.findAverageRatingByBookId(bookId));
        return response;
    }

    @Override
    public void deleteBook(Long bookId, String sellerEmail) {
        log.info("Deleting book {} by seller: {}", bookId, sellerEmail);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        if (!book.getSeller().getEmail().equals(sellerEmail)) {
            throw new UnauthorizedException("You are not authorized to delete this book");
        }

        bookRepository.delete(book);
        log.info("Book deleted successfully: {}", bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        BookResponse response = BookMapper.toResponse(book);
        response.setAverageRating(reviewRepository.findAverageRatingByBookId(bookId));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getAllAvailableBooks(Pageable pageable) {
        return bookRepository.findByAvailableTrue(pageable)
                .map(book -> {
                    BookResponse r = BookMapper.toResponse(book);
                    r.setAverageRating(reviewRepository.findAverageRatingByBookId(book.getId()));
                    return r;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> searchBooks(String keyword, Long categoryId, Pageable pageable) {
        Page<Book> books;
        if (keyword != null && !keyword.isBlank() && categoryId != null) {
            books = bookRepository.searchBooksByCategory(keyword, categoryId, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            books = bookRepository.searchBooks(keyword, pageable);
        } else if (categoryId != null) {
            books = bookRepository.findByAvailableTrueAndCategoryId(categoryId, pageable);
        } else {
            books = bookRepository.findByAvailableTrue(pageable);
        }

        return books.map(book -> {
            BookResponse r = BookMapper.toResponse(book);
            r.setAverageRating(reviewRepository.findAverageRatingByBookId(book.getId()));
            return r;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getBooksBySeller(String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        return bookRepository.findBySellerId(seller.getId()).stream()
                .map(book -> {
                    BookResponse r = BookMapper.toResponse(book);
                    r.setAverageRating(reviewRepository.findAverageRatingByBookId(book.getId()));
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void adminDeleteBook(Long bookId) {
        log.info("Admin deleting book: {}", bookId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        bookRepository.delete(book);
    }
}


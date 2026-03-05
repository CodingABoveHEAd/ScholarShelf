package com.niloy.scholarshelf.repository;

import com.niloy.scholarshelf.entity.Book;
import com.niloy.scholarshelf.enums.BookCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Book entity operations.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findBySellerId(Long sellerId);

    List<Book> findByCategoryId(Long categoryId);

    Page<Book> findByAvailableTrue(Pageable pageable);

    // Show ALL books (including out-of-stock) for browse listing
    Page<Book> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Book> findByCategory_IdOrderByCreatedAtDesc(Long categoryId, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> searchAllBooks(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "b.category.id = :categoryId")
    Page<Book> searchAllBooksByCategory(@Param("keyword") String keyword,
                                        @Param("categoryId") Long categoryId,
                                        Pageable pageable);

    // Legacy available-only queries kept for exchange logic
    Page<Book> findByAvailableTrueAndCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.available = true AND " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Book> searchAvailableBooks(@Param("keyword") String keyword, Pageable pageable);

    List<Book> findByBookCondition(BookCondition condition);

    long countByAvailableTrue();

    /** Find distinct authors that have available books, ordered by author name. */
    @Query("SELECT DISTINCT b.author FROM Book b WHERE b.available = true ORDER BY b.author")
    List<String> findDistinctAuthorsWithAvailableBooks();

    /** Find top N available books by a given author, ordered by newest first. */
    @Query("SELECT b FROM Book b WHERE b.available = true AND b.author = :author ORDER BY b.createdAt DESC")
    List<Book> findTopAvailableBooksByAuthor(@Param("author") String author, Pageable pageable);

    /** Find available books by condition, ordered by newest first. */
    @Query("SELECT b FROM Book b WHERE b.available = true AND b.bookCondition = :condition ORDER BY b.createdAt DESC")
    List<Book> findAvailableBooksByCondition(@Param("condition") BookCondition condition, Pageable pageable);

    /** Find available books by category, excluding a specific book. */
    @Query("SELECT b FROM Book b WHERE b.available = true AND b.category.id = :categoryId AND b.id <> :excludeBookId ORDER BY b.createdAt DESC")
    List<Book> findSuggestedBooks(@Param("categoryId") Long categoryId, @Param("excludeBookId") Long excludeBookId, Pageable pageable);
}


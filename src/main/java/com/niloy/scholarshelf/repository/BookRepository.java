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

    List<Book> findByBookCondition(BookCondition condition);

    long countByAvailableTrue();
}


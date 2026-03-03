package com.niloy.scholarshelf.repository;

import com.niloy.scholarshelf.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Returns total ordered quantity per book: [bookId, bookTitle, totalOrdered]
     */
    @Query("SELECT oi.book.id, oi.book.title, SUM(oi.orderedQuantity) " +
           "FROM OrderItem oi GROUP BY oi.book.id, oi.book.title ORDER BY SUM(oi.orderedQuantity) DESC")
    List<Object[]> findTotalOrderedPerBook();

    @Query("SELECT COALESCE(SUM(oi.orderedQuantity), 0) FROM OrderItem oi WHERE oi.book.id = :bookId")
    Integer sumOrderedQuantityByBookId(@Param("bookId") Long bookId);
}

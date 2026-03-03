package com.niloy.scholarshelf.repository;

import com.niloy.scholarshelf.entity.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyerIdOrderByOrderDateDesc(Long buyerId);

    @Query("SELECT o FROM Order o JOIN FETCH o.buyer JOIN FETCH o.items i JOIN FETCH i.book ORDER BY o.orderDate DESC")
    List<Order> findAllWithDetails();
}

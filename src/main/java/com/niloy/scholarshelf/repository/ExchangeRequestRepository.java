package com.niloy.scholarshelf.repository;

import com.niloy.scholarshelf.entity.ExchangeRequest;
import com.niloy.scholarshelf.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ExchangeRequest entity operations.
 */
@Repository
public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {

    List<ExchangeRequest> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    @Query("SELECT er FROM ExchangeRequest er WHERE er.book.seller.id = :sellerId ORDER BY er.createdAt DESC")
    List<ExchangeRequest> findBySellerIdOrderByCreatedAtDesc(@Param("sellerId") Long sellerId);

    List<ExchangeRequest> findByBookId(Long bookId);

    List<ExchangeRequest> findByStatus(RequestStatus status);

    long countByStatus(RequestStatus status);

    /** Check if a buyer has an active (PENDING or ACCEPTED) request for a specific book. */
    @Query("SELECT COUNT(er) > 0 FROM ExchangeRequest er WHERE er.buyer.id = :buyerId AND er.book.id = :bookId AND er.status IN :statuses")
    boolean existsByBuyerIdAndBookIdAndStatusIn(@Param("buyerId") Long buyerId, @Param("bookId") Long bookId, @Param("statuses") List<RequestStatus> statuses);
}


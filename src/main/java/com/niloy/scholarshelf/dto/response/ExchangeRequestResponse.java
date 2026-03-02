package com.niloy.scholarshelf.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for exchange request information in responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRequestResponse {
    private Long id;
    private String status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String buyerName;
    private Long buyerId;
    private String bookTitle;
    private Long bookId;
    private String sellerName;
    private Long sellerId;
}


package com.niloy.scholarshelf.mapper;

import com.niloy.scholarshelf.dto.response.ExchangeRequestResponse;
import com.niloy.scholarshelf.entity.ExchangeRequest;

/**
 * Mapper to convert ExchangeRequest entity to ExchangeRequestResponse DTO.
 */
public class ExchangeRequestMapper {

    private ExchangeRequestMapper() {}

    public static ExchangeRequestResponse toResponse(ExchangeRequest request) {
        return ExchangeRequestResponse.builder()
                .id(request.getId())
                .status(request.getStatus().name())
                .message(request.getMessage())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .buyerName(request.getBuyer() != null ? request.getBuyer().getFullName() : null)
                .buyerId(request.getBuyer() != null ? request.getBuyer().getId() : null)
                .bookTitle(request.getBook() != null ? request.getBook().getTitle() : null)
                .bookId(request.getBook() != null ? request.getBook().getId() : null)
                .sellerName(request.getBook() != null && request.getBook().getSeller() != null
                        ? request.getBook().getSeller().getFullName() : null)
                .sellerId(request.getBook() != null && request.getBook().getSeller() != null
                        ? request.getBook().getSeller().getId() : null)
                .build();
    }
}


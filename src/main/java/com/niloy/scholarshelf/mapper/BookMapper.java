package com.niloy.scholarshelf.mapper;

import com.niloy.scholarshelf.dto.response.BookResponse;
import com.niloy.scholarshelf.entity.Book;

/**
 * Mapper to convert Book entity to BookResponse DTO.
 */
public class BookMapper {

    private BookMapper() {}

    public static BookResponse toResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .price(book.getPrice())
                .bookCondition(book.getBookCondition().name())
                .imageUrl(book.getImageUrl())
                .available(book.getAvailable())
                .quantity(book.getQuantity() != null ? book.getQuantity() : 1)
                .createdAt(book.getCreatedAt())
                .sellerName(book.getSeller() != null ? book.getSeller().getFullName() : null)
                .sellerId(book.getSeller() != null ? book.getSeller().getId() : null)
                .categoryName(book.getCategory() != null ? book.getCategory().getName() : null)
                .categoryId(book.getCategory() != null ? book.getCategory().getId() : null)
                .reviewCount(book.getReviews() != null ? book.getReviews().size() : 0)
                .build();
    }
}


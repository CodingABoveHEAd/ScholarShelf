package com.niloy.scholarshelf.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookStockResponse {
    private Long bookId;
    private String bookTitle;
    private Integer stockCount;
    private Boolean available;
    private Integer totalOrdered;
}

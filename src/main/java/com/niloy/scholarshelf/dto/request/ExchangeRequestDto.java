package com.niloy.scholarshelf.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating an exchange request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRequestDto {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @NotBlank(message = "Message is required")
    private String message;
}


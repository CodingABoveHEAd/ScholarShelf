package com.niloy.scholarshelf.controller.api;

import com.niloy.scholarshelf.dto.request.ExchangeRequestDto;
import com.niloy.scholarshelf.dto.response.ExchangeRequestResponse;
import com.niloy.scholarshelf.service.ExchangeRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for exchange request operations.
 */
@RestController
@RequestMapping("/api/v1/exchanges")
@RequiredArgsConstructor
@Tag(name = "Exchange Requests", description = "Book exchange request management APIs")
public class ExchangeApiController {

    private final ExchangeRequestService exchangeRequestService;

    @PostMapping
    @Operation(summary = "Create exchange request", description = "Buyer sends an exchange request for a book")
    public ResponseEntity<ExchangeRequestResponse> createRequest(
            @Valid @RequestBody ExchangeRequestDto request,
            Authentication authentication) {
        return ResponseEntity.ok(exchangeRequestService.createRequest(request, authentication.getName()));
    }

    @GetMapping("/buyer")
    @Operation(summary = "Get buyer's requests", description = "Returns all exchange requests made by the buyer")
    public ResponseEntity<List<ExchangeRequestResponse>> getBuyerRequests(Authentication authentication) {
        return ResponseEntity.ok(exchangeRequestService.getBuyerRequests(authentication.getName()));
    }

    @GetMapping("/seller")
    @Operation(summary = "Get seller's requests", description = "Returns all incoming exchange requests for the seller")
    public ResponseEntity<List<ExchangeRequestResponse>> getSellerRequests(Authentication authentication) {
        return ResponseEntity.ok(exchangeRequestService.getSellerRequests(authentication.getName()));
    }

    @PutMapping("/{id}/accept")
    @Operation(summary = "Accept exchange request", description = "Seller accepts an exchange request")
    public ResponseEntity<ExchangeRequestResponse> acceptRequest(@PathVariable Long id,
                                                                  Authentication authentication) {
        return ResponseEntity.ok(exchangeRequestService.acceptRequest(id, authentication.getName()));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject exchange request", description = "Seller rejects an exchange request")
    public ResponseEntity<ExchangeRequestResponse> rejectRequest(@PathVariable Long id,
                                                                  Authentication authentication) {
        return ResponseEntity.ok(exchangeRequestService.rejectRequest(id, authentication.getName()));
    }
}


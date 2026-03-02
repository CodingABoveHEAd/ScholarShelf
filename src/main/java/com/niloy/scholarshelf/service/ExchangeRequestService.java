package com.niloy.scholarshelf.service;

import com.niloy.scholarshelf.dto.request.ExchangeRequestDto;
import com.niloy.scholarshelf.dto.response.ExchangeRequestResponse;

import java.util.List;

/**
 * Service interface for exchange request operations.
 */
public interface ExchangeRequestService {

    ExchangeRequestResponse createRequest(ExchangeRequestDto request, String buyerEmail);

    ExchangeRequestResponse acceptRequest(Long requestId, String sellerEmail);

    ExchangeRequestResponse rejectRequest(Long requestId, String sellerEmail);

    List<ExchangeRequestResponse> getBuyerRequests(String buyerEmail);

    List<ExchangeRequestResponse> getSellerRequests(String sellerEmail);

    List<ExchangeRequestResponse> getAllRequests();
}


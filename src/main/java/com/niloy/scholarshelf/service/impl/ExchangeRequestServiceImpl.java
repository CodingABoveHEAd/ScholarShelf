package com.niloy.scholarshelf.service.impl;

import com.niloy.scholarshelf.dto.request.ExchangeRequestDto;
import com.niloy.scholarshelf.dto.response.ExchangeRequestResponse;
import com.niloy.scholarshelf.entity.Book;
import com.niloy.scholarshelf.entity.ExchangeRequest;
import com.niloy.scholarshelf.entity.User;
import com.niloy.scholarshelf.enums.RequestStatus;
import com.niloy.scholarshelf.exception.ResourceNotFoundException;
import com.niloy.scholarshelf.exception.UnauthorizedException;
import com.niloy.scholarshelf.mapper.ExchangeRequestMapper;
import com.niloy.scholarshelf.repository.BookRepository;
import com.niloy.scholarshelf.repository.ExchangeRequestRepository;
import com.niloy.scholarshelf.repository.UserRepository;
import com.niloy.scholarshelf.service.ExchangeRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of exchange request service handling request lifecycle.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExchangeRequestServiceImpl implements ExchangeRequestService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public ExchangeRequestResponse createRequest(ExchangeRequestDto request, String buyerEmail) {
        log.info("Creating exchange request for book {} by buyer: {}", request.getBookId(), buyerEmail);

        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.getBookId()));

        if (!book.getAvailable()) {
            throw new IllegalStateException("Book is no longer available");
        }

        ExchangeRequest exchangeRequest = ExchangeRequest.builder()
                .buyer(buyer)
                .book(book)
                .message(request.getMessage())
                .status(RequestStatus.PENDING)
                .build();

        exchangeRequest = exchangeRequestRepository.save(exchangeRequest);
        log.info("Exchange request created with id: {}", exchangeRequest.getId());

        return ExchangeRequestMapper.toResponse(exchangeRequest);
    }

    @Override
    public ExchangeRequestResponse acceptRequest(Long requestId, String sellerEmail) {
        log.info("Accepting exchange request {} by seller: {}", requestId, sellerEmail);

        ExchangeRequest request = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange request not found"));

        if (!request.getBook().getSeller().getEmail().equals(sellerEmail)) {
            throw new UnauthorizedException("You are not authorized to accept this request");
        }

        request.setStatus(RequestStatus.ACCEPTED);
        request.getBook().setAvailable(false);
        bookRepository.save(request.getBook());

        request = exchangeRequestRepository.save(request);
        return ExchangeRequestMapper.toResponse(request);
    }

    @Override
    public ExchangeRequestResponse rejectRequest(Long requestId, String sellerEmail) {
        log.info("Rejecting exchange request {} by seller: {}", requestId, sellerEmail);

        ExchangeRequest request = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange request not found"));

        if (!request.getBook().getSeller().getEmail().equals(sellerEmail)) {
            throw new UnauthorizedException("You are not authorized to reject this request");
        }

        request.setStatus(RequestStatus.REJECTED);
        request = exchangeRequestRepository.save(request);
        return ExchangeRequestMapper.toResponse(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExchangeRequestResponse> getBuyerRequests(String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));

        return exchangeRequestRepository.findByBuyerIdOrderByCreatedAtDesc(buyer.getId())
                .stream()
                .map(ExchangeRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExchangeRequestResponse> getSellerRequests(String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        return exchangeRequestRepository.findBySellerIdOrderByCreatedAtDesc(seller.getId())
                .stream()
                .map(ExchangeRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExchangeRequestResponse> getAllRequests() {
        return exchangeRequestRepository.findAll().stream()
                .map(ExchangeRequestMapper::toResponse)
                .collect(Collectors.toList());
    }
}


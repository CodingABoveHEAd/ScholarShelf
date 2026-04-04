package com.niloy.scholarshelf.service.impl;

import com.niloy.scholarshelf.dto.request.OrderRequest;
import com.niloy.scholarshelf.dto.response.BookStockResponse;
import com.niloy.scholarshelf.dto.response.OrderItemResponse;
import com.niloy.scholarshelf.dto.response.OrderResponse;
import com.niloy.scholarshelf.entity.Book;
import com.niloy.scholarshelf.entity.Order;
import com.niloy.scholarshelf.entity.OrderItem;
import com.niloy.scholarshelf.entity.User;
import com.niloy.scholarshelf.enums.OrderStatus;
import com.niloy.scholarshelf.exception.ResourceNotFoundException;
import com.niloy.scholarshelf.repository.BookRepository;
import com.niloy.scholarshelf.repository.OrderItemRepository;
import com.niloy.scholarshelf.repository.OrderRepository;
import com.niloy.scholarshelf.repository.UserRepository;
import com.niloy.scholarshelf.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    public OrderResponse placeOrder(OrderRequest request, String buyerEmail) {
        log.info("Placing order for buyer: {}, bookId: {}, qty: {}", buyerEmail, request.getBookId(), request.getQuantity());

        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.getBookId()));

        // Validate stock
        int stock = book.getQuantity() != null ? book.getQuantity() : 0;
        if (!book.getAvailable() || stock < 1) {
            throw new IllegalStateException("Book is not available for order.");
        }
        if (request.getQuantity() > stock) {
            throw new IllegalStateException("Requested quantity (" + request.getQuantity() +
                    ") exceeds available stock (" + stock + ").");
        }

        // Build order with PENDING status (stock not yet deducted until confirmation)
        BigDecimal unitPrice = BigDecimal.valueOf(book.getPrice());
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .buyer(buyer)
                .totalPrice(subtotal)
                .status(OrderStatus.PENDING)
                .build();

        OrderItem item = OrderItem.builder()
                .order(order)
                .book(book)
                .orderedQuantity(request.getQuantity())
                .unitPrice(unitPrice)
                .build();

        order.getItems().add(item);
        order = orderRepository.save(order);

        log.info("Order created with PENDING status, id: {}", order.getId());
        return toResponse(order);
    }

    @Override
    public OrderResponse sellerAcceptOrder(Long orderId, String sellerEmail) {
        log.info("Seller accepting order {} by user {}", orderId, sellerEmail);

        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        boolean isCorrespondingSeller = order.getItems().stream()
                .anyMatch(item -> item.getBook() != null
                        && item.getBook().getSeller() != null
                        && item.getBook().getSeller().getId().equals(seller.getId()));

        if (!isCorrespondingSeller) {
            throw new IllegalStateException("Only the corresponding seller can accept this order.");
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalStateException("Only pending orders can be accepted. Current status: " + order.getStatus());
        }

        // Deduct stock for modern PENDING orders; legacy PLACED orders may already have stock applied.
        if (order.getStatus() == OrderStatus.PENDING) {
            deductStock(order);
        }

        order.setStatus(OrderStatus.ACCEPTED);
        order = orderRepository.save(order);
        log.info("Order {} accepted successfully by seller", orderId);
        return toResponse(order);
    }

    @Override
    public OrderResponse adminAcceptOrder(Long orderId) {
        log.info("Admin accepting order {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalStateException("Order cannot be accepted. Current status: " + order.getStatus());
        }

        // Deduct stock if it was still PENDING
        if (order.getStatus() == OrderStatus.PENDING) {
            deductStock(order);
        }

        order.setStatus(OrderStatus.ACCEPTED);
        order = orderRepository.save(order);
        log.info("Order {} accepted by admin", orderId);
        return toResponse(order);
    }

    @Override
    public OrderResponse cancelOrder(Long orderId, String userEmail) {
        log.info("Cancelling order {} by user {}", orderId, userEmail);

        User buyer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getBuyer().getId().equals(buyer.getId())) {
            throw new IllegalStateException("You can only cancel your own orders.");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be cancelled by the buyer. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        log.info("Order {} cancelled by buyer", orderId);
        return toResponse(order);
    }

    @Override
    public OrderResponse adminCancelOrder(Long orderId) {
        log.info("Admin cancelling order {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled.");
        }

        // Restore stock if it was already deducted
        if (order.getStatus() == OrderStatus.ACCEPTED || order.getStatus() == OrderStatus.PLACED) {
            restoreStock(order);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        log.info("Order {} cancelled by admin", orderId);
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return toResponse(order);
    }

    private void deductStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Book book = item.getBook();
            int stock = book.getQuantity() != null ? book.getQuantity() : 0;
            if (item.getOrderedQuantity() > stock) {
                throw new IllegalStateException("Insufficient stock for book: " + book.getTitle());
            }
            int newStock = stock - item.getOrderedQuantity();
            book.setQuantity(newStock);
            if (newStock <= 0) {
                book.setAvailable(false);
            }
            bookRepository.save(book);
        }
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Book book = item.getBook();
            int stock = book.getQuantity() != null ? book.getQuantity() : 0;
            book.setQuantity(stock + item.getOrderedQuantity());
            book.setAvailable(true);
            bookRepository.save(book);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getBuyerOrders(String buyerEmail) {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));
        return orderRepository.findByBuyerIdOrderByOrderDateDesc(buyer.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllWithDetails()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookStockResponse> getStockReport() {
        // Get all books with their total ordered quantities
        List<Object[]> ordered = orderItemRepository.findTotalOrderedPerBook();
        // Build a map bookId -> totalOrdered
        java.util.Map<Long, Integer> orderedMap = new java.util.HashMap<>();
        for (Object[] row : ordered) {
            Long bookId = (Long) row[0];
            Long total = (Long) row[2];
            orderedMap.put(bookId, total.intValue());
        }

        return bookRepository.findAll().stream().map(book -> BookStockResponse.builder()
                .bookId(book.getId())
                .bookTitle(book.getTitle())
                .stockCount(book.getQuantity() != null ? book.getQuantity() : 0)
                .available(book.getAvailable())
                .totalOrdered(orderedMap.getOrDefault(book.getId(), 0))
                .build()
        ).collect(Collectors.toList());
    }

    // ---- mapping helpers ----

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems() == null ? new ArrayList<>() :
                order.getItems().stream().map(this::toItemResponse).collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .buyerId(order.getBuyer() != null ? order.getBuyer().getId() : null)
                .buyerName(order.getBuyer() != null ? order.getBuyer().getFullName() : null)
                .buyerEmail(order.getBuyer() != null ? order.getBuyer().getEmail() : null)
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .items(itemResponses)
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getOrderedQuantity()));
        return OrderItemResponse.builder()
                .id(item.getId())
                .bookId(item.getBook() != null ? item.getBook().getId() : null)
                .bookTitle(item.getBook() != null ? item.getBook().getTitle() : null)
                .bookAuthor(item.getBook() != null ? item.getBook().getAuthor() : null)
                .bookImageUrl(item.getBook() != null ? item.getBook().getImageUrl() : null)
                .orderedQuantity(item.getOrderedQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(subtotal)
                .build();
    }
}

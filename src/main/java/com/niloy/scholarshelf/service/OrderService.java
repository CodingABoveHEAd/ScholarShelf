package com.niloy.scholarshelf.service;

import com.niloy.scholarshelf.dto.request.OrderRequest;
import com.niloy.scholarshelf.dto.response.BookStockResponse;
import com.niloy.scholarshelf.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    /**
     * Place an order for a given book+quantity by the authenticated buyer.
     * Decrements stock transactionally.
     */
    OrderResponse placeOrder(OrderRequest request, String buyerEmail);

    /**
     * Get all orders placed by a buyer (for buyer dashboard).
     */
    List<OrderResponse> getBuyerOrders(String buyerEmail);

    /**
     * Get all orders (for admin).
     */
    List<OrderResponse> getAllOrders();

    /**
     * Get per-book stock report (admin): stock remaining and total ordered.
     */
    List<BookStockResponse> getStockReport();

    /**
     * Seller accepts/approves an order that contains at least one of their listed books.
     */
    OrderResponse sellerAcceptOrder(Long orderId, String sellerEmail);

    /**
     * Admin accepts/approves any order.
     */
    OrderResponse adminAcceptOrder(Long orderId);

    /**
     * Cancel an order by buyer. Cancellation is allowed only while order is pending.
     */
    OrderResponse cancelOrder(Long orderId, String userEmail);

    /**
     * Admin cancels any order.
     */
    OrderResponse adminCancelOrder(Long orderId);

    /**
     * Get a single order by ID (for checkout review page).
     */
    OrderResponse getOrderById(Long orderId);
}

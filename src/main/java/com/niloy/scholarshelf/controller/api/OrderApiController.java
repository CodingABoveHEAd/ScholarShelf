package com.niloy.scholarshelf.controller.api;

import com.niloy.scholarshelf.dto.request.OrderRequest;
import com.niloy.scholarshelf.dto.response.BookStockResponse;
import com.niloy.scholarshelf.dto.response.OrderResponse;
import com.niloy.scholarshelf.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    /**
     * POST /api/orders — place an order (BUYER only)
     */
    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication) {
        OrderResponse response = orderService.placeOrder(request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders/my — buyer's own orders
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<List<OrderResponse>> myOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getBuyerOrders(authentication.getName()));
    }

    /**
     * GET /api/admin/orders — all orders (ADMIN only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> allOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * GET /api/admin/stock-report — per-book stock report (ADMIN only)
     */
    @GetMapping("/admin/stock-report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookStockResponse>> stockReport() {
        return ResponseEntity.ok(orderService.getStockReport());
    }

    /**
     * POST /api/orders/{id}/accept — buyer confirms their own pending order
     */
    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<OrderResponse> acceptOrder(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(orderService.acceptOrder(id, authentication.getName()));
    }

    /**
     * POST /api/orders/{id}/cancel — buyer cancels their own order
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(orderService.cancelOrder(id, authentication.getName()));
    }

    /**
     * POST /api/admin/orders/{id}/accept — admin accepts any order
     */
    @PostMapping("/admin/{id}/accept")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> adminAcceptOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.adminAcceptOrder(id));
    }

    /**
     * POST /api/admin/orders/{id}/cancel — admin cancels any order
     */
    @PostMapping("/admin/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> adminCancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.adminCancelOrder(id));
    }
}

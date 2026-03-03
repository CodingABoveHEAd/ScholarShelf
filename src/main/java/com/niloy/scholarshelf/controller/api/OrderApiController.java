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
}

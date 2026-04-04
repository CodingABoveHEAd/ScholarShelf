package com.niloy.scholarshelf.controller.web;

import com.niloy.scholarshelf.dto.request.OrderRequest;
import com.niloy.scholarshelf.dto.response.BookResponse;
import com.niloy.scholarshelf.dto.response.OrderResponse;
import com.niloy.scholarshelf.service.BookService;
import com.niloy.scholarshelf.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderWebController {

    private final OrderService orderService;
    private final BookService bookService;

    // ---- Buyer pages ----

    @GetMapping("/buyer/orders")
    public String buyerOrders(Authentication authentication, Model model) {
        model.addAttribute("orders", orderService.getBuyerOrders(authentication.getName()));
        return "buyer/orders";
    }

    /**
     * Step 1: Show checkout/review page instead of immediately placing order.
     */
    @PostMapping("/buyer/orders/checkout")
    public String showCheckout(@RequestParam Long bookId,
                               @RequestParam Integer quantity,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            BookResponse book = bookService.getBookById(bookId);
            model.addAttribute("book", book);
            model.addAttribute("quantity", quantity);
            double totalPrice = book.getPrice() * quantity;
            model.addAttribute("totalPrice", totalPrice);
            return "buyer/checkout";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/books/" + bookId;
        }
    }

    /**
     * Step 2: Actually place the order (creates with PENDING status).
     */
    @PostMapping("/buyer/orders/place")
    public String placeOrder(@RequestParam Long bookId,
                             @RequestParam Integer quantity,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            OrderRequest request = new OrderRequest(bookId, quantity);
            OrderResponse order = orderService.placeOrder(request, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Order #" + order.getId() + " created! Please confirm it to complete your purchase.");
            return "redirect:/buyer/orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/books/" + bookId;
        }
    }

    /**
     * Buyer cancels their own order.
     */
    @PostMapping("/buyer/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            orderService.cancelOrder(orderId, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Order #" + orderId + " has been cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/buyer/orders";
    }

    // ---- Admin pages ----

    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("stockReport", orderService.getStockReport());
        return "admin/orders";
    }

    /**
     * Admin accepts/approves an order.
     */
    @PostMapping("/admin/orders/{orderId}/accept")
    public String adminAcceptOrder(@PathVariable Long orderId,
                                   RedirectAttributes redirectAttributes) {
        try {
            orderService.adminAcceptOrder(orderId);
            redirectAttributes.addFlashAttribute("success", "Order #" + orderId + " has been accepted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    /**
     * Admin cancels any order.
     */
    @PostMapping("/admin/orders/{orderId}/cancel")
    public String adminCancelOrder(@PathVariable Long orderId,
                                   RedirectAttributes redirectAttributes) {
        try {
            orderService.adminCancelOrder(orderId);
            redirectAttributes.addFlashAttribute("success", "Order #" + orderId + " has been cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }
}

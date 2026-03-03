package com.niloy.scholarshelf.controller.web;

import com.niloy.scholarshelf.dto.request.OrderRequest;
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

    // ---- Buyer pages ----

    @GetMapping("/buyer/orders")
    public String buyerOrders(Authentication authentication, Model model) {
        model.addAttribute("orders", orderService.getBuyerOrders(authentication.getName()));
        return "buyer/orders";
    }

    @PostMapping("/buyer/orders/place")
    public String placeOrder(@RequestParam Long bookId,
                             @RequestParam Integer quantity,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            OrderRequest request = new OrderRequest(bookId, quantity);
            orderService.placeOrder(request, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
            return "redirect:/buyer/orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/books/" + bookId;
        }
    }

    // ---- Admin pages ----

    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("stockReport", orderService.getStockReport());
        return "admin/orders";
    }
}

package com.niloy.scholarshelf.controller.web;

import com.niloy.scholarshelf.dto.request.ExchangeRequestDto;
import com.niloy.scholarshelf.service.ExchangeRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for exchange request management.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ExchangeWebController {

    private final ExchangeRequestService exchangeRequestService;

    /** Buyer: Submit an exchange request for a book. */
    @PostMapping("/buyer/exchange/request")
    public String createRequest(@ModelAttribute ExchangeRequestDto requestDto,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            exchangeRequestService.createRequest(requestDto, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Exchange request sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/books/" + requestDto.getBookId();
    }

    /** Buyer: View all exchange requests made by the buyer. */
    @GetMapping("/buyer/exchanges")
    public String buyerExchanges(Authentication authentication, Model model) {
        model.addAttribute("requests", exchangeRequestService.getBuyerRequests(authentication.getName()));
        return "exchange/buyer-list";
    }

    /** Seller: View all incoming exchange requests. */
    @GetMapping("/seller/exchanges")
    public String sellerExchanges(Authentication authentication, Model model) {
        model.addAttribute("requests", exchangeRequestService.getSellerRequests(authentication.getName()));
        return "exchange/seller-list";
    }

    /** Seller: Accept an exchange request. */
    @PostMapping("/seller/exchanges/{id}/accept")
    public String acceptRequest(@PathVariable Long id,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            exchangeRequestService.acceptRequest(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Request accepted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seller/exchanges";
    }

    /** Seller: Reject an exchange request. */
    @PostMapping("/seller/exchanges/{id}/reject")
    public String rejectRequest(@PathVariable Long id,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            exchangeRequestService.rejectRequest(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Request rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seller/exchanges";
    }
}


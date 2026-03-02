package com.niloy.scholarshelf.controller.web;

import com.niloy.scholarshelf.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for buyer wishlist management.
 */
@Controller
@RequestMapping("/buyer/wishlist")
@RequiredArgsConstructor
public class WishlistWebController {

    private final WishlistService wishlistService;

    @GetMapping
    public String viewWishlist(Authentication authentication, Model model) {
        model.addAttribute("books", wishlistService.getWishlist(authentication.getName()));
        return "wishlist/list";
    }

    @PostMapping("/add/{bookId}")
    public String addToWishlist(@PathVariable Long bookId,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        wishlistService.addToWishlist(bookId, authentication.getName());
        redirectAttributes.addFlashAttribute("success", "Book added to wishlist!");
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/remove/{bookId}")
    public String removeFromWishlist(@PathVariable Long bookId,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        wishlistService.removeFromWishlist(bookId, authentication.getName());
        redirectAttributes.addFlashAttribute("success", "Book removed from wishlist.");
        return "redirect:/buyer/wishlist";
    }
}


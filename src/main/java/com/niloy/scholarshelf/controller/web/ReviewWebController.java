package com.niloy.scholarshelf.controller.web;

import com.niloy.scholarshelf.dto.request.ReviewRequest;
import com.niloy.scholarshelf.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for submitting book reviews.
 */
@Controller
@RequiredArgsConstructor
public class ReviewWebController {

    private final ReviewService reviewService;

    @PostMapping("/books/{bookId}/reviews")
    public String submitReview(@PathVariable Long bookId,
                               @Valid @ModelAttribute ReviewRequest reviewRequest,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            reviewService.createReview(bookId, reviewRequest, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Review submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/books/" + bookId;
    }
}


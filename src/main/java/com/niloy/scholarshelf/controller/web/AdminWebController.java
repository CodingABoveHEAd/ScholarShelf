package com.niloy.scholarshelf.controller.web;

import com.niloy.scholarshelf.dto.request.CategoryRequest;
import com.niloy.scholarshelf.repository.BookRepository;
import com.niloy.scholarshelf.repository.ExchangeRequestRepository;
import com.niloy.scholarshelf.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for admin dashboard and management.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminWebController {

    private final UserService userService;
    private final BookService bookService;
    private final CategoryService categoryService;
    private final ExchangeRequestService exchangeRequestService;
    private final BookRepository bookRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;

    /** Admin dashboard with platform statistics. */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.getTotalUsers());
        model.addAttribute("totalBooks", bookRepository.count());
        model.addAttribute("availableBooks", bookRepository.countByAvailableTrue());
        model.addAttribute("totalRequests", exchangeRequestRepository.count());
        model.addAttribute("buyerCount", userService.getUserCountByRole("BUYER"));
        model.addAttribute("sellerCount", userService.getUserCountByRole("SELLER"));
        return "admin/dashboard";
    }

    /** Admin: Manage all users. */
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    /** Admin: Toggle user active/inactive. */
    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.toggleUserActive(id);
        redirectAttributes.addFlashAttribute("success", "User status updated!");
        return "redirect:/admin/users";
    }

    /** Admin: Change user role. */
    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable Long id,
                             @RequestParam String role,
                             RedirectAttributes redirectAttributes) {
        userService.changeUserRole(id, role);
        redirectAttributes.addFlashAttribute("success", "User role updated!");
        return "redirect:/admin/users";
    }

    /** Admin: Manage categories. */
    @GetMapping("/categories")
    public String manageCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("categoryRequest", new CategoryRequest());
        return "admin/categories";
    }

    /** Admin: Create category. */
    @PostMapping("/categories")
    public String createCategory(@Valid @ModelAttribute CategoryRequest categoryRequest,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/categories";
        }

        try {
            categoryService.createCategory(categoryRequest);
            redirectAttributes.addFlashAttribute("success", "Category created!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    /** Admin: Delete category. */
    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Category deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    /** Admin: Delete a reported/inappropriate book. */
    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.adminDeleteBook(id);
        redirectAttributes.addFlashAttribute("success", "Book removed!");
        return "redirect:/admin/dashboard";
    }

    /** Admin: View all exchange transactions. */
    @GetMapping("/transactions")
    public String viewTransactions(Model model) {
        model.addAttribute("requests", exchangeRequestService.getAllRequests());
        return "admin/transactions";
    }
}


package com.niloy.scholarshelf.controller.web;

import com.niloy.scholarshelf.dto.request.BookRequest;
import com.niloy.scholarshelf.dto.request.CategoryRequest;
import com.niloy.scholarshelf.dto.response.BookResponse;
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
import org.springframework.web.multipart.MultipartFile;
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
    private final ImageUploadService imageUploadService;
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

    // ─────────────────────────────────────────────────
    //  BOOK MANAGEMENT
    // ─────────────────────────────────────────────────

    /** Admin: List all books. */
    @GetMapping("/books")
    public String manageBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "admin/books";
    }

    /** Admin: Show form to create a new book. */
    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("bookRequest", new BookRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("sellers", userService.getUsersByRole("SELLER"));
        model.addAttribute("editing", false);
        return "admin/book-form";
    }

    /** Admin: Create a new book. */
    @PostMapping("/books/new")
    public String createBook(@Valid @ModelAttribute BookRequest bookRequest,
                             BindingResult bindingResult,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             @RequestParam(value = "sellerId", required = false) Long sellerId,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors() || sellerId == null) {
            if (sellerId == null) {
                model.addAttribute("sellerError", "Please select a seller.");
            }
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("sellers", userService.getUsersByRole("SELLER"));
            model.addAttribute("editing", false);
            return "admin/book-form";
        }
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                bookRequest.setImageUrl(imageUploadService.storeBookImage(imageFile));
            }
            bookService.adminCreateBook(bookRequest, sellerId);
            redirectAttributes.addFlashAttribute("success", "Book created successfully!");
        } catch (Exception e) {
            log.error("Admin create book error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/books";
    }

    /** Admin: Show form to edit a book. */
    @GetMapping("/books/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model) {
        BookResponse book = bookService.getBookById(id);
        BookRequest bookRequest = BookRequest.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .price(book.getPrice())
                .bookCondition(book.getBookCondition())
                .categoryId(book.getCategoryId())
                .imageUrl(book.getImageUrl())
                .quantity(book.getQuantity() != null ? book.getQuantity() : 1)
                .build();
        model.addAttribute("bookRequest", bookRequest);
        model.addAttribute("bookId", id);
        model.addAttribute("existingImageUrl", book.getImageUrl());
        model.addAttribute("sellerId", book.getSellerId());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("sellers", userService.getUsersByRole("SELLER"));
        model.addAttribute("editing", true);
        return "admin/book-form";
    }

    /** Admin: Update a book. */
    @PostMapping("/books/{id}/edit")
    public String updateBook(@PathVariable Long id,
                             @Valid @ModelAttribute BookRequest bookRequest,
                             BindingResult bindingResult,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             @RequestParam(value = "existingImageUrl", required = false) String existingImageUrl,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookId", id);
            model.addAttribute("existingImageUrl", existingImageUrl);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("sellers", userService.getUsersByRole("SELLER"));
            model.addAttribute("editing", true);
            return "admin/book-form";
        }
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // Delete old image if it was previously uploaded
                if (existingImageUrl != null && existingImageUrl.startsWith("/uploads/")) {
                    imageUploadService.deleteImage(existingImageUrl);
                }
                bookRequest.setImageUrl(imageUploadService.storeBookImage(imageFile));
            } else {
                // keep existing image
                bookRequest.setImageUrl(existingImageUrl);
            }
            bookService.adminUpdateBook(id, bookRequest);
            redirectAttributes.addFlashAttribute("success", "Book updated successfully!");
        } catch (Exception e) {
            log.error("Admin update book error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/books";
    }

    /** Admin: Delete a book. */
    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.adminDeleteBook(id);
            redirectAttributes.addFlashAttribute("success", "Book removed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/books";
    }

    // ─────────────────────────────────────────────────
    //  CATEGORY MANAGEMENT
    // ─────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────
    //  TRANSACTIONS
    // ─────────────────────────────────────────────────

    /** Admin: View all exchange transactions. */
    @GetMapping("/transactions")
    public String viewTransactions(Model model) {
        model.addAttribute("requests", exchangeRequestService.getAllRequests());
        return "admin/transactions";
    }
}


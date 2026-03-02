package com.niloy.scholarshelf.controller.web;

import com.niloy.scholarshelf.dto.request.BookRequest;
import com.niloy.scholarshelf.dto.response.BookResponse;
import com.niloy.scholarshelf.dto.response.ReviewResponse;
import com.niloy.scholarshelf.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Web controller for book browsing, detail view, and seller book management.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class BookWebController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;
    private final WishlistService wishlistService;

    /** Public: Browse all available books with search, filter, and pagination. */
    @GetMapping("/books")
    public String listBooks(@RequestParam(defaultValue = "") String keyword,
                            @RequestParam(required = false) Long categoryId,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "12") int size,
                            Model model) {
        Page<BookResponse> books = bookService.searchBooks(
                keyword, categoryId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));

        model.addAttribute("books", books);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        return "books/list";
    }

    /** Public: View book details with reviews. */
    @GetMapping("/books/{id}")
    public String bookDetail(@PathVariable Long id, Model model, Authentication authentication) {
        BookResponse book = bookService.getBookById(id);
        List<ReviewResponse> reviews = reviewService.getReviewsByBook(id);

        model.addAttribute("book", book);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", reviewService.getAverageRating(id));

        if (authentication != null) {
            boolean inWishlist = wishlistService.isInWishlist(id, authentication.getName());
            model.addAttribute("inWishlist", inWishlist);
        }

        return "books/detail";
    }

    /** Seller: Show form to create a new book. */
    @GetMapping("/seller/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("bookRequest", new BookRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books/form";
    }

    /** Seller: Create a new book listing. */
    @PostMapping("/seller/books")
    public String createBook(@Valid @ModelAttribute BookRequest bookRequest,
                             BindingResult bindingResult,
                             Authentication authentication,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "books/form";
        }

        try {
            bookService.createBook(bookRequest, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Book listed successfully!");
            return "redirect:/seller/books";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "books/form";
        }
    }

    /** Seller: List all books by the current seller. */
    @GetMapping("/seller/books")
    public String sellerBooks(Authentication authentication, Model model) {
        model.addAttribute("books", bookService.getBooksBySeller(authentication.getName()));
        return "books/seller-list";
    }

    /** Seller: Show form to edit an existing book. */
    @GetMapping("/seller/books/{id}/edit")
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
                .build();

        model.addAttribute("bookRequest", bookRequest);
        model.addAttribute("bookId", id);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("editing", true);
        return "books/form";
    }

    /** Seller: Update an existing book. */
    @PostMapping("/seller/books/{id}")
    public String updateBook(@PathVariable Long id,
                             @Valid @ModelAttribute BookRequest bookRequest,
                             BindingResult bindingResult,
                             Authentication authentication,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookId", id);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("editing", true);
            return "books/form";
        }

        try {
            bookService.updateBook(id, bookRequest, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Book updated successfully!");
            return "redirect:/seller/books";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("bookId", id);
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("editing", true);
            return "books/form";
        }
    }

    /** Seller: Delete a book listing. */
    @PostMapping("/seller/books/{id}/delete")
    public String deleteBook(@PathVariable Long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        bookService.deleteBook(id, authentication.getName());
        redirectAttributes.addFlashAttribute("success", "Book deleted successfully!");
        return "redirect:/seller/books";
    }
}


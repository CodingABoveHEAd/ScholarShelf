package com.niloy.scholarshelf.controller.web;

import com.niloy.scholarshelf.dto.response.BookResponse;
import com.niloy.scholarshelf.service.BookService;
import com.niloy.scholarshelf.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * Controller for the home/landing page.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        Page<BookResponse> featuredBooks = bookService.getAllAvailableBooks(
                PageRequest.of(0, 8, Sort.by("createdAt").descending()));
        model.addAttribute("books", featuredBooks.getContent());
        model.addAttribute("categories", categoryService.getAllCategories());

        // Books by Writer: up to 6 authors, 4 books each
        Map<String, List<BookResponse>> booksByWriter = bookService.getBooksByWriter(6, 4);
        model.addAttribute("booksByWriter", booksByWriter);

        // Books by Best Condition: up to 8 books
        List<BookResponse> booksByCondition = bookService.getBooksByBestCondition(8);
        model.addAttribute("booksByCondition", booksByCondition);

        return "home";
    }
}


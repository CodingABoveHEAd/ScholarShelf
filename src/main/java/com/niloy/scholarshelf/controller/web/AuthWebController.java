package com.niloy.scholarshelf.controller.web;

import com.niloy.scholarshelf.dto.request.LoginRequest;
import com.niloy.scholarshelf.dto.request.RegisterRequest;
import com.niloy.scholarshelf.dto.response.AuthResponse;
import com.niloy.scholarshelf.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Web controller for authentication (login and registration forms).
 * JWT token is stored in an HTTP-only cookie.
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthWebController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginRequest loginRequest,
                        BindingResult bindingResult,
                        HttpServletResponse response,
                        Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        try {
            AuthResponse authResponse = authService.login(loginRequest);
            setJwtCookie(response, authResponse.getToken());

            log.info("User logged in: {} with role: {}", authResponse.getEmail(), authResponse.getRole());

            return switch (authResponse.getRole()) {
                case "ADMIN" -> "redirect:/admin/dashboard";
                case "SELLER" -> "redirect:/seller/books";
                default -> "redirect:/books";
            };
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            model.addAttribute("error", "Invalid email or password");
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest,
                           BindingResult bindingResult,
                           HttpServletResponse response,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            AuthResponse authResponse = authService.register(registerRequest);
            setJwtCookie(response, authResponse.getToken());

            log.info("User registered: {} with role: {}", authResponse.getEmail(), authResponse.getRole());

            return switch (authResponse.getRole()) {
                case "ADMIN" -> "redirect:/admin/dashboard";
                case "SELLER" -> "redirect:/seller/books";
                default -> "redirect:/books";
            };
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/auth/login?logout";
    }

    /** Set JWT token in an HTTP-only cookie. */
    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400); // 24 hours
        response.addCookie(cookie);
    }
}


package com.niloy.scholarshelf.controller.web;

import com.niloy.scholarshelf.dto.request.MessageRequest;
import com.niloy.scholarshelf.service.MessageService;
import com.niloy.scholarshelf.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller for messaging/chat between users.
 */
@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageWebController {

    private final MessageService messageService;
    private final UserService userService;

    /** View inbox - list of chat partners. */
    @GetMapping
    public String inbox(Authentication authentication, Model model) {
        model.addAttribute("chatPartners", messageService.getChatPartners(authentication.getName()));
        model.addAttribute("unreadCount", messageService.getUnreadCount(authentication.getName()));
        return "messages/inbox";
    }

    /** View conversation with a specific user. */
    @GetMapping("/{userId}")
    public String conversation(@PathVariable Long userId,
                               Authentication authentication,
                               Model model) {
        model.addAttribute("messages", messageService.getConversation(userId, authentication.getName()));
        model.addAttribute("otherUser", userService.getUserById(userId));
        model.addAttribute("messageRequest", new MessageRequest());
        return "messages/conversation";
    }

    /** Send a message to a user. */
    @PostMapping("/{userId}")
    public String sendMessage(@PathVariable Long userId,
                              @Valid @ModelAttribute MessageRequest messageRequest,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            messageService.sendMessage(userId, messageRequest, authentication.getName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/messages/" + userId;
    }
}


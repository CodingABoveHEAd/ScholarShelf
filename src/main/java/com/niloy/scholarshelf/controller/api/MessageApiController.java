package com.niloy.scholarshelf.controller.api;

import com.niloy.scholarshelf.dto.request.MessageRequest;
import com.niloy.scholarshelf.dto.response.MessageResponse;
import com.niloy.scholarshelf.dto.response.UserResponse;
import com.niloy.scholarshelf.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for messaging operations.
 */
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Chat/messaging APIs between buyers and sellers")
public class MessageApiController {

    private final MessageService messageService;

    @PostMapping("/{receiverId}")
    @Operation(summary = "Send message", description = "Sends a message to another user")
    public ResponseEntity<MessageResponse> sendMessage(@PathVariable Long receiverId,
                                                        @Valid @RequestBody MessageRequest request,
                                                        Authentication authentication) {
        return ResponseEntity.ok(messageService.sendMessage(receiverId, request, authentication.getName()));
    }

    @GetMapping("/conversation/{userId}")
    @Operation(summary = "Get conversation", description = "Returns the conversation with a specific user")
    public ResponseEntity<List<MessageResponse>> getConversation(@PathVariable Long userId,
                                                                  Authentication authentication) {
        return ResponseEntity.ok(messageService.getConversation(userId, authentication.getName()));
    }

    @GetMapping("/partners")
    @Operation(summary = "Get chat partners", description = "Returns list of users the current user has chatted with")
    public ResponseEntity<List<UserResponse>> getChatPartners(Authentication authentication) {
        return ResponseEntity.ok(messageService.getChatPartners(authentication.getName()));
    }
}


package com.niloy.scholarshelf.service;

import com.niloy.scholarshelf.dto.request.MessageRequest;
import com.niloy.scholarshelf.dto.response.MessageResponse;
import com.niloy.scholarshelf.dto.response.UserResponse;

import java.util.List;

/**
 * Service interface for messaging/chat operations.
 */
public interface MessageService {

    MessageResponse sendMessage(Long receiverId, MessageRequest request, String senderEmail);

    List<MessageResponse> getConversation(Long otherUserId, String currentUserEmail);

    List<UserResponse> getChatPartners(String userEmail);

    long getUnreadCount(String userEmail);
}


package com.niloy.scholarshelf.service.impl;

import com.niloy.scholarshelf.dto.request.MessageRequest;
import com.niloy.scholarshelf.dto.response.MessageResponse;
import com.niloy.scholarshelf.dto.response.UserResponse;
import com.niloy.scholarshelf.entity.Message;
import com.niloy.scholarshelf.entity.User;
import com.niloy.scholarshelf.exception.ResourceNotFoundException;
import com.niloy.scholarshelf.mapper.MessageMapper;
import com.niloy.scholarshelf.mapper.UserMapper;
import com.niloy.scholarshelf.repository.MessageRepository;
import com.niloy.scholarshelf.repository.UserRepository;
import com.niloy.scholarshelf.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of messaging service (Buyer ↔ Seller chat).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public MessageResponse sendMessage(Long receiverId, MessageRequest request, String senderEmail) {
        log.info("Sending message from {} to user {}", senderEmail, receiverId);

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with id: " + receiverId));

        Message message = Message.builder()
                .content(request.getContent())
                .sender(sender)
                .receiver(receiver)
                .isRead(false)
                .build();

        message = messageRepository.save(message);
        log.info("Message sent with id: {}", message.getId());

        return MessageMapper.toResponse(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getConversation(Long otherUserId, String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return messageRepository.findConversation(currentUser.getId(), otherUserId).stream()
                .map(MessageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getChatPartners(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return messageRepository.findChatPartners(user.getId()).stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return messageRepository.countUnreadMessages(user.getId());
    }
}


package com.niloy.scholarshelf.mapper;

import com.niloy.scholarshelf.dto.response.MessageResponse;
import com.niloy.scholarshelf.entity.Message;

/**
 * Mapper to convert Message entity to MessageResponse DTO.
 */
public class MessageMapper {

    private MessageMapper() {}

    public static MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .isRead(message.getIsRead())
                .senderName(message.getSender() != null ? message.getSender().getFullName() : null)
                .senderId(message.getSender() != null ? message.getSender().getId() : null)
                .receiverName(message.getReceiver() != null ? message.getReceiver().getFullName() : null)
                .receiverId(message.getReceiver() != null ? message.getReceiver().getId() : null)
                .build();
    }
}


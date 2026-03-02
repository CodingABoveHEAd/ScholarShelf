package com.niloy.scholarshelf.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for message/chat information in responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private String content;
    private LocalDateTime sentAt;
    private Boolean isRead;
    private String senderName;
    private Long senderId;
    private String receiverName;
    private Long receiverId;
}


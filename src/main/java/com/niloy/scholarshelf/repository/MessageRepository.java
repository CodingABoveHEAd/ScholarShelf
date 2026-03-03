package com.niloy.scholarshelf.repository;

import com.niloy.scholarshelf.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Message entity operations (Buyer ↔ Seller chat).
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /** Get the conversation between two users, ordered chronologically. */
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("userId1") Long userId1,
                                   @Param("userId2") Long userId2);

    /** Get distinct users who have exchanged messages with the given user. */
    @Query("SELECT DISTINCT u FROM User u WHERE " +
            "u.id IN (SELECT m.sender.id FROM Message m WHERE m.receiver.id = :userId) " +
            "OR u.id IN (SELECT m.receiver.id FROM Message m WHERE m.sender.id = :userId)")
    List<com.niloy.scholarshelf.entity.User> findChatPartners(@Param("userId") Long userId);

    /** Count unread messages for a user. */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false")
    long countUnreadMessages(@Param("userId") Long userId);
}


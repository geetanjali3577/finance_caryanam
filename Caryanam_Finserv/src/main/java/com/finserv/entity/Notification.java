// ==========================================
// Notification Entity
// ==========================================

package com.finserv.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;

    private Long receiverId;

    private String senderRole;

    private String receiverRole;

    private String message;

    private boolean isRead = false;

    private LocalDateTime createdAt;
}

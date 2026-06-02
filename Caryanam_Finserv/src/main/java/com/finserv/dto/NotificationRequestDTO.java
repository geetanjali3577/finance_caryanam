// ==========================================
// Notification DTO
// ==========================================

package com.finserv.dto;

import lombok.Data;

@Data
public class NotificationRequestDTO {

    private Long senderId;
    private Long receiverId;

    private String senderRole;
    private String receiverRole;

    private String message;
}
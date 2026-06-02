// ==========================================
// Notification Service
// ==========================================

package com.finserv.service;

import com.finserv.dto.NotificationRequestDTO;
import com.finserv.entity.Notification;

import java.util.List;

public interface NotificationService {

    Notification sendNotification(NotificationRequestDTO dto);

    List<Notification> getUserNotifications(Long receiverId);

    Notification markAsRead(Long notificationId);
}

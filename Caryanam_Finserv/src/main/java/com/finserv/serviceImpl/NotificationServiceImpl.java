// ==========================================
// Notification Service Impl
// ==========================================

package com.finserv.serviceImpl;

import com.finserv.dto.NotificationRequestDTO;
import com.finserv.entity.Notification;
import com.finserv.repository.NotificationRepository;
import com.finserv.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification sendNotification(NotificationRequestDTO dto) {

        Notification notification = Notification.builder()
                .senderId(dto.getSenderId())
                .receiverId(dto.getReceiverId())
                .senderRole(dto.getSenderRole())
                .receiverRole(dto.getReceiverRole())
                .message(dto.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUserNotifications(Long receiverId) {

        return notificationRepository
                .findByReceiverIdOrderByCreatedAtDesc(receiverId);
    }

    @Override
    public Notification markAsRead(Long notificationId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);

        return notificationRepository.save(notification);
    }
}

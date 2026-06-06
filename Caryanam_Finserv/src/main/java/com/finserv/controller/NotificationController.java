

package com.finserv.controller;

import com.finserv.dto.NotificationRequestDTO;
import com.finserv.entity.Notification;
import com.finserv.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin("*")
public class NotificationController {
     @Autowired
    private NotificationService notificationService;

     @PostMapping("/send")
    public Notification sendNotification(
            @RequestBody NotificationRequestDTO dto) {

        return notificationService.sendNotification(dto);
    }

    @GetMapping("/{receiverId}")
    public List<Notification> getNotifications(
            @PathVariable Long receiverId) {

        return notificationService.getUserNotifications(receiverId);
    }

    @PutMapping("/read/{notificationId}")
    public Notification markAsRead(
            @PathVariable Long notificationId) {

        return notificationService.markAsRead(notificationId);
    }
}

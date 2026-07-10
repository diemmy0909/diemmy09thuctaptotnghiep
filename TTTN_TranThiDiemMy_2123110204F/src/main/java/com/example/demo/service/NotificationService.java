package com.example.demo.service;

import com.example.demo.entity.Notification;
import com.example.demo.entity.NotificationType;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void createNotification(User user, String title, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .build();
        notificationRepository.save(notification);
    }
    
    public void createNotificationForUser(Long userId, String title, String message, NotificationType type) {
        userRepository.findById(userId).ifPresent(user -> createNotification(user, title, message, type));
    }

    public void createGlobalNotification(String title, String message, NotificationType type) {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            createNotification(user, title, message, type);
        }
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (notification.getUser().getId().equals(userId)) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        });
    }
    
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().filter(n -> !n.isRead()).toList();
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }
}

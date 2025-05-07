package com.pharmacy.inventory.service;

import com.pharmacy.inventory.model.Notification;
import com.pharmacy.inventory.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationService {
    List<Notification> getAllNotifications();
    Optional<Notification> getNotificationById(UUID id);
    List<Notification> getNotificationsByUser(UUID userId);
    List<Notification> getUnreadNotificationsByUser(UUID userId);
    List<Notification> getNotificationsByType(Notification.NotificationType type);
    List<Notification> getRecentNotificationsByUser(UUID userId);
    Notification createNotification(Notification notification);
    boolean markNotificationAsRead(UUID id);
    int markAllNotificationsAsRead(UUID userId);
    void deleteNotification(UUID id);
    int deleteAllNotificationsForUser(UUID userId);
    void generateLowStockNotifications();
    void generateExpiryNotifications();
}

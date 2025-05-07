package com.pharmacy.inventory.repository;

import com.pharmacy.inventory.model.Notification;
import com.pharmacy.inventory.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUser(User user);
    List<Notification> findByUserAndIsRead(User user, Boolean isRead);
    List<Notification> findByType(Notification.NotificationType type);
    List<Notification> findTop10ByUserOrderByCreatedAtDesc(User user);
}

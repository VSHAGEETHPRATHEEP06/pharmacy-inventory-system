package com.pharmacy.inventory.service.impl;

import com.pharmacy.inventory.model.Batch;
import com.pharmacy.inventory.model.Notification;
import com.pharmacy.inventory.model.Stock;
import com.pharmacy.inventory.model.User;
import com.pharmacy.inventory.repository.NotificationRepository;
import com.pharmacy.inventory.repository.StockRepository;
import com.pharmacy.inventory.repository.UserRepository;
import com.pharmacy.inventory.service.BatchService;
import com.pharmacy.inventory.service.NotificationService;
import com.pharmacy.inventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final StockService stockService;
    private final BatchService batchService;
    private final StockRepository stockRepository;
    
    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                UserRepository userRepository,
                                StockService stockService,
                                BatchService batchService,
                                StockRepository stockRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.stockService = stockService;
        this.batchService = batchService;
        this.stockRepository = stockRepository;
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Optional<Notification> getNotificationById(UUID id) {
        return notificationRepository.findById(id);
    }

    @Override
    public List<Notification> getNotificationsByUser(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(notificationRepository::findByUser).orElse(List.of());
    }

    @Override
    public List<Notification> getUnreadNotificationsByUser(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(u -> notificationRepository.findByUserAndIsRead(u, false)).orElse(List.of());
    }

    @Override
    public List<Notification> getNotificationsByType(Notification.NotificationType type) {
        return notificationRepository.findByType(type);
    }

    @Override
    public List<Notification> getRecentNotificationsByUser(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(notificationRepository::findTop10ByUserOrderByCreatedAtDesc).orElse(List.of());
    }

    @Override
    @Transactional
    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public boolean markNotificationAsRead(UUID id) {
        Optional<Notification> notification = notificationRepository.findById(id);
        
        if (notification.isPresent()) {
            Notification notif = notification.get();
            notif.setIsRead(true);
            notificationRepository.save(notif);
            return true;
        }
        
        return false;
    }

    @Override
    @Transactional
    public int markAllNotificationsAsRead(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        
        if (user.isPresent()) {
            List<Notification> notifications = notificationRepository.findByUserAndIsRead(user.get(), false);
            
            for (Notification notification : notifications) {
                notification.setIsRead(true);
                notificationRepository.save(notification);
            }
            
            return notifications.size();
        }
        
        return 0;
    }

    @Override
    @Transactional
    public void deleteNotification(UUID id) {
        notificationRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public int deleteAllNotificationsForUser(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        
        if (user.isPresent()) {
            List<Notification> notifications = notificationRepository.findByUser(user.get());
            int count = notifications.size();
            notificationRepository.deleteAll(notifications);
            return count;
        }
        
        return 0;
    }

    @Override
    @Transactional
    public void generateLowStockNotifications() {
        List<Stock> lowStockItems = stockService.getLowStockItems();
        List<User> adminUsers = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().name().equals("ROLE_ADMIN") || 
                                          role.getName().name().equals("ROLE_PHARMACIST") ||
                                          role.getName().name().equals("ROLE_MANAGER")))
                .toList();
        
        for (Stock stock : lowStockItems) {
            String message = "Low stock alert for medicine: " + stock.getMedicine().getName() + 
                            ". Current quantity: " + stock.getCurrentQuantity();
            
            for (User user : adminUsers) {
                Notification notification = new Notification();
                notification.setMessage(message);
                notification.setType(Notification.NotificationType.LOW_STOCK);
                notification.setUser(user);
                createNotification(notification);
            }
        }
    }

    @Override
    @Transactional
    public void generateExpiryNotifications() {
        List<Batch> expiringBatches = batchService.findBatchesExpiringInOneMonth();
        List<User> adminUsers = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().name().equals("ROLE_ADMIN") || 
                                          role.getName().name().equals("ROLE_PHARMACIST") ||
                                          role.getName().name().equals("ROLE_MANAGER")))
                .toList();
        
        for (Batch batch : expiringBatches) {
            String message = "Expiry alert for medicine: " + batch.getMedicine().getName() + 
                            ", Batch: " + batch.getBatchNumber() + 
                            ". Expiring on: " + batch.getExpiryDate();
            
            for (User user : adminUsers) {
                Notification notification = new Notification();
                notification.setMessage(message);
                notification.setType(Notification.NotificationType.EXPIRY_WARNING);
                notification.setUser(user);
                createNotification(notification);
            }
        }
    }
}

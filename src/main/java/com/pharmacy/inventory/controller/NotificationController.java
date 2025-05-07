package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.dto.request.NotificationRequest;
import com.pharmacy.inventory.dto.response.MessageResponse;
import com.pharmacy.inventory.model.Notification;
import com.pharmacy.inventory.model.User;
import com.pharmacy.inventory.repository.UserRepository;
import com.pharmacy.inventory.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification Management", description = "APIs for managing notifications in the pharmacy inventory system")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    
    @Autowired
    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Get all notifications", description = "Retrieves a list of all notifications in the system")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID", description = "Retrieves notification details by its unique identifier")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Notification> getNotificationById(@PathVariable UUID id) {
        return notificationService.getNotificationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications by user", description = "Retrieves all notifications for a specific user")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'SALESPERSON', 'MANAGER')")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "Get unread notifications by user", description = "Retrieves all unread notifications for a specific user")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'SALESPERSON', 'MANAGER')")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUser(userId));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get notifications by type", description = "Retrieves all notifications of a specific type")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'MANAGER')")
    public ResponseEntity<List<Notification>> getNotificationsByType(
            @PathVariable Notification.NotificationType type) {
        return ResponseEntity.ok(notificationService.getNotificationsByType(type));
    }

    @GetMapping("/user/{userId}/recent")
    @Operation(summary = "Get recent notifications by user", description = "Retrieves the 10 most recent notifications for a specific user")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'SALESPERSON', 'MANAGER')")
    public ResponseEntity<List<Notification>> getRecentNotificationsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getRecentNotificationsByUser(userId));
    }

    @PostMapping
    @Operation(summary = "Create a new notification", description = "Creates a new notification in the system")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'MANAGER')")
    public ResponseEntity<?> createNotification(@Valid @RequestBody NotificationRequest notificationRequest) {
        try {
            // Validate user exists
            Optional<User> userOpt = userRepository.findById(notificationRequest.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("User not found"));
            }
            
            // Create notification entity
            Notification notification = new Notification();
            notification.setMessage(notificationRequest.getMessage());
            notification.setType(notificationRequest.getType());
            notification.setUser(userOpt.get());
            notification.setIsRead(false);
            
            // Save notification
            Notification savedNotification = notificationService.createNotification(notification);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNotification);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Failed to create notification: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'SALESPERSON', 'MANAGER')")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable UUID id) {
        boolean updated = notificationService.markNotificationAsRead(id);
        if (updated) {
            return ResponseEntity.ok(new MessageResponse("Notification marked as read successfully"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Notification not found with ID: " + id));
    }

    @PatchMapping("/user/{userId}/read-all")
    @Operation(summary = "Mark all notifications as read", description = "Marks all notifications for a specific user as read")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'SALESPERSON', 'MANAGER')")
    public ResponseEntity<?> markAllNotificationsAsRead(@PathVariable UUID userId) {
        int count = notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok(new MessageResponse(count + " notifications marked as read successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Removes a notification from the system")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> deleteNotification(@PathVariable UUID id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok(new MessageResponse("Notification deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to delete notification: " + e.getMessage()));
        }
    }

    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Delete all notifications for user", description = "Removes all notifications for a specific user")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAllNotificationsForUser(@PathVariable UUID userId) {
        int count = notificationService.deleteAllNotificationsForUser(userId);
        return ResponseEntity.ok(new MessageResponse(count + " notifications deleted successfully"));
    }

    @PostMapping("/generate/low-stock")
    @Operation(summary = "Generate low stock notifications", description = "Generates notifications for items with low stock")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'MANAGER')")
    public ResponseEntity<?> generateLowStockNotifications() {
        notificationService.generateLowStockNotifications();
        return ResponseEntity.ok(new MessageResponse("Low stock notifications generated successfully"));
    }

    @PostMapping("/generate/expiry")
    @Operation(summary = "Generate expiry notifications", description = "Generates notifications for medicines nearing expiry")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'MANAGER')")
    public ResponseEntity<?> generateExpiryNotifications() {
        notificationService.generateExpiryNotifications();
        return ResponseEntity.ok(new MessageResponse("Expiry notifications generated successfully"));
    }
}

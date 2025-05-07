package com.pharmacy.inventory.dto.request;

import com.pharmacy.inventory.model.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class NotificationRequest {
    
    @NotBlank(message = "Message is required")
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;
    
    @NotNull(message = "Notification type is required")
    private Notification.NotificationType type;
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Notification.NotificationType getType() {
        return type;
    }
    
    public void setType(Notification.NotificationType type) {
        this.type = type;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}

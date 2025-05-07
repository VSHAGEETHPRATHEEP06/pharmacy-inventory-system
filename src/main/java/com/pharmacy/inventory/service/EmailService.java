package com.pharmacy.inventory.service;

import java.io.File;
import java.util.List;

public interface EmailService {
    /**
     * Send a simple text email
     * 
     * @param to recipient email address
     * @param subject email subject
     * @param text email text content
     */
    void sendSimpleMessage(String to, String subject, String text);
    
    /**
     * Send a simple text email to multiple recipients
     * 
     * @param to list of recipient email addresses
     * @param subject email subject
     * @param text email text content
     */
    void sendSimpleMessage(List<String> to, String subject, String text);
    
    /**
     * Send an email with HTML content
     * 
     * @param to recipient email address
     * @param subject email subject
     * @param htmlContent email HTML content
     */
    void sendHtmlMessage(String to, String subject, String htmlContent);
    
    /**
     * Send an email with attachment
     * 
     * @param to recipient email address
     * @param subject email subject
     * @param text email text content
     * @param attachment file to be attached
     * @param attachmentName name of the attachment
     */
    void sendMessageWithAttachment(String to, String subject, String text, File attachment, String attachmentName);
    
    /**
     * Send low stock notification email
     * 
     * @param to recipient email address
     * @param medicineName medicine name
     * @param currentQuantity current stock quantity
     */
    void sendLowStockNotification(String to, String medicineName, int currentQuantity);
    
    /**
     * Send expiry notification email
     * 
     * @param to recipient email address
     * @param medicineName medicine name
     * @param batchNumber batch number
     * @param expiryDate expiry date
     */
    void sendExpiryNotification(String to, String medicineName, String batchNumber, String expiryDate);
}

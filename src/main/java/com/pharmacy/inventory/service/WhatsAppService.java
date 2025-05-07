package com.pharmacy.inventory.service;

public interface WhatsAppService {
    /**
     * Send a WhatsApp message to a phone number
     * 
     * @param to the recipient's phone number in E.164 format (e.g., +1234567890)
     * @param message the message to send
     * @return true if the message was sent successfully, false otherwise
     */
    boolean sendMessage(String to, String message);
    
    /**
     * Send a low stock alert via WhatsApp
     * 
     * @param to the recipient's phone number in E.164 format
     * @param medicineName the name of the medicine
     * @param currentQuantity the current quantity in stock
     * @return true if the message was sent successfully, false otherwise
     */
    boolean sendLowStockAlert(String to, String medicineName, int currentQuantity);
    
    /**
     * Send an expiry alert via WhatsApp
     * 
     * @param to the recipient's phone number in E.164 format
     * @param medicineName the name of the medicine
     * @param batchNumber the batch number
     * @param expiryDate the expiry date
     * @return true if the message was sent successfully, false otherwise
     */
    boolean sendExpiryAlert(String to, String medicineName, String batchNumber, String expiryDate);
    
    /**
     * Send a delivery notification via WhatsApp
     * 
     * @param to the recipient's phone number in E.164 format
     * @param orderReference the order reference number
     * @return true if the message was sent successfully, false otherwise
     */
    boolean sendDeliveryNotification(String to, String orderReference);
}

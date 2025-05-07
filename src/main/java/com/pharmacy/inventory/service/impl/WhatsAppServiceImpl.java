package com.pharmacy.inventory.service.impl;

import com.pharmacy.inventory.service.WhatsAppService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppServiceImpl implements WhatsAppService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppServiceImpl.class);

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.number}")
    private String fromNumber;

    private void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    @Override
    public boolean sendMessage(String to, String messageBody) {
        try {
            initTwilio();
            
            // Format to WhatsApp format
            String formattedFrom = "whatsapp:" + fromNumber;
            String formattedTo = "whatsapp:" + to;
            
            Message message = Message.creator(
                    new PhoneNumber(formattedTo),
                    new PhoneNumber(formattedFrom),
                    messageBody)
                    .create();
            
            logger.info("WhatsApp message sent with SID: {}", message.getSid());
            return true;
        } catch (Exception e) {
            logger.error("Failed to send WhatsApp message: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendLowStockAlert(String to, String medicineName, int currentQuantity) {
        String message = """
                *LOW STOCK ALERT*
                
                Medicine: %s
                Current Quantity: %d
                
                Please take necessary action to restock this item.
                
                -Pharmacy Inventory System
                """.formatted(medicineName, currentQuantity);
        
        return sendMessage(to, message);
    }

    @Override
    public boolean sendExpiryAlert(String to, String medicineName, String batchNumber, String expiryDate) {
        String message = """
                *EXPIRY ALERT*
                
                Medicine: %s
                Batch Number: %s
                Expiry Date: %s
                
                Please take necessary action to manage this expiring stock.
                
                -Pharmacy Inventory System
                """.formatted(medicineName, batchNumber, expiryDate);
        
        return sendMessage(to, message);
    }

    @Override
    public boolean sendDeliveryNotification(String to, String orderReference) {
        String message = """
                *DELIVERY NOTIFICATION*
                
                Your order with reference number %s has been delivered.
                
                Please confirm receipt.
                
                -Pharmacy Inventory System
                """.formatted(orderReference);
        
        return sendMessage(to, message);
    }
}

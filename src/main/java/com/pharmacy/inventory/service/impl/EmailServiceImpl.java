package com.pharmacy.inventory.service.impl;

import com.pharmacy.inventory.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    
    @Value("${spring.mail.username}")
    private String senderEmail;
    
    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void sendSimpleMessage(List<String> to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void sendHtmlMessage(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates html content
            
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

    @Override
    public void sendMessageWithAttachment(String to, String subject, String text, File attachment, String attachmentName) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            
            FileSystemResource file = new FileSystemResource(attachment);
            helper.addAttachment(attachmentName, file);
            
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email with attachment", e);
        }
    }

    @Override
    public void sendLowStockNotification(String to, String medicineName, int currentQuantity) {
        String subject = "Low Stock Alert: " + medicineName;
        String htmlContent = """
                <div style="font-family: Arial, sans-serif; padding: 20px; background-color: #f8f9fa; border-radius: 5px;">
                    <h2 style="color: #dc3545;">Low Stock Alert</h2>
                    <p>This is to inform you that the following medicine is running low in stock:</p>
                    <div style="background-color: #fff; padding: 15px; border-radius: 5px; margin: 15px 0; border-left: 4px solid #dc3545;">
                        <p><strong>Medicine:</strong> %s</p>
                        <p><strong>Current Quantity:</strong> %d</p>
                    </div>
                    <p>Please take necessary action to restock this item.</p>
                    <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="color: #6c757d; font-size: 12px;">This is an automated message from the Pharmacy Inventory Management System.</p>
                </div>
                """.formatted(medicineName, currentQuantity);
        
        sendHtmlMessage(to, subject, htmlContent);
    }

    @Override
    public void sendExpiryNotification(String to, String medicineName, String batchNumber, String expiryDate) {
        String subject = "Expiry Alert: " + medicineName;
        String htmlContent = """
                <div style="font-family: Arial, sans-serif; padding: 20px; background-color: #f8f9fa; border-radius: 5px;">
                    <h2 style="color: #ffc107;">Expiry Alert</h2>
                    <p>This is to inform you that the following medicine is approaching its expiry date:</p>
                    <div style="background-color: #fff; padding: 15px; border-radius: 5px; margin: 15px 0; border-left: 4px solid #ffc107;">
                        <p><strong>Medicine:</strong> %s</p>
                        <p><strong>Batch Number:</strong> %s</p>
                        <p><strong>Expiry Date:</strong> %s</p>
                    </div>
                    <p>Please take necessary action to manage this expiring stock.</p>
                    <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="color: #6c757d; font-size: 12px;">This is an automated message from the Pharmacy Inventory Management System.</p>
                </div>
                """.formatted(medicineName, batchNumber, expiryDate);
        
        sendHtmlMessage(to, subject, htmlContent);
    }
}

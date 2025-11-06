package com.sqool.sqoolbus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Email service for sending OTP and other notifications
 * This is a basic implementation that logs emails instead of actually sending them
 * In production, this should be replaced with actual email service integration
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    /**
     * Send simple email (basic implementation for development)
     * In production, replace with actual email service implementation
     */
    public void sendSimpleEmail(String to, String subject, String body) {
        // For development purposes, just log the email
        // In production, integrate with email service like SendGrid, AWS SES, etc.
        
        logger.info("=== EMAIL SENT ===");
        logger.info("To: {}", to);
        logger.info("Subject: {}", subject);
        logger.info("Body: {}", body);
        logger.info("==================");
        
        // TODO: Implement actual email sending
        // Example with Spring Boot Mail Starter:
        /*
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@sqoolbus.com");
            
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
        */
    }
    
    /**
     * Send HTML email (placeholder implementation)
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        logger.info("=== HTML EMAIL SENT ===");
        logger.info("To: {}", to);
        logger.info("Subject: {}", subject);
        logger.info("HTML Body: {}", htmlBody);
        logger.info("=======================");
        
        // TODO: Implement actual HTML email sending
    }
}
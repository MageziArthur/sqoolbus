package com.sqool.sqoolbus.service;

import com.sqool.sqoolbus.dto.OtpResponse;
import com.sqool.sqoolbus.tenant.entity.UserOtp;
import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.repository.UserOtpRepository;
import com.sqool.sqoolbus.tenant.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for managing OTP (One-Time Password) operations
 */
@Service
@Transactional
public class OtpService {
    
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    
    private static final int DEFAULT_OTP_LENGTH = 6;
    private static final int DEFAULT_EXPIRY_MINUTES = 5;
    private static final int MAX_OTP_GENERATION_PER_HOUR = 5;
    private static final int MAX_FAILED_ATTEMPTS_PER_HOUR = 10;
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    @Autowired
    private UserOtpRepository otpRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Generate OTP for user
     */
    public OtpResponse generateOtp(String username, UserOtp.OtpType otpType, 
                                   UserOtp.DeliveryMethod deliveryMethod, 
                                   String deliveryDestination, String clientIp, String userAgent) {
        
        try {
            // Find user
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                logger.warn("OTP generation attempted for non-existent user: {}", username);
                return OtpResponse.error("User not found");
            }
            
            User user = userOptional.get();
            
            // Check if user is active
            if (!user.getIsActive()) {
                logger.warn("OTP generation attempted for inactive user: {}", username);
                return OtpResponse.error("User account is not active");
            }
            
            // Rate limiting - check if user has exceeded OTP generation limit
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            long recentOtpCount = otpRepository.countOtpsGeneratedSince(user, otpType, oneHourAgo);
            
            if (recentOtpCount >= MAX_OTP_GENERATION_PER_HOUR) {
                logger.warn("User {} exceeded OTP generation rate limit. Count: {}", username, recentOtpCount);
                return OtpResponse.error("Rate limit exceeded. Please try again later.");
            }
            
            // Check failed attempts rate limit
            long failedAttempts = otpRepository.countFailedAttemptsForUserSince(user, otpType, oneHourAgo);
            if (failedAttempts >= MAX_FAILED_ATTEMPTS_PER_HOUR) {
                logger.warn("User {} exceeded failed attempts rate limit. Count: {}", username, failedAttempts);
                return OtpResponse.error("Too many failed attempts. Please try again later.");
            }
            
            // Determine delivery destination if not provided
            if (deliveryDestination == null) {
                deliveryDestination = getDefaultDeliveryDestination(user, deliveryMethod);
                if (deliveryDestination == null) {
                    return OtpResponse.error("No delivery destination available for selected method");
                }
            }
            
            // Invalidate existing unused OTPs for the same type
            int invalidatedCount = otpRepository.invalidateAllOtpsForUserAndType(user, otpType);
            if (invalidatedCount > 0) {
                logger.info("Invalidated {} existing OTPs for user {} and type {}", 
                           invalidatedCount, username, otpType);
            }
            
            // Generate new OTP
            String otpCode = generateOtpCode(DEFAULT_OTP_LENGTH);
            
            // Create OTP entity
            UserOtp otp = new UserOtp(user, otpCode, otpType, deliveryMethod, 
                                     deliveryDestination, DEFAULT_EXPIRY_MINUTES);
            otp.setClientIp(clientIp);
            otp.setUserAgent(userAgent);
            
            // Save OTP
            otp = otpRepository.save(otp);
            
            // Send OTP asynchronously
            sendOtpAsync(otp);
            
            // Create response
            OtpResponse response = OtpResponse.success("OTP sent successfully");
            response.setMaskedDestination(maskDestination(deliveryDestination, deliveryMethod));
            response.setExpiryMinutes(DEFAULT_EXPIRY_MINUTES);
            response.setRateLimitRemaining(MAX_OTP_GENERATION_PER_HOUR - (int) recentOtpCount - 1);
            
            logger.info("OTP generated successfully for user {} with type {} to destination {}", 
                       username, otpType, response.getMaskedDestination());
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error generating OTP for user {}: {}", username, e.getMessage(), e);
            return OtpResponse.error("Failed to generate OTP. Please try again.");
        }
    }
    
    /**
     * Verify OTP
     */
    public OtpResponse verifyOtp(String username, String otpCode, UserOtp.OtpType otpType) {
        
        try {
            // Find user
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                logger.warn("OTP verification attempted for non-existent user: {}", username);
                return OtpResponse.error("Invalid credentials");
            }
            
            User user = userOptional.get();
            
            // Find valid OTP
            Optional<UserOtp> otpOptional = otpRepository.findValidOtpByUserAndType(
                user, otpType, LocalDateTime.now());
            
            if (otpOptional.isEmpty()) {
                logger.warn("No valid OTP found for user {} and type {}", username, otpType);
                return OtpResponse.error("Invalid or expired OTP");
            }
            
            UserOtp otp = otpOptional.get();
            
            // Increment attempts
            otp.incrementAttempts();
            
            // Check if OTP matches
            if (!otp.getOtpCode().equals(otpCode)) {
                otpRepository.save(otp);
                
                int remainingAttempts = otp.getMaxAttempts() - otp.getAttempts();
                logger.warn("Invalid OTP code provided by user {}. Remaining attempts: {}", 
                           username, remainingAttempts);
                
                if (remainingAttempts <= 0) {
                    return OtpResponse.error("OTP verification failed. Maximum attempts exceeded.");
                }
                
                OtpResponse response = OtpResponse.error("Invalid OTP code");
                response.setRemainingAttempts(remainingAttempts);
                return response;
            }
            
            // OTP is valid - mark as used
            otp.markAsUsed();
            otpRepository.save(otp);
            
            logger.info("OTP verified successfully for user {} with type {}", username, otpType);
            
            return OtpResponse.success("OTP verified successfully");
            
        } catch (Exception e) {
            logger.error("Error verifying OTP for user {}: {}", username, e.getMessage(), e);
            return OtpResponse.error("Failed to verify OTP. Please try again.");
        }
    }
    
    /**
     * Generate OTP code
     */
    private String generateOtpCode(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }
    
    /**
     * Get default delivery destination for user based on delivery method
     */
    private String getDefaultDeliveryDestination(User user, UserOtp.DeliveryMethod deliveryMethod) {
        switch (deliveryMethod) {
            case EMAIL:
                return user.getEmail();
            case SMS:
            case VOICE:
                // Assuming phone number is stored in a field (you might need to add this to User entity)
                // return user.getPhoneNumber();
                return null; // For now, return null since phone number might not be implemented
            case AUTHENTICATOR_APP:
                return user.getUsername(); // Use username as identifier for authenticator apps
            default:
                return null;
        }
    }
    
    /**
     * Mask delivery destination for security
     */
    private String maskDestination(String destination, UserOtp.DeliveryMethod deliveryMethod) {
        if (destination == null || destination.length() < 4) {
            return "***";
        }
        
        switch (deliveryMethod) {
            case EMAIL:
                int atIndex = destination.indexOf('@');
                if (atIndex > 2) {
                    return destination.substring(0, 2) + "***" + destination.substring(atIndex);
                }
                break;
            case SMS:
            case VOICE:
                if (destination.length() > 4) {
                    return "***" + destination.substring(destination.length() - 4);
                }
                break;
            case AUTHENTICATOR_APP:
                return destination; // Don't mask authenticator app identifiers
        }
        
        return destination.substring(0, 2) + "***";
    }
    
    /**
     * Send OTP asynchronously
     */
    @Async
    public void sendOtpAsync(UserOtp otp) {
        try {
            switch (otp.getDeliveryMethod()) {
                case EMAIL:
                    sendOtpByEmail(otp);
                    break;
                case SMS:
                    sendOtpBySms(otp);
                    break;
                case VOICE:
                    sendOtpByVoice(otp);
                    break;
                case AUTHENTICATOR_APP:
                    // For authenticator apps, OTP is typically generated on the device
                    // This might involve QR code generation or app-specific integration
                    logger.info("Authenticator app OTP generated for user: {}", 
                               otp.getUser().getUsername());
                    break;
            }
        } catch (Exception e) {
            logger.error("Failed to send OTP via {}: {}", otp.getDeliveryMethod(), e.getMessage(), e);
        }
    }
    
    /**
     * Send OTP via email
     */
    private void sendOtpByEmail(UserOtp otp) {
        String subject = "Your " + otp.getOtpType().getDescription() + " Code";
        String body = buildEmailBody(otp);
        
        emailService.sendSimpleEmail(otp.getDeliveryDestination(), subject, body);
        logger.info("OTP sent via email to: {}", maskDestination(otp.getDeliveryDestination(), UserOtp.DeliveryMethod.EMAIL));
    }
    
    /**
     * Send OTP via SMS (placeholder implementation)
     */
    private void sendOtpBySms(UserOtp otp) {
        // Implement SMS sending logic here
        // This would typically involve integration with SMS service provider like Twilio, AWS SNS, etc.
        logger.info("SMS OTP sending not implemented yet for: {}", 
                   maskDestination(otp.getDeliveryDestination(), UserOtp.DeliveryMethod.SMS));
    }
    
    /**
     * Send OTP via voice call (placeholder implementation)
     */
    private void sendOtpByVoice(UserOtp otp) {
        // Implement voice call logic here
        // This would typically involve integration with voice service provider
        logger.info("Voice OTP sending not implemented yet for: {}", 
                   maskDestination(otp.getDeliveryDestination(), UserOtp.DeliveryMethod.VOICE));
    }
    
    /**
     * Build email body for OTP
     */
    private String buildEmailBody(UserOtp otp) {
        return String.format(
            "Dear %s,\n\n" +
            "Your %s code is: %s\n\n" +
            "This code will expire in %d minutes.\n" +
            "If you did not request this code, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Sqoolbus Team",
            otp.getUser().getFirstName() != null ? otp.getUser().getFirstName() : otp.getUser().getUsername(),
            otp.getOtpType().getDescription(),
            otp.getOtpCode(),
            DEFAULT_EXPIRY_MINUTES
        );
    }
    
    /**
     * Clean up expired OTPs (should be called by a scheduled task)
     */
    @Transactional
    public void cleanupExpiredOtps() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
            int deletedExpired = otpRepository.deleteExpiredOtps(cutoffTime);
            
            LocalDateTime oldUsedCutoff = LocalDateTime.now().minusDays(7);
            int deletedOldUsed = otpRepository.deleteOldUsedOtps(oldUsedCutoff);
            
            if (deletedExpired > 0 || deletedOldUsed > 0) {
                logger.info("OTP cleanup completed. Deleted {} expired and {} old used OTPs", 
                           deletedExpired, deletedOldUsed);
            }
        } catch (Exception e) {
            logger.error("Error during OTP cleanup: {}", e.getMessage(), e);
        }
    }
}
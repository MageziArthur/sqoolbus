package com.sqool.sqoolbus.tenant.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing OTP tokens for two-factor authentication
 */
@Entity
@Table(name = "user_otps", indexes = {
    @Index(name = "idx_user_otps_user_id", columnList = "user_id"),
    @Index(name = "idx_user_otps_otp_code", columnList = "otp_code"),
    @Index(name = "idx_user_otps_expires_at", columnList = "expires_at"),
    @Index(name = "idx_user_otps_is_used", columnList = "is_used")
})
public class UserOtp {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "otp_code", nullable = false, length = 10)
    private String otpCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "otp_type", nullable = false, length = 20)
    private OtpType otpType;
    
    @Column(name = "delivery_method", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;
    
    @Column(name = "delivery_destination", nullable = false, length = 255)
    private String deliveryDestination; // email address or phone number
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;
    
    @Column(name = "attempts", nullable = false)
    private Integer attempts = 0;
    
    @Column(name = "max_attempts", nullable = false)
    private Integer maxAttempts = 3;
    
    @Column(name = "client_ip", length = 45)
    private String clientIp;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    // Constructors
    public UserOtp() {
        this.createdAt = LocalDateTime.now();
    }
    
    public UserOtp(User user, String otpCode, OtpType otpType, DeliveryMethod deliveryMethod, 
                   String deliveryDestination, int validityMinutes) {
        this();
        this.user = user;
        this.otpCode = otpCode;
        this.otpType = otpType;
        this.deliveryMethod = deliveryMethod;
        this.deliveryDestination = deliveryDestination;
        this.expiresAt = LocalDateTime.now().plusMinutes(validityMinutes);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getOtpCode() {
        return otpCode;
    }
    
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
    
    public OtpType getOtpType() {
        return otpType;
    }
    
    public void setOtpType(OtpType otpType) {
        this.otpType = otpType;
    }
    
    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }
    
    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }
    
    public String getDeliveryDestination() {
        return deliveryDestination;
    }
    
    public void setDeliveryDestination(String deliveryDestination) {
        this.deliveryDestination = deliveryDestination;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUsedAt() {
        return usedAt;
    }
    
    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
    
    public Boolean getIsUsed() {
        return isUsed;
    }
    
    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }
    
    public Integer getAttempts() {
        return attempts;
    }
    
    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }
    
    public Integer getMaxAttempts() {
        return maxAttempts;
    }
    
    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
    
    public String getClientIp() {
        return clientIp;
    }
    
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    // Utility methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
    
    public boolean isValid() {
        return !isUsed && !isExpired() && attempts < maxAttempts;
    }
    
    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }
    
    public void incrementAttempts() {
        this.attempts++;
    }
    
    /**
     * OTP Type enumeration
     */
    public enum OtpType {
        LOGIN_2FA("Login Two-Factor Authentication"),
        PASSWORD_RESET("Password Reset"),
        EMAIL_VERIFICATION("Email Verification"),
        PHONE_VERIFICATION("Phone Verification"),
        ACCOUNT_RECOVERY("Account Recovery");
        
        private final String description;
        
        OtpType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Delivery Method enumeration
     */
    public enum DeliveryMethod {
        EMAIL("Email"),
        SMS("SMS"),
        VOICE("Voice Call"),
        AUTHENTICATOR_APP("Authenticator App");
        
        private final String description;
        
        DeliveryMethod(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
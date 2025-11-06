package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Enhanced login request DTO that supports two-factor authentication
 */
@Schema(description = "Enhanced login request with optional 2FA support")
public class EnhancedMasterLoginRequest {
    
    @Schema(description = "Username for login", example = "superadmin")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @Schema(description = "Password for login", example = "admin123")
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    
    @Schema(description = "OTP code for two-factor authentication (optional)", example = "123456")
    private String otpCode;
    
    @Schema(description = "Whether to enable 2FA for this login", example = "true")
    private boolean enable2FA = false;
    
    @Schema(description = "Preferred delivery method for 2FA", example = "EMAIL",
            allowableValues = {"EMAIL", "SMS", "VOICE", "AUTHENTICATOR_APP"})
    private String deliveryMethod = "EMAIL";
    
    // Constructors
    public EnhancedMasterLoginRequest() {}
    
    public EnhancedMasterLoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getOtpCode() {
        return otpCode;
    }
    
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
    
    public boolean isEnable2FA() {
        return enable2FA;
    }
    
    public void setEnable2FA(boolean enable2FA) {
        this.enable2FA = enable2FA;
    }
    
    public String getDeliveryMethod() {
        return deliveryMethod;
    }
    
    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }
}
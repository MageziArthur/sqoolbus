package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Enhanced login request DTO for tenant authentication with 2FA support
 */
@Schema(description = "Enhanced tenant login request with optional 2FA support")
public class EnhancedLoginRequest {
    
    @Schema(description = "Username or email for login", example = "admin")
    @NotBlank(message = "Username or email is required")
    @Size(min = 3, max = 100, message = "Username/email must be between 3 and 100 characters")
    private String usernameOrEmail;
    
    @Schema(description = "Password for login", example = "password123")
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
    public EnhancedLoginRequest() {}
    
    public EnhancedLoginRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }
    
    // Getters and Setters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }
    
    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
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
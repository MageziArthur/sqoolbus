package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for OTP generation
 */
@Schema(description = "Request to generate OTP for two-factor authentication")
public class OtpGenerationRequest {
    
    @Schema(description = "Username for OTP generation", example = "superadmin")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @Schema(description = "OTP type", example = "LOGIN_2FA", 
            allowableValues = {"LOGIN_2FA", "PASSWORD_RESET", "EMAIL_VERIFICATION", "PHONE_VERIFICATION", "ACCOUNT_RECOVERY"})
    @NotBlank(message = "OTP type is required")
    private String otpType;
    
    @Schema(description = "Delivery method", example = "EMAIL", 
            allowableValues = {"EMAIL", "SMS", "VOICE", "AUTHENTICATOR_APP"})
    @NotBlank(message = "Delivery method is required")
    private String deliveryMethod;
    
    @Schema(description = "Delivery destination (email or phone)", example = "admin@sqoolbus.com")
    private String deliveryDestination;
    
    // Constructors
    public OtpGenerationRequest() {}
    
    public OtpGenerationRequest(String username, String otpType, String deliveryMethod) {
        this.username = username;
        this.otpType = otpType;
        this.deliveryMethod = deliveryMethod;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getOtpType() {
        return otpType;
    }
    
    public void setOtpType(String otpType) {
        this.otpType = otpType;
    }
    
    public String getDeliveryMethod() {
        return deliveryMethod;
    }
    
    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }
    
    public String getDeliveryDestination() {
        return deliveryDestination;
    }
    
    public void setDeliveryDestination(String deliveryDestination) {
        this.deliveryDestination = deliveryDestination;
    }
}
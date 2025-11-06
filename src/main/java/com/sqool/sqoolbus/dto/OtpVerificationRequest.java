package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for OTP verification
 */
@Schema(description = "Request to verify OTP for two-factor authentication")
public class OtpVerificationRequest {
    
    @Schema(description = "Username for OTP verification", example = "superadmin")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @Schema(description = "OTP code", example = "123456")
    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "^[0-9]{4,10}$", message = "OTP code must be 4-10 digits")
    private String otpCode;
    
    @Schema(description = "OTP type", example = "LOGIN_2FA",
            allowableValues = {"LOGIN_2FA", "PASSWORD_RESET", "EMAIL_VERIFICATION", "PHONE_VERIFICATION", "ACCOUNT_RECOVERY"})
    @NotBlank(message = "OTP type is required")
    private String otpType;
    
    // Constructors
    public OtpVerificationRequest() {}
    
    public OtpVerificationRequest(String username, String otpCode, String otpType) {
        this.username = username;
        this.otpCode = otpCode;
        this.otpType = otpType;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getOtpCode() {
        return otpCode;
    }
    
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
    
    public String getOtpType() {
        return otpType;
    }
    
    public void setOtpType(String otpType) {
        this.otpType = otpType;
    }
}
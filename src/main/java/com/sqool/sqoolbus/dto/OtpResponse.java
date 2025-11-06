package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for OTP operations
 */
@Schema(description = "Response for OTP generation and verification operations")
public class OtpResponse {
    
    @Schema(description = "Success status", example = "true")
    private boolean success;
    
    @Schema(description = "Response message", example = "OTP sent successfully")
    private String message;
    
    @Schema(description = "Masked delivery destination", example = "ad***@sqoolbus.com")
    private String maskedDestination;
    
    @Schema(description = "OTP expiry time in minutes", example = "5")
    private Integer expiryMinutes;
    
    @Schema(description = "Remaining attempts", example = "2")
    private Integer remainingAttempts;
    
    @Schema(description = "Rate limit remaining", example = "3")
    private Integer rateLimitRemaining;
    
    // Constructors
    public OtpResponse() {}
    
    public OtpResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public static OtpResponse success(String message) {
        return new OtpResponse(true, message);
    }
    
    public static OtpResponse error(String message) {
        return new OtpResponse(false, message);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getMaskedDestination() {
        return maskedDestination;
    }
    
    public void setMaskedDestination(String maskedDestination) {
        this.maskedDestination = maskedDestination;
    }
    
    public Integer getExpiryMinutes() {
        return expiryMinutes;
    }
    
    public void setExpiryMinutes(Integer expiryMinutes) {
        this.expiryMinutes = expiryMinutes;
    }
    
    public Integer getRemainingAttempts() {
        return remainingAttempts;
    }
    
    public void setRemainingAttempts(Integer remainingAttempts) {
        this.remainingAttempts = remainingAttempts;
    }
    
    public Integer getRateLimitRemaining() {
        return rateLimitRemaining;
    }
    
    public void setRateLimitRemaining(Integer rateLimitRemaining) {
        this.rateLimitRemaining = rateLimitRemaining;
    }
}
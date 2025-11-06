package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enhanced login response DTO that supports two-factor authentication
 */
@Schema(description = "Enhanced login response with 2FA support")
public class EnhancedMasterLoginResponse {
    
    @Schema(description = "JWT token (only provided after successful 2FA or when 2FA is disabled)", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
    
    @Schema(description = "Token type", example = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "User ID", example = "1")
    private Long userId;
    
    @Schema(description = "Username", example = "superadmin")
    private String username;
    
    @Schema(description = "User's first name", example = "Super")
    private String firstName;
    
    @Schema(description = "User's last name", example = "Admin")
    private String lastName;
    
    @Schema(description = "User's email", example = "admin@sqoolbus.com")
    private String email;
    
    @Schema(description = "User roles", example = "[\"ROLE_SUPER_ADMIN\"]")
    private java.util.Set<String> roles;
    
    @Schema(description = "User permissions", example = "[\"MANAGE_USERS\", \"MANAGE_TENANTS\"]")
    private java.util.Set<String> permissions;
    
    @Schema(description = "Whether 2FA is required for this login", example = "false")
    private boolean require2FA = false;
    
    @Schema(description = "Whether 2FA verification is pending", example = "false")
    private boolean pending2FA = false;
    
    @Schema(description = "2FA session identifier (used for pending 2FA)", example = "temp-session-123")
    private String twoFASessionId;
    
    @Schema(description = "Masked delivery destination for 2FA", example = "ad***@sqoolbus.com")
    private String maskedDeliveryDestination;
    
    @Schema(description = "2FA OTP expiry minutes", example = "5")
    private Integer otpExpiryMinutes;
    
    // Constructors
    public EnhancedMasterLoginResponse() {}
    
    public EnhancedMasterLoginResponse(Long userId, String username, String firstName, String lastName, 
                                     String email, java.util.Set<String> roles, java.util.Set<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
    }
    
    // Static factory methods
    public static EnhancedMasterLoginResponse successfulLogin(String token, Long userId, String username, 
                                                            String firstName, String lastName, String email,
                                                            java.util.Set<String> roles, java.util.Set<String> permissions) {
        EnhancedMasterLoginResponse response = new EnhancedMasterLoginResponse(userId, username, firstName, lastName, email, roles, permissions);
        response.setToken(token);
        return response;
    }
    
    public static EnhancedMasterLoginResponse pending2FA(String sessionId, String maskedDestination, 
                                                        Integer expiryMinutes, Long userId, String username,
                                                        String firstName, String lastName, String email,
                                                        java.util.Set<String> roles, java.util.Set<String> permissions) {
        EnhancedMasterLoginResponse response = new EnhancedMasterLoginResponse(userId, username, firstName, lastName, email, roles, permissions);
        response.setPending2FA(true);
        response.setTwoFASessionId(sessionId);
        response.setMaskedDeliveryDestination(maskedDestination);
        response.setOtpExpiryMinutes(expiryMinutes);
        return response;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public java.util.Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(java.util.Set<String> roles) {
        this.roles = roles;
    }
    
    public java.util.Set<String> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(java.util.Set<String> permissions) {
        this.permissions = permissions;
    }
    
    public boolean isRequire2FA() {
        return require2FA;
    }
    
    public void setRequire2FA(boolean require2FA) {
        this.require2FA = require2FA;
    }
    
    public boolean isPending2FA() {
        return pending2FA;
    }
    
    public void setPending2FA(boolean pending2FA) {
        this.pending2FA = pending2FA;
    }
    
    public String getTwoFASessionId() {
        return twoFASessionId;
    }
    
    public void setTwoFASessionId(String twoFASessionId) {
        this.twoFASessionId = twoFASessionId;
    }
    
    public String getMaskedDeliveryDestination() {
        return maskedDeliveryDestination;
    }
    
    public void setMaskedDeliveryDestination(String maskedDeliveryDestination) {
        this.maskedDeliveryDestination = maskedDeliveryDestination;
    }
    
    public Integer getOtpExpiryMinutes() {
        return otpExpiryMinutes;
    }
    
    public void setOtpExpiryMinutes(Integer otpExpiryMinutes) {
        this.otpExpiryMinutes = otpExpiryMinutes;
    }
}
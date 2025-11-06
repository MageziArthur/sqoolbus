package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enhanced login response DTO for tenant authentication with 2FA support
 */
@Schema(description = "Enhanced tenant login response with 2FA support")
public class EnhancedLoginResponse {
    
    @Schema(description = "JWT token (only provided after successful 2FA or when 2FA is disabled)", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
    
    @Schema(description = "Token type", example = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "Token expiration in seconds", example = "86400")
    private Long expiresIn;
    
    @Schema(description = "Token expiration timestamp", example = "2025-11-07T18:30:00")
    private String expiresAt;
    
    @Schema(description = "Tenant ID", example = "default_sqool")
    private String tenantId;
    
    @Schema(description = "User information")
    private UserInfo user;
    
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
    public EnhancedLoginResponse() {}
    
    public EnhancedLoginResponse(String token, String tokenType, Long expiresIn, String expiresAt, 
                               String tenantId, UserInfo user) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.expiresAt = expiresAt;
        this.tenantId = tenantId;
        this.user = user;
    }
    
    // Static factory methods
    public static EnhancedLoginResponse successfulLogin(String token, String tokenType, Long expiresIn, 
                                                       String expiresAt, String tenantId, UserInfo user) {
        return new EnhancedLoginResponse(token, tokenType, expiresIn, expiresAt, tenantId, user);
    }
    
    public static EnhancedLoginResponse pending2FA(String sessionId, String maskedDestination, 
                                                  Integer expiryMinutes, String tenantId, UserInfo user) {
        EnhancedLoginResponse response = new EnhancedLoginResponse();
        response.setPending2FA(true);
        response.setTwoFASessionId(sessionId);
        response.setMaskedDeliveryDestination(maskedDestination);
        response.setOtpExpiryMinutes(expiryMinutes);
        response.setTenantId(tenantId);
        response.setUser(user);
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
    
    public Long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public String getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public UserInfo getUser() {
        return user;
    }
    
    public void setUser(UserInfo user) {
        this.user = user;
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
    
    /**
     * User information nested class
     */
    @Schema(description = "User information")
    public static class UserInfo {
        @Schema(description = "User ID", example = "1")
        private Long id;
        
        @Schema(description = "Username", example = "admin")
        private String username;
        
        @Schema(description = "Email", example = "admin@tenant.com")
        private String email;
        
        @Schema(description = "First name", example = "Admin")
        private String firstName;
        
        @Schema(description = "Last name", example = "User")
        private String lastName;
        
        @Schema(description = "User roles", example = "[\"ADMIN\", \"USER\"]")
        private java.util.Set<String> roles;
        
        @Schema(description = "User permissions", example = "[\"USER_READ\", \"USER_WRITE\"]")
        private java.util.Set<String> permissions;
        
        // Constructors
        public UserInfo() {}
        
        public UserInfo(Long id, String username, String email, String firstName, String lastName,
                       java.util.Set<String> roles, java.util.Set<String> permissions) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.roles = roles;
            this.permissions = permissions;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public java.util.Set<String> getRoles() { return roles; }
        public void setRoles(java.util.Set<String> roles) { this.roles = roles; }
        
        public java.util.Set<String> getPermissions() { return permissions; }
        public void setPermissions(java.util.Set<String> permissions) { this.permissions = permissions; }
    }
}
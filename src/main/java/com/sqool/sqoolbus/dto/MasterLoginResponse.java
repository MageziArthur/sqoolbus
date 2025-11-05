package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Master login response with user information and system-level permissions")
public class MasterLoginResponse {
    
    @Schema(description = "JWT authentication token")
    private String token;
    
    @Schema(description = "Token type", example = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "Token expiration time in seconds", example = "3600")
    private Long expiresIn;
    
    @Schema(description = "User ID")
    private Long userId;
    
    @Schema(description = "Username")
    private String username;
    
    @Schema(description = "User email")
    private String email;
    
    @Schema(description = "User's full name")
    private String fullName;
    
    @Schema(description = "User's system-level roles")
    private List<String> roles;
    
    @Schema(description = "User's system-level permissions")
    private List<String> permissions;
    
    @Schema(description = "Whether user is active")
    private Boolean isActive;
    
    @Schema(description = "Last login timestamp")
    private LocalDateTime lastLoginAt;
    
    // Constructors
    public MasterLoginResponse() {}
    
    public MasterLoginResponse(String token, Long expiresIn, Long userId, String username, 
                               String email, String fullName, List<String> roles, List<String> permissions) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
        this.permissions = permissions;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public List<String> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }
    
    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    
    @Override
    public String toString() {
        return "MasterLoginResponse{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", roles=" + roles +
                ", permissions=" + permissions +
                ", isActive=" + isActive +
                '}';
    }
}
package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Login response containing authentication token and user information")
public class LoginResponse {
    
    @Schema(description = "JWT authentication token", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Token type", 
            example = "Bearer", 
            defaultValue = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "Token expiration time in seconds", 
            example = "86400")
    private Long expiresIn;
    
    @Schema(description = "Token expiration timestamp", 
            example = "2025-11-02T18:30:00")
    private LocalDateTime expiresAt;
    
    @Schema(description = "Authenticated user information")
    private UserInfo user;
    
    public LoginResponse() {}
    
    public LoginResponse(String token, Long expiresIn, LocalDateTime expiresAt, UserInfo user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.expiresAt = expiresAt;
        this.user = user;
    }
    
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
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public UserInfo getUser() {
        return user;
    }
    
    public void setUser(UserInfo user) {
        this.user = user;
    }
    
    @Schema(description = "User information")
    public static class UserInfo {
        @Schema(description = "User unique identifier", example = "1")
        private Long id;
        
        @Schema(description = "Username", example = "admin")
        private String username;
        
        @Schema(description = "Email address", example = "admin@sqool.com")
        private String email;
        
        @Schema(description = "First name", example = "System")
        private String firstName;
        
        @Schema(description = "Last name", example = "Administrator")
        private String lastName;
        
        @Schema(description = "User roles", example = "[\"ADMIN\", \"USER\"]")
        private Set<String> roles;
        
        @Schema(description = "User permissions", example = "[\"USER_READ\", \"USER_WRITE\", \"TENANT_ADMIN\"]")
        private Set<String> permissions;
        
        public UserInfo() {}
        
        public UserInfo(Long id, String username, String email, String firstName, String lastName, 
                       Set<String> roles, Set<String> permissions) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.roles = roles;
            this.permissions = permissions;
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
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
        
        public Set<String> getRoles() {
            return roles;
        }
        
        public void setRoles(Set<String> roles) {
            this.roles = roles;
        }
        
        public Set<String> getPermissions() {
            return permissions;
        }
        
        public void setPermissions(Set<String> permissions) {
            this.permissions = permissions;
        }
    }
}
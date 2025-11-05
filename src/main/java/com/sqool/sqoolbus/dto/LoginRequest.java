package com.sqool.sqoolbus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request containing user credentials")
public class LoginRequest {
    
    @Schema(description = "Username or email address for authentication", 
            example = "admin", 
            required = true)
    @NotBlank(message = "Username or email is required")
    @Size(min = 3, max = 100, message = "Username or email must be between 3 and 100 characters")
    private String usernameOrEmail;
    
    @Schema(description = "User password", 
            example = "admin123", 
            required = true,
            format = "password")
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    
    public LoginRequest() {}
    
    public LoginRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }
    
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }
    
    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
    
    // Keep backward compatibility with getUsername() method
    public String getUsername() {
        return usernameOrEmail;
    }
    
    public void setUsername(String username) {
        this.usernameOrEmail = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
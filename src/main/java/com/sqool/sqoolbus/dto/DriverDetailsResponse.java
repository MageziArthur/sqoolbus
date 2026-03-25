package com.sqool.sqoolbus.dto;

import com.sqool.sqoolbus.master.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Driver details response")
public class DriverDetailsResponse {

    @Schema(description = "Driver user ID", example = "5")
    private Long id;

    @Schema(description = "Username", example = "driver.john")
    private String username;

    @Schema(description = "Email address", example = "john.driver@example.com")
    private String email;

    @Schema(description = "First name", example = "John")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Schema(description = "Full name", example = "John Doe")
    private String fullName;

    @Schema(description = "Whether the driver user is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Whether driver email is verified", example = "true")
    private Boolean isEmailVerified;

    @Schema(description = "Last login time")
    private LocalDateTime lastLoginAt;

    public DriverDetailsResponse() {
    }

    public DriverDetailsResponse(Long id, String username, String email, String firstName, String lastName,
                                 String fullName, Boolean isActive, Boolean isEmailVerified, LocalDateTime lastLoginAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.isActive = isActive;
        this.isEmailVerified = isEmailVerified;
        this.lastLoginAt = lastLoginAt;
    }

    public static DriverDetailsResponse fromUser(User user) {
        return new DriverDetailsResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getIsActive(),
                user.getIsEmailVerified(),
                user.getLastLoginAt()
        );
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Rider signup response with user information")
public class RiderSignupResponse {

    @Schema(description = "JWT authentication token")
    private String token;

    @Schema(description = "Token expiration time in seconds")
    private Long expiresIn;

    @Schema(description = "Token expiration timestamp")
    private LocalDateTime expiresAt;

    @Schema(description = "Rider user information")
    private RiderUserInfo user;

    @Schema(description = "Rider profile information")
    private RiderProfileInfo profile;

    public RiderSignupResponse() {}

    public RiderSignupResponse(String token, Long expiresIn, LocalDateTime expiresAt,
                              RiderUserInfo user, RiderProfileInfo profile) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.expiresAt = expiresAt;
        this.user = user;
        this.profile = profile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public RiderUserInfo getUser() {
        return user;
    }

    public void setUser(RiderUserInfo user) {
        this.user = user;
    }

    public RiderProfileInfo getProfile() {
        return profile;
    }

    public void setProfile(RiderProfileInfo profile) {
        this.profile = profile;
    }

    @Schema(description = "Rider user information")
    public static class RiderUserInfo {
        @Schema(description = "User ID")
        private Long id;

        @Schema(description = "Username")
        private String username;

        @Schema(description = "Email address")
        private String email;

        @Schema(description = "First name")
        private String firstName;

        @Schema(description = "Last name")
        private String lastName;

        @Schema(description = "Assigned roles")
        private Set<String> roles;

        @Schema(description = "User permissions")
        private Set<String> permissions;

        @Schema(description = "Account status")
        private Boolean isActive;

        @Schema(description = "Email verification status")
        private Boolean isEmailVerified;

        public RiderUserInfo() {}

        public RiderUserInfo(Long id, String username, String email, String firstName, String lastName,
                             Set<String> roles, Set<String> permissions, Boolean isActive, Boolean isEmailVerified) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.roles = roles;
            this.permissions = permissions;
            this.isActive = isActive;
            this.isEmailVerified = isEmailVerified;
        }

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

        public Set<String> getRoles() { return roles; }
        public void setRoles(Set<String> roles) { this.roles = roles; }

        public Set<String> getPermissions() { return permissions; }
        public void setPermissions(Set<String> permissions) { this.permissions = permissions; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }

        public Boolean getIsEmailVerified() { return isEmailVerified; }
        public void setIsEmailVerified(Boolean isEmailVerified) { this.isEmailVerified = isEmailVerified; }
    }

    @Schema(description = "Rider profile information")
    public static class RiderProfileInfo {
        @Schema(description = "License number")
        private String licenseNumber;

        @Schema(description = "Employee ID")
        private String employeeId;

        public RiderProfileInfo() {}

        public RiderProfileInfo(String licenseNumber, String employeeId) {
            this.licenseNumber = licenseNumber;
            this.employeeId = employeeId;
        }

        public String getLicenseNumber() { return licenseNumber; }
        public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    }
}

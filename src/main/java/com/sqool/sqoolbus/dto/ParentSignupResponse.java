package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Parent signup response with user information and profile details")
public class ParentSignupResponse {
    
    @Schema(description = "JWT authentication token")
    private String token;
    
    @Schema(description = "Token expiration time in seconds")
    private Long expiresIn;
    
    @Schema(description = "Token expiration timestamp")
    private LocalDateTime expiresAt;
    
    @Schema(description = "Parent user information")
    private ParentUserInfo user;
    
    @Schema(description = "Parent profile information")
    private ParentProfileInfo profile;
    
    @Schema(description = "Associated school information")
    private SchoolInfo school;
    
    // Constructors
    public ParentSignupResponse() {}
    
    public ParentSignupResponse(String token, Long expiresIn, LocalDateTime expiresAt, 
                               ParentUserInfo user, ParentProfileInfo profile, SchoolInfo school) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.expiresAt = expiresAt;
        this.user = user;
        this.profile = profile;
        this.school = school;
    }
    
    // Getters and Setters
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
    
    public ParentUserInfo getUser() {
        return user;
    }
    
    public void setUser(ParentUserInfo user) {
        this.user = user;
    }
    
    public ParentProfileInfo getProfile() {
        return profile;
    }
    
    public void setProfile(ParentProfileInfo profile) {
        this.profile = profile;
    }
    
    public SchoolInfo getSchool() {
        return school;
    }
    
    public void setSchool(SchoolInfo school) {
        this.school = school;
    }
    
    // Nested classes for structured response
    @Schema(description = "Parent user information")
    public static class ParentUserInfo {
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
        
        public ParentUserInfo() {}
        
        public ParentUserInfo(Long id, String username, String email, String firstName, String lastName,
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
        
        public Set<String> getRoles() { return roles; }
        public void setRoles(Set<String> roles) { this.roles = roles; }
        
        public Set<String> getPermissions() { return permissions; }
        public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
        
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
        
        public Boolean getIsEmailVerified() { return isEmailVerified; }
        public void setIsEmailVerified(Boolean isEmailVerified) { this.isEmailVerified = isEmailVerified; }
    }
    
    @Schema(description = "Parent profile information")
    public static class ParentProfileInfo {
        @Schema(description = "Profile ID")
        private Long id;
        
        @Schema(description = "Phone number")
        private String phoneNumber;
        
        @Schema(description = "Emergency contact")
        private String emergencyContact;
        
        @Schema(description = "Emergency contact name")
        private String emergencyContactName;
        
        @Schema(description = "Home address")
        private String address;
        
        @Schema(description = "City")
        private String city;
        
        @Schema(description = "State")
        private String state;
        
        @Schema(description = "ZIP code")
        private String zipCode;
        
        @Schema(description = "Occupation")
        private String occupation;
        
        @Schema(description = "Workplace")
        private String workplace;
        
        @Schema(description = "Work phone")
        private String workPhone;
        
        @Schema(description = "Preferred contact method")
        private String preferredContactMethod;
        
        public ParentProfileInfo() {}
        
        public ParentProfileInfo(Long id, String phoneNumber, String emergencyContact, String emergencyContactName,
                                String address, String city, String state, String zipCode,
                                String occupation, String workplace, String workPhone, String preferredContactMethod) {
            this.id = id;
            this.phoneNumber = phoneNumber;
            this.emergencyContact = emergencyContact;
            this.emergencyContactName = emergencyContactName;
            this.address = address;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
            this.occupation = occupation;
            this.workplace = workplace;
            this.workPhone = workPhone;
            this.preferredContactMethod = preferredContactMethod;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getEmergencyContact() { return emergencyContact; }
        public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
        
        public String getEmergencyContactName() { return emergencyContactName; }
        public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        
        public String getOccupation() { return occupation; }
        public void setOccupation(String occupation) { this.occupation = occupation; }
        
        public String getWorkplace() { return workplace; }
        public void setWorkplace(String workplace) { this.workplace = workplace; }
        
        public String getWorkPhone() { return workPhone; }
        public void setWorkPhone(String workPhone) { this.workPhone = workPhone; }
        
        public String getPreferredContactMethod() { return preferredContactMethod; }
        public void setPreferredContactMethod(String preferredContactMethod) { this.preferredContactMethod = preferredContactMethod; }
    }
    
    @Schema(description = "School information")
    public static class SchoolInfo {
        @Schema(description = "School ID")
        private Long id;
        
        @Schema(description = "School name")
        private String name;
        
        @Schema(description = "School code")
        private String code;
        
        @Schema(description = "School address")
        private String address;
        
        @Schema(description = "School phone")
        private String phoneNumber;
        
        @Schema(description = "School email")
        private String email;
        
        public SchoolInfo() {}
        
        public SchoolInfo(Long id, String name, String code, String address, String phoneNumber, String email) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.address = address;
            this.phoneNumber = phoneNumber;
            this.email = email;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
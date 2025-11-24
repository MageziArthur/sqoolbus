package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Parent signup request with additional parent-specific information")
public class ParentSignupRequest {
    
    @Schema(description = "Username for the parent account", example = "jane_parent", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @Schema(description = "Parent's email address", example = "jane.parent@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @Schema(description = "Password for the account", example = "securePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    
    @Schema(description = "Parent's first name", example = "Jane", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @Schema(description = "Parent's last name", example = "Smith", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @Schema(description = "School ID that the parent's children attend", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "School ID is required")
    private Long schoolId;
    
    @Schema(description = "Parent's phone number", example = "+1-555-123-4567", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "Phone number must be valid")
    private String phoneNumber;
    
    @Schema(description = "Emergency contact phone number", example = "+1-555-987-6543")
    private String emergencyContact;
    
    @Schema(description = "Emergency contact name", example = "John Smith")
    @Size(max = 100, message = "Emergency contact name must not exceed 100 characters")
    private String emergencyContactName;
    
    @Schema(description = "Home address", example = "123 Main Street, Anytown, State 12345")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @Schema(description = "City", example = "Anytown")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Schema(description = "State", example = "CA")
    @Size(max = 50, message = "State must not exceed 50 characters")
    private String state;
    
    @Schema(description = "ZIP code", example = "12345")
    @Size(max = 10, message = "ZIP code must not exceed 10 characters")
    private String zipCode;
    
    @Schema(description = "Occupation", example = "Software Engineer")
    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;
    
    @Schema(description = "Workplace", example = "Tech Company Inc.")
    @Size(max = 200, message = "Workplace must not exceed 200 characters")
    private String workplace;
    
    @Schema(description = "Work phone number", example = "+1-555-456-7890")
    private String workPhone;
    
    @Schema(description = "Preferred contact method", example = "EMAIL", allowableValues = {"EMAIL", "PHONE", "SMS"})
    private String preferredContactMethod = "EMAIL";
    
    // Constructors
    public ParentSignupRequest() {}
    
    public ParentSignupRequest(String username, String email, String password, String firstName, 
                              String lastName, Long schoolId, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.schoolId = schoolId;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and Setters
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    public Long getSchoolId() {
        return schoolId;
    }
    
    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmergencyContact() {
        return emergencyContact;
    }
    
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    
    public String getEmergencyContactName() {
        return emergencyContactName;
    }
    
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getOccupation() {
        return occupation;
    }
    
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    
    public String getWorkplace() {
        return workplace;
    }
    
    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }
    
    public String getWorkPhone() {
        return workPhone;
    }
    
    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }
    
    public String getPreferredContactMethod() {
        return preferredContactMethod;
    }
    
    public void setPreferredContactMethod(String preferredContactMethod) {
        this.preferredContactMethod = preferredContactMethod;
    }
}
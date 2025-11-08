package com.sqool.sqoolbus.tenant.entity.hail;

import com.sqool.sqoolbus.tenant.entity.BaseEntity;
import com.sqool.sqoolbus.tenant.entity.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing user profile information for different user types (Parent, Rider, SchoolAdmin)
 */
@Entity
@Table(name = "user_profile")
public class UserProfile extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;
    
    // Common profile fields
    @Column(name = "employee_id", length = 50)
    private String employeeId;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact;
    
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;
    
    @Column(name = "address", length = 500)
    private String address;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "state", length = 50)
    private String state;
    
    @Column(name = "zip_code", length = 10)
    private String zipCode;
    
    @Column(name = "country", length = 50)
    private String country = "USA";
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "gender", length = 10)
    private String gender;
    
    @Column(name = "profile_image_url", length = 300)
    private String profileImageUrl;
    
    // Parent-specific fields
    @Column(name = "home_latitude")
    private Double homeLatitude;
    
    @Column(name = "home_longitude")
    private Double homeLongitude;
    
    @Column(name = "occupation", length = 100)
    private String occupation;
    
    @Column(name = "workplace", length = 200)
    private String workplace;
    
    @Column(name = "work_phone", length = 20)
    private String workPhone;
    
    @Column(name = "preferred_contact_method", length = 20)
    private String preferredContactMethod = "EMAIL";
    
    @Column(name = "notification_preferences", length = 500)
    private String notificationPreferences;
    
    @Column(name = "relationship_to_child", length = 50)
    private String relationshipToChild;
    
    @Column(name = "pickup_authorization")
    private Boolean pickupAuthorization = true;
    
    @Column(name = "emergency_contact_authorization")
    private Boolean emergencyContactAuthorization = true;
    
    // Rider-specific fields
    @Column(name = "hire_date")
    private LocalDate hireDate;
    
    @Column(name = "license_number", length = 50)
    private String licenseNumber;
    
    @Column(name = "license_expiry_date")
    private LocalDate licenseExpiryDate;
    
    @Column(name = "license_class", length = 10)
    private String licenseClass;
    
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
    
    @Column(name = "certifications", length = 500)
    private String certifications;
    
    @Column(name = "medical_clearance_date")
    private LocalDate medicalClearanceDate;
    
    @Column(name = "background_check_date")
    private LocalDate backgroundCheckDate;
    
    @Column(name = "training_completion_date")
    private LocalDate trainingCompletionDate;
    
    @Column(name = "shift_start_time", length = 10)
    private String shiftStartTime;
    
    @Column(name = "shift_end_time", length = 10)
    private String shiftEndTime;
    
    @Column(name = "hourly_rate")
    private Double hourlyRate;
    
    @Column(name = "performance_rating")
    private Double performanceRating;
    
    @Column(name = "is_available")
    private Boolean isAvailable = true;
    
    // Admin-specific fields
    @Column(name = "office_phone", length = 20)
    private String officePhone;
    
    @Column(name = "department", length = 100)
    private String department;
    
    @Column(name = "job_title", length = 100)
    private String jobTitle;
    
    @Column(name = "office_location", length = 200)
    private String officeLocation;
    
    @Column(name = "supervisor_name", length = 100)
    private String supervisorName;
    
    @Column(name = "supervisor_contact", length = 20)
    private String supervisorContact;
    
    @Column(name = "working_hours", length = 100)
    private String workingHours;
    
    @Column(name = "salary")
    private Double salary;
    
    @Column(name = "access_level")
    private Integer accessLevel = 1;
    
    @Column(name = "permissions", length = 1000)
    private String permissions;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_route_id")
    private Route assignedRoute;
    
    // Constructors
    public UserProfile() {
        super();
    }
    
    public UserProfile(User user) {
        super();
        this.user = user;
    }
    
    public UserProfile(User user, School school) {
        super();
        this.user = user;
        this.school = school;
    }
    
    // Getters and Setters
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public School getSchool() {
        return school;
    }
    
    public void setSchool(School school) {
        this.school = school;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
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
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    
    public Double getHomeLatitude() {
        return homeLatitude;
    }
    
    public void setHomeLatitude(Double homeLatitude) {
        this.homeLatitude = homeLatitude;
    }
    
    public Double getHomeLongitude() {
        return homeLongitude;
    }
    
    public void setHomeLongitude(Double homeLongitude) {
        this.homeLongitude = homeLongitude;
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
    
    public String getNotificationPreferences() {
        return notificationPreferences;
    }
    
    public void setNotificationPreferences(String notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
    }
    
    public String getRelationshipToChild() {
        return relationshipToChild;
    }
    
    public void setRelationshipToChild(String relationshipToChild) {
        this.relationshipToChild = relationshipToChild;
    }
    
    public Boolean getPickupAuthorization() {
        return pickupAuthorization;
    }
    
    public void setPickupAuthorization(Boolean pickupAuthorization) {
        this.pickupAuthorization = pickupAuthorization;
    }
    
    public Boolean getEmergencyContactAuthorization() {
        return emergencyContactAuthorization;
    }
    
    public void setEmergencyContactAuthorization(Boolean emergencyContactAuthorization) {
        this.emergencyContactAuthorization = emergencyContactAuthorization;
    }
    
    public LocalDate getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public LocalDate getLicenseExpiryDate() {
        return licenseExpiryDate;
    }
    
    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }
    
    public String getLicenseClass() {
        return licenseClass;
    }
    
    public void setLicenseClass(String licenseClass) {
        this.licenseClass = licenseClass;
    }
    
    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }
    
    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
    
    public String getCertifications() {
        return certifications;
    }
    
    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }
    
    public LocalDate getMedicalClearanceDate() {
        return medicalClearanceDate;
    }
    
    public void setMedicalClearanceDate(LocalDate medicalClearanceDate) {
        this.medicalClearanceDate = medicalClearanceDate;
    }
    
    public LocalDate getBackgroundCheckDate() {
        return backgroundCheckDate;
    }
    
    public void setBackgroundCheckDate(LocalDate backgroundCheckDate) {
        this.backgroundCheckDate = backgroundCheckDate;
    }
    
    public LocalDate getTrainingCompletionDate() {
        return trainingCompletionDate;
    }
    
    public void setTrainingCompletionDate(LocalDate trainingCompletionDate) {
        this.trainingCompletionDate = trainingCompletionDate;
    }
    
    public String getShiftStartTime() {
        return shiftStartTime;
    }
    
    public void setShiftStartTime(String shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }
    
    public String getShiftEndTime() {
        return shiftEndTime;
    }
    
    public void setShiftEndTime(String shiftEndTime) {
        this.shiftEndTime = shiftEndTime;
    }
    
    public Double getHourlyRate() {
        return hourlyRate;
    }
    
    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    
    public Double getPerformanceRating() {
        return performanceRating;
    }
    
    public void setPerformanceRating(Double performanceRating) {
        this.performanceRating = performanceRating;
    }
    
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public String getOfficePhone() {
        return officePhone;
    }
    
    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getJobTitle() {
        return jobTitle;
    }
    
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    
    public String getOfficeLocation() {
        return officeLocation;
    }
    
    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }
    
    public String getSupervisorName() {
        return supervisorName;
    }
    
    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }
    
    public String getSupervisorContact() {
        return supervisorContact;
    }
    
    public void setSupervisorContact(String supervisorContact) {
        this.supervisorContact = supervisorContact;
    }
    
    public String getWorkingHours() {
        return workingHours;
    }
    
    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }
    
    public Double getSalary() {
        return salary;
    }
    
    public void setSalary(Double salary) {
        this.salary = salary;
    }
    
    public Integer getAccessLevel() {
        return accessLevel;
    }
    
    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }
    
    public String getPermissions() {
        return permissions;
    }
    
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Route getAssignedRoute() {
        return assignedRoute;
    }
    
    public void setAssignedRoute(Route assignedRoute) {
        this.assignedRoute = assignedRoute;
    }
    
    // Utility methods
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null) sb.append(address);
        if (city != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (state != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(state);
        }
        if (zipCode != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(zipCode);
        }
        return sb.toString();
    }
    
    public void setHomeCoordinates(Double latitude, Double longitude) {
        this.homeLatitude = latitude;
        this.homeLongitude = longitude;
    }
    
    public boolean hasHomeCoordinates() {
        return homeLatitude != null && homeLongitude != null;
    }
    
    public boolean isLicenseValid() {
        return licenseExpiryDate != null && licenseExpiryDate.isAfter(LocalDate.now());
    }
    
    public boolean isLicenseExpiringSoon() {
        if (licenseExpiryDate == null) return false;
        return licenseExpiryDate.isBefore(LocalDate.now().plusDays(30));
    }
    
    public boolean isMedicalClearanceCurrent() {
        if (medicalClearanceDate == null) return false;
        return medicalClearanceDate.isAfter(LocalDate.now().minusYears(2));
    }
    
    public boolean isBackgroundCheckCurrent() {
        if (backgroundCheckDate == null) return false;
        return backgroundCheckDate.isAfter(LocalDate.now().minusYears(5));
    }
    
    public boolean isQualifiedToWork() {
        return isLicenseValid() &&
               isMedicalClearanceCurrent() &&
               isBackgroundCheckCurrent() &&
               (isAvailable != null && isAvailable);
    }
    
    public int getAge() {
        if (dateOfBirth == null) return 0;
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
    
    public int getYearsOfService() {
        if (hireDate == null) return 0;
        return LocalDate.now().getYear() - hireDate.getYear();
    }
    
    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + getId() +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", school=" + (school != null ? school.getName() : "null") +
                ", employeeId='" + employeeId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                '}';
    }
}
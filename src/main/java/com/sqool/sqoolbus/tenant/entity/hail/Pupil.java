package com.sqool.sqoolbus.tenant.entity.hail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sqool.sqoolbus.tenant.entity.BaseEntity;
import com.sqool.sqoolbus.tenant.entity.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a student/pupil
 */
@Entity
@Table(name = "pupil")
public class Pupil extends BaseEntity {
    
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    @Column(name = "middle_name", length = 100)
    private String middleName;
    
    @Column(name = "student_id", unique = true, nullable = false, length = 50)
    private String studentId;
    
    @Column(name = "grade_level", nullable = false, length = 10)
    private String gradeLevel; // e.g., "K", "Pre-K", "1", "2", etc.
    
    @Column(name = "class_section", length = 10)
    private String classSection; // e.g., "A", "B", "C"
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "gender", length = 10)
    private String gender;
    
    @Column(name = "home_address", length = 500)
    private String homeAddress;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "state", length = 50)
    private String state;
    
    @Column(name = "zip_code", length = 10)
    private String zipCode;
    
    @Column(name = "home_latitude")
    private Double homeLatitude;
    
    @Column(name = "home_longitude")
    private Double homeLongitude;
    
    @Column(name = "parent_contact", length = 20)
    private String parentContact;
    
    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact;
    
    @Column(name = "parent_email", length = 100)
    private String parentEmail;
    
    @Column(name = "pickup_instructions", length = 500)
    private String pickupInstructions;
    
    @Column(name = "medical_conditions", length = 500)
    private String medicalConditions;
    
    @Column(name = "special_needs", columnDefinition = "TEXT")
    private String specialNeeds;
    
    @Column(name = "photo_url", length = 300)
    private String photoUrl;
    
    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "uses_bus_service", nullable = false)
    private Boolean usesBusService = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    @JsonIgnoreProperties({"pupils", "routes"})
    private School school;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    @JsonIgnoreProperties({"pupils", "trips", "school"})
    private Route route;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"children", "assignedRoutes", "roles", "permissions"})
    private User parent;
    
    // Constructors
    public Pupil() {
        super();
    }
    
    public Pupil(String firstName, String lastName, String studentId, String gradeLevel, School school) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentId = studentId;
        this.gradeLevel = gradeLevel;
        this.school = school;
    }
    
    // Getters and Setters
    
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
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }    public String getClassSection() {
        return classSection;
    }
    
    public void setClassSection(String classSection) {
        this.classSection = classSection;
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
    
    public String getHomeAddress() {
        return homeAddress;
    }
    
    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
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
    
    public String getParentContact() {
        return parentContact;
    }
    
    public void setParentContact(String parentContact) {
        this.parentContact = parentContact;
    }
    
    public String getEmergencyContact() {
        return emergencyContact;
    }
    
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
    
    public String getParentEmail() {
        return parentEmail;
    }
    
    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }
    
    public String getPickupInstructions() {
        return pickupInstructions;
    }
    
    public void setPickupInstructions(String pickupInstructions) {
        this.pickupInstructions = pickupInstructions;
    }
    
    public String getMedicalConditions() {
        return medicalConditions;
    }
    
    public void setMedicalConditions(String medicalConditions) {
        this.medicalConditions = medicalConditions;
    }
    
    public String getSpecialNeeds() {
        return specialNeeds;
    }
    
    public void setSpecialNeeds(String specialNeeds) {
        this.specialNeeds = specialNeeds;
    }
    
    public String getPhotoUrl() {
        return photoUrl;
    }
    
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getUsesBusService() {
        return usesBusService;
    }
    
    public void setUsesBusService(Boolean usesBusService) {
        this.usesBusService = usesBusService;
    }
    
    public School getSchool() {
        return school;
    }
    
    public void setSchool(School school) {
        this.school = school;
    }
    
    public Route getRoute() {
        return route;
    }
    
    public void setRoute(Route route) {
        this.route = route;
    }
    
    public User getParent() {
        return parent;
    }
    
    public void setParent(User parent) {
        this.parent = parent;
    }
    
    // Utility methods
    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstName);
        if (middleName != null && !middleName.trim().isEmpty()) {
            sb.append(" ").append(middleName);
        }
        sb.append(" ").append(lastName);
        return sb.toString();
    }
    
    public void setHomeCoordinates(Double latitude, Double longitude) {
        this.homeLatitude = latitude;
        this.homeLongitude = longitude;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public boolean hasHomeCoordinates() {
        return homeLatitude != null && homeLongitude != null;
    }
    
    public String getFullHomeAddress() {
        StringBuilder sb = new StringBuilder();
        if (homeAddress != null) sb.append(homeAddress);
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
    
    public int getAge() {
        if (dateOfBirth == null) return 0;
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
    
    @Override
    public String toString() {
        return "Pupil{" +
                "id=" + getId() +
                ", fullName='" + getFullName() + '\'' +
                ", studentId='" + studentId + '\'' +
                ", gradeLevel=" + gradeLevel +
                ", school=" + (school != null ? school.getName() : "null") +
                ", route=" + (route != null ? route.getRouteNumber() : "null") +
                ", parent=" + (parent != null ? parent.getFirstName() + " " + parent.getLastName() : "null") +
                ", isActive=" + isActive +
                '}';
    }
}
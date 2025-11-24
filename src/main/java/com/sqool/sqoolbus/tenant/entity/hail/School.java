package com.sqool.sqoolbus.tenant.entity.hail;

import com.sqool.sqoolbus.tenant.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a school
 */
@Entity
@Table(name = "school")
public class School extends BaseEntity {
    
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    @Column(name = "school_code", unique = true, nullable = false, length = 50)
    private String code;
    
    @Column(name = "address", length = 500)
    private String address;
    
    @Column(name = "school_type", nullable = false, length = 20)
    private String schoolType;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "website", length = 200)
    private String website;
    
    @Column(name = "principal_name", length = 100)
    private String principalName;
    
    @Column(name = "principal_contact", length = 20)
    private String principalContact;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "state", length = 50)
    private String state;
    
    @Column(name = "zip_code", length = 10)
    private String zipCode;
    
    @Column(name = "country", length = 50)
    private String country = "USA";
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "timezone", length = 50)
    private String timezone = "America/New_York";
    
    @Column(name = "start_time")
    private java.time.LocalTime startTime;
    
    @Column(name = "end_time")
    private java.time.LocalTime endTime;
    
    @Column(name = "established_date")
    private java.time.LocalDate establishedDate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Constructors
    public School() {
        super();
    }
    
    public School(String name, String code) {
        super();
        this.name = name;
        this.code = code;
    }
    
    // Getters and Setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getSchoolType() {
        return schoolType;
    }
    
    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getPrincipalContact() {
        return principalContact;
    }
    
    public void setPrincipalContact(String principalContact) {
        this.principalContact = principalContact;
    }
    
    public String getPrincipalName() {
        return principalName;
    }
    
    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
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
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public java.time.LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(java.time.LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public java.time.LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(java.time.LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public java.time.LocalDate getEstablishedDate() {
        return establishedDate;
    }
    
    public void setEstablishedDate(java.time.LocalDate establishedDate) {
        this.establishedDate = establishedDate;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    // Utility methods
    public void setCoordinates(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
    
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
    
    @Override
    public String toString() {
        return "School{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
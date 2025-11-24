package com.sqool.sqoolbus.dto;

import com.sqool.sqoolbus.tenant.entity.hail.BusStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BusResponse {
    
    private Long id;
    private String busNumber;
    private String licensePlate;
    private String make;
    private String model;
    private Integer year;
    private String color;
    private String vin;
    private Integer capacity;
    private String fuelType;
    private String transmissionType;
    private BusStatus status;
    private LocalDate purchaseDate;
    private Double purchasePrice;
    private Double currentMileage;
    private LocalDate lastServiceDate;
    private LocalDate nextServiceDate;
    private String insurancePolicyNumber;
    private LocalDate insuranceExpiryDate;
    private String registrationNumber;
    private LocalDate registrationExpiryDate;
    private LocalDate inspectionExpiryDate;
    private String gpsDeviceId;
    private Boolean wifiEnabled;
    private Boolean acAvailable;
    private Boolean wheelchairAccessible;
    private Boolean cctvInstalled;
    private Boolean firstAidKit;
    private Boolean fireExtinguisher;
    private Integer emergencyExits;
    private String notes;
    private Boolean isActive;
    private Double lastLocationLatitude;
    private Double lastLocationLongitude;
    private LocalDateTime lastLocationUpdate;
    
    // Related entities
    private Long schoolId;
    private String schoolName;
    private Long assignedRouteId;
    private String assignedRouteName;
    private Long assignedDriverId;
    private String assignedDriverName;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public BusResponse() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBusNumber() {
        return busNumber;
    }
    
    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public String getMake() {
        return make;
    }
    
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getVin() {
        return vin;
    }
    
    public void setVin(String vin) {
        this.vin = vin;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public String getFuelType() {
        return fuelType;
    }
    
    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
    
    public String getTransmissionType() {
        return transmissionType;
    }
    
    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }
    
    public BusStatus getStatus() {
        return status;
    }
    
    public void setStatus(BusStatus status) {
        this.status = status;
    }
    
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public Double getPurchasePrice() {
        return purchasePrice;
    }
    
    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
    public Double getCurrentMileage() {
        return currentMileage;
    }
    
    public void setCurrentMileage(Double currentMileage) {
        this.currentMileage = currentMileage;
    }
    
    public LocalDate getLastServiceDate() {
        return lastServiceDate;
    }
    
    public void setLastServiceDate(LocalDate lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }
    
    public LocalDate getNextServiceDate() {
        return nextServiceDate;
    }
    
    public void setNextServiceDate(LocalDate nextServiceDate) {
        this.nextServiceDate = nextServiceDate;
    }
    
    public String getInsurancePolicyNumber() {
        return insurancePolicyNumber;
    }
    
    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
    }
    
    public LocalDate getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }
    
    public void setInsuranceExpiryDate(LocalDate insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }
    
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    public LocalDate getRegistrationExpiryDate() {
        return registrationExpiryDate;
    }
    
    public void setRegistrationExpiryDate(LocalDate registrationExpiryDate) {
        this.registrationExpiryDate = registrationExpiryDate;
    }
    
    public LocalDate getInspectionExpiryDate() {
        return inspectionExpiryDate;
    }
    
    public void setInspectionExpiryDate(LocalDate inspectionExpiryDate) {
        this.inspectionExpiryDate = inspectionExpiryDate;
    }
    
    public String getGpsDeviceId() {
        return gpsDeviceId;
    }
    
    public void setGpsDeviceId(String gpsDeviceId) {
        this.gpsDeviceId = gpsDeviceId;
    }
    
    public Boolean getWifiEnabled() {
        return wifiEnabled;
    }
    
    public void setWifiEnabled(Boolean wifiEnabled) {
        this.wifiEnabled = wifiEnabled;
    }
    
    public Boolean getAcAvailable() {
        return acAvailable;
    }
    
    public void setAcAvailable(Boolean acAvailable) {
        this.acAvailable = acAvailable;
    }
    
    public Boolean getWheelchairAccessible() {
        return wheelchairAccessible;
    }
    
    public void setWheelchairAccessible(Boolean wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }
    
    public Boolean getCctvInstalled() {
        return cctvInstalled;
    }
    
    public void setCctvInstalled(Boolean cctvInstalled) {
        this.cctvInstalled = cctvInstalled;
    }
    
    public Boolean getFirstAidKit() {
        return firstAidKit;
    }
    
    public void setFirstAidKit(Boolean firstAidKit) {
        this.firstAidKit = firstAidKit;
    }
    
    public Boolean getFireExtinguisher() {
        return fireExtinguisher;
    }
    
    public void setFireExtinguisher(Boolean fireExtinguisher) {
        this.fireExtinguisher = fireExtinguisher;
    }
    
    public Integer getEmergencyExits() {
        return emergencyExits;
    }
    
    public void setEmergencyExits(Integer emergencyExits) {
        this.emergencyExits = emergencyExits;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Double getLastLocationLatitude() {
        return lastLocationLatitude;
    }
    
    public void setLastLocationLatitude(Double lastLocationLatitude) {
        this.lastLocationLatitude = lastLocationLatitude;
    }
    
    public Double getLastLocationLongitude() {
        return lastLocationLongitude;
    }
    
    public void setLastLocationLongitude(Double lastLocationLongitude) {
        this.lastLocationLongitude = lastLocationLongitude;
    }
    
    public LocalDateTime getLastLocationUpdate() {
        return lastLocationUpdate;
    }
    
    public void setLastLocationUpdate(LocalDateTime lastLocationUpdate) {
        this.lastLocationUpdate = lastLocationUpdate;
    }
    
    public Long getSchoolId() {
        return schoolId;
    }
    
    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }
    
    public String getSchoolName() {
        return schoolName;
    }
    
    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
    
    public Long getAssignedRouteId() {
        return assignedRouteId;
    }
    
    public void setAssignedRouteId(Long assignedRouteId) {
        this.assignedRouteId = assignedRouteId;
    }
    
    public String getAssignedRouteName() {
        return assignedRouteName;
    }
    
    public void setAssignedRouteName(String assignedRouteName) {
        this.assignedRouteName = assignedRouteName;
    }
    
    public Long getAssignedDriverId() {
        return assignedDriverId;
    }
    
    public void setAssignedDriverId(Long assignedDriverId) {
        this.assignedDriverId = assignedDriverId;
    }
    
    public String getAssignedDriverName() {
        return assignedDriverName;
    }
    
    public void setAssignedDriverName(String assignedDriverName) {
        this.assignedDriverName = assignedDriverName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

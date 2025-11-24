package com.sqool.sqoolbus.tenant.entity.hail;

import com.sqool.sqoolbus.tenant.entity.BaseEntity;
import com.sqool.sqoolbus.tenant.entity.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a school bus
 */
@Entity
@Table(name = "bus")
public class Bus extends BaseEntity {
    
    @Column(name = "bus_number", unique = true, nullable = false, length = 50)
    private String busNumber;
    
    @Column(name = "license_plate", unique = true, nullable = false, length = 20)
    private String licensePlate;
    
    @Column(name = "make", length = 50)
    private String make;
    
    @Column(name = "model", length = 50)
    private String model;
    
    @Column(name = "year")
    private Integer year;
    
    @Column(name = "color", length = 30)
    private String color;
    
    @Column(name = "vin", unique = true, length = 50)
    private String vin; // Vehicle Identification Number
    
    @Column(name = "capacity")
    private Integer capacity;
    
    @Column(name = "fuel_type", length = 20)
    private String fuelType; // DIESEL, GASOLINE, ELECTRIC, HYBRID
    
    @Column(name = "transmission_type", length = 20)
    private String transmissionType; // AUTOMATIC, MANUAL
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BusStatus status = BusStatus.AVAILABLE;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    
    @Column(name = "purchase_price")
    private Double purchasePrice;
    
    @Column(name = "current_mileage")
    private Double currentMileage;
    
    @Column(name = "last_service_date")
    private LocalDate lastServiceDate;
    
    @Column(name = "next_service_date")
    private LocalDate nextServiceDate;
    
    @Column(name = "insurance_policy_number", length = 100)
    private String insurancePolicyNumber;
    
    @Column(name = "insurance_expiry_date")
    private LocalDate insuranceExpiryDate;
    
    @Column(name = "registration_number", length = 50)
    private String registrationNumber;
    
    @Column(name = "registration_expiry_date")
    private LocalDate registrationExpiryDate;
    
    @Column(name = "inspection_expiry_date")
    private LocalDate inspectionExpiryDate;
    
    @Column(name = "gps_device_id", length = 100)
    private String gpsDeviceId;
    
    @Column(name = "wifi_enabled")
    private Boolean wifiEnabled = false;
    
    @Column(name = "ac_available")
    private Boolean acAvailable = false;
    
    @Column(name = "wheelchair_accessible")
    private Boolean wheelchairAccessible = false;
    
    @Column(name = "cctv_installed")
    private Boolean cctvInstalled = false;
    
    @Column(name = "first_aid_kit")
    private Boolean firstAidKit = true;
    
    @Column(name = "fire_extinguisher")
    private Boolean fireExtinguisher = true;
    
    @Column(name = "emergency_exits")
    private Integer emergencyExits;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "last_location_latitude")
    private Double lastLocationLatitude;
    
    @Column(name = "last_location_longitude")
    private Double lastLocationLongitude;
    
    @Column(name = "last_location_update")
    private LocalDateTime lastLocationUpdate;
    
    // Relationships
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_route_id")
    private Route assignedRoute;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_driver_id")
    private User assignedDriver; // User with RIDER role
    
    // Constructors
    public Bus() {
    }
    
    public Bus(String busNumber, String licensePlate, School school) {
        this.busNumber = busNumber;
        this.licensePlate = licensePlate;
        this.school = school;
    }
    
    // Getters and Setters
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
    
    public School getSchool() {
        return school;
    }
    
    public void setSchool(School school) {
        this.school = school;
    }
    
    public Route getAssignedRoute() {
        return assignedRoute;
    }
    
    public void setAssignedRoute(Route assignedRoute) {
        this.assignedRoute = assignedRoute;
    }
    
    public User getAssignedDriver() {
        return assignedDriver;
    }
    
    public void setAssignedDriver(User assignedDriver) {
        this.assignedDriver = assignedDriver;
    }
}

package com.sqool.sqoolbus.tenant.entity.hail;

import com.sqool.sqoolbus.tenant.entity.BaseEntity;
import com.sqool.sqoolbus.tenant.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity representing a trip that a rider starts for a route
 */
@Entity
@Table(name = "trip")
public class Trip extends BaseEntity {
    
    @Column(name = "trip_number", unique = true, length = 50)
    private String tripNumber;
    
    @Column(name = "trip_date", nullable = false)
    private LocalDateTime tripDate;
    
    @Column(name = "planned_start_time")
    private LocalDateTime plannedStartTime;
    
    @Column(name = "planned_end_time")
    private LocalDateTime plannedEndTime;
    
    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;
    
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;
    
    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;
    
    @Column(name = "actual_duration_minutes")
    private Integer actualDurationMinutes;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trip_type", nullable = false)
    private TripType tripType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TripStatus status = TripStatus.SCHEDULED;
    
    @Column(name = "start_location", length = 300)
    private String startLocation;
    
    @Column(name = "end_location", length = 300)
    private String endLocation;
    
    @Column(name = "start_latitude")
    private Double startLatitude;
    
    @Column(name = "start_longitude")
    private Double startLongitude;
    
    @Column(name = "end_latitude")
    private Double endLatitude;
    
    @Column(name = "end_longitude")
    private Double endLongitude;
    
    @Column(name = "current_latitude")
    private Double currentLatitude;
    
    @Column(name = "current_longitude")
    private Double currentLongitude;
    
    @Column(name = "distance_traveled_km")
    private Double distanceTraveledKm;
    
    @Column(name = "passenger_count")
    private Integer passengerCount = 0;
    
    @Column(name = "picked_up_count")
    private Integer pickedUpCount = 0;
    
    @Column(name = "dropped_off_count")
    private Integer droppedOffCount = 0;
    
    @Column(name = "weather_conditions", length = 100)
    private String weatherConditions;
    
    @Column(name = "traffic_conditions", length = 100)
    private String trafficConditions;
    
    @Column(name = "delay_reason", length = 500)
    private String delayReason;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "incident_reported")
    private Boolean incidentReported = false;
    
    @Column(name = "incident_details", columnDefinition = "TEXT")
    private String incidentDetails;
    
    @Column(name = "emergency_stops")
    private Integer emergencyStops = 0;
    
    @Column(name = "mechanical_issues", length = 500)
    private String mechanicalIssues;
    
    @Column(name = "fuel_used_liters")
    private Double fuelUsedLiters;
    
    @Column(name = "average_speed_kmh")
    private Double averageSpeedKmh;
    
    @Column(name = "max_speed_kmh")
    private Double maxSpeedKmh;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rider_id", nullable = false)
    private User rider;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
    
    // Enums
    public enum TripType {
        PICKUP,         // Morning pickup trip
        DROPOFF,        // Afternoon dropoff trip
        FIELD_TRIP,     // Special field trip
        EMERGENCY,      // Emergency trip
        MAINTENANCE,    // Maintenance run
        TRAINING        // Training trip
    }
    
    public enum TripStatus {
        SCHEDULED,      // Trip is scheduled
        IN_PROGRESS,    // Trip is currently active
        COMPLETED,      // Trip completed successfully
        CANCELLED,      // Trip was cancelled
        DELAYED,        // Trip is delayed
        SUSPENDED,      // Trip suspended due to issue
        EMERGENCY_STOP  // Emergency stop occurred
    }
    
    // Constructors
    public Trip() {
        super();
        this.tripDate = LocalDateTime.now();
    }
    
    public Trip(Route route, User rider, TripType tripType) {
        this();
        this.route = route;
        this.rider = rider;
        this.tripType = tripType;
        this.school = route.getSchool();
        this.generateTripNumber();
    }
    
    // Getters and Setters
    public String getTripNumber() {
        return tripNumber;
    }
    
    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }
    
    public LocalDateTime getTripDate() {
        return tripDate;
    }
    
    public void setTripDate(LocalDateTime tripDate) {
        this.tripDate = tripDate;
    }
    
    public LocalDateTime getPlannedStartTime() {
        return plannedStartTime;
    }
    
    public void setPlannedStartTime(LocalDateTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public LocalDateTime getPlannedEndTime() {
        return plannedEndTime;
    }
    
    public void setPlannedEndTime(LocalDateTime plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }
    
    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }
    
    public LocalDateTime getActualEndTime() {
        return actualEndTime;
    }
    
    public void setActualEndTime(LocalDateTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }
    
    public Integer getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }
    
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public Integer getActualDurationMinutes() {
        return actualDurationMinutes;
    }
    
    public void setActualDurationMinutes(Integer actualDurationMinutes) {
        this.actualDurationMinutes = actualDurationMinutes;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public TripType getTripType() {
        return tripType;
    }
    
    public void setTripType(TripType tripType) {
        this.tripType = tripType;
    }
    
    public TripStatus getStatus() {
        return status;
    }
    
    public void setStatus(TripStatus status) {
        this.status = status;
    }
    
    public String getStartLocation() {
        return startLocation;
    }
    
    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }
    
    public String getEndLocation() {
        return endLocation;
    }
    
    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }
    
    public Double getStartLatitude() {
        return startLatitude;
    }
    
    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }
    
    public Double getStartLongitude() {
        return startLongitude;
    }
    
    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }
    
    public Double getEndLatitude() {
        return endLatitude;
    }
    
    public void setEndLatitude(Double endLatitude) {
        this.endLatitude = endLatitude;
    }
    
    public Double getEndLongitude() {
        return endLongitude;
    }
    
    public void setEndLongitude(Double endLongitude) {
        this.endLongitude = endLongitude;
    }
    
    public Double getDistanceTraveledKm() {
        return distanceTraveledKm;
    }
    
    public void setDistanceTraveledKm(Double distanceTraveledKm) {
        this.distanceTraveledKm = distanceTraveledKm;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public Integer getPassengerCount() {
        return passengerCount;
    }
    
    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }
    
    public String getWeatherConditions() {
        return weatherConditions;
    }
    
    public void setWeatherConditions(String weatherConditions) {
        this.weatherConditions = weatherConditions;
    }
    
    public String getTrafficConditions() {
        return trafficConditions;
    }
    
    public void setTrafficConditions(String trafficConditions) {
        this.trafficConditions = trafficConditions;
    }
    
    public String getDelayReason() {
        return delayReason;
    }
    
    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Boolean getIncidentReported() {
        return incidentReported;
    }
    
    public void setIncidentReported(Boolean incidentReported) {
        this.incidentReported = incidentReported;
    }
    
    public String getIncidentDetails() {
        return incidentDetails;
    }
    
    public void setIncidentDetails(String incidentDetails) {
        this.incidentDetails = incidentDetails;
    }
    
    public Double getFuelUsedLiters() {
        return fuelUsedLiters;
    }
    
    public void setFuelUsedLiters(Double fuelUsedLiters) {
        this.fuelUsedLiters = fuelUsedLiters;
    }
    
    public Double getAverageSpeedKmh() {
        return averageSpeedKmh;
    }
    
    public void setAverageSpeedKmh(Double averageSpeedKmh) {
        this.averageSpeedKmh = averageSpeedKmh;
    }
    
    public Double getMaxSpeedKmh() {
        return maxSpeedKmh;
    }
    
    public void setMaxSpeedKmh(Double maxSpeedKmh) {
        this.maxSpeedKmh = maxSpeedKmh;
    }
    
    public Double getCurrentLatitude() {
        return currentLatitude;
    }
    
    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public Double getCurrentLongitude() {
        return currentLongitude;
    }
    
    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public Integer getPickedUpCount() {
        return pickedUpCount;
    }
    
    public void setPickedUpCount(Integer pickedUpCount) {
        this.pickedUpCount = pickedUpCount;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public Integer getDroppedOffCount() {
        return droppedOffCount;
    }
    
    public void setDroppedOffCount(Integer droppedOffCount) {
        this.droppedOffCount = droppedOffCount;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public Integer getEmergencyStops() {
        return emergencyStops;
    }
    
    public void setEmergencyStops(Integer emergencyStops) {
        this.emergencyStops = emergencyStops;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public String getMechanicalIssues() {
        return mechanicalIssues;
    }
    
    public void setMechanicalIssues(String mechanicalIssues) {
        this.mechanicalIssues = mechanicalIssues;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public Route getRoute() {
        return route;
    }
    
    public void setRoute(Route route) {
        this.route = route;
        if (route != null) {
            this.school = route.getSchool();
        }
    }
    
    public User getRider() {
        return rider;
    }
    
    public void setRider(User rider) {
        this.rider = rider;
    }
    
    public School getSchool() {
        return school;
    }
    
    public void setSchool(School school) {
        this.school = school;
    }
    
    // Utility methods
    public void generateTripNumber() {
        if (route != null && tripDate != null) {
            String dateStr = tripDate.toLocalDate().toString().replace("-", "");
            String typePrefix = tripType != null ? tripType.name().substring(0, 1) : "T";
            this.tripNumber = route.getRouteNumber() + "-" + dateStr + "-" + typePrefix + "-" + System.currentTimeMillis() % 10000;
        }
    }
    
    public void startTrip() {
        this.actualStartTime = LocalDateTime.now();
        this.status = TripStatus.IN_PROGRESS;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public void endTrip() {
        this.actualEndTime = LocalDateTime.now();
        this.status = TripStatus.COMPLETED;
        this.setUpdatedAt(LocalDateTime.now());
        
        // Calculate trip duration and other metrics
        if (actualStartTime != null && actualEndTime != null) {
            long durationMinutes = java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
            this.actualDurationMinutes = (int) durationMinutes;
            
            if (plannedStartTime != null && plannedEndTime != null) {
                long plannedDuration = java.time.Duration.between(plannedStartTime, plannedEndTime).toMinutes();
                // Set estimated duration if not already set
                if (this.estimatedDurationMinutes == null) {
                    this.estimatedDurationMinutes = (int) plannedDuration;
                }
            }
        }
    }
    
    public void cancelTrip(String reason) {
        this.status = TripStatus.CANCELLED;
        this.delayReason = reason;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public void reportIncident(String details) {
        this.incidentReported = true;
        this.incidentDetails = details;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public void setStartCoordinates(Double latitude, Double longitude) {
        this.startLatitude = latitude;
        this.startLongitude = longitude;
    }
    
    public void setEndCoordinates(Double latitude, Double longitude) {
        this.endLatitude = latitude;
        this.endLongitude = longitude;
    }
    
    public boolean isInProgress() {
        return status == TripStatus.IN_PROGRESS;
    }
    
    public boolean isCompleted() {
        return status == TripStatus.COMPLETED;
    }
    
    public boolean isCancelled() {
        return status == TripStatus.CANCELLED;
    }
    
    public boolean isDelayed() {
        if (actualDurationMinutes != null && estimatedDurationMinutes != null) {
            return actualDurationMinutes > estimatedDurationMinutes + 5; // Consider more than 5 minutes extra as delayed
        }
        return false;
    }
    
    public boolean hasIncident() {
        return incidentReported != null && incidentReported;
    }
    
    public long getTripDurationMinutes() {
        if (actualStartTime != null && actualEndTime != null) {
            return java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
        }
        return 0;
    }
    
    public double getAverageSpeedCalculated() {
        if (distanceTraveledKm != null && getTripDurationMinutes() > 0) {
            return (distanceTraveledKm / getTripDurationMinutes()) * 60; // km/h
        }
        return 0.0;
    }
    
    public boolean isOnTime() {
        if (actualDurationMinutes != null && estimatedDurationMinutes != null) {
            return actualDurationMinutes <= estimatedDurationMinutes + 5; // Consider 5 minutes or less extra as on time
        }
        return true; // If no duration data, assume on time
    }
    
    @Override
    public String toString() {
        return "Trip{" +
                "id=" + getId() +
                ", tripNumber='" + tripNumber + '\'' +
                ", route=" + (route != null ? route.getRouteNumber() : "null") +
                ", rider=" + (rider != null ? rider.getFullName() : "null") +
                ", tripType=" + tripType +
                ", status=" + status +
                ", tripDate=" + (tripDate != null ? tripDate.toLocalDate() : "null") +
                ", passengerCount=" + passengerCount +
                ", isDelayed=" + isDelayed() +
                '}';
    }
}
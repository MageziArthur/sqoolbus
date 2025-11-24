package com.sqool.sqoolbus.tenant.entity.hail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sqool.sqoolbus.tenant.entity.BaseEntity;
import com.sqool.sqoolbus.tenant.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a bus route
 */
@Entity
@Table(name = "route")
public class Route extends BaseEntity {
    
    @Column(name = "route_number", unique = true, nullable = false, length = 50)
    private String routeNumber;
    
    @Column(name = "route_name", nullable = false, length = 200)
    private String routeName;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "route_type", nullable = false)
    private RouteType routeType;
    
    @Column(name = "start_time")
    private LocalTime startTime;
    
    @Column(name = "end_time")
    private LocalTime endTime;
    
    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;
    
    @Column(name = "distance_km")
    private Double distanceKm;
    
    @Column(name = "max_capacity")
    private Integer maxCapacity;
    
    @Column(name = "current_enrollment")
    private Integer currentEnrollment = 0;
    
    @Column(name = "pickup_instructions", length = 1000)
    private String pickupInstructions;
    
    @Column(name = "dropoff_instructions", length = 1000)
    private String dropoffInstructions;
    
    @Column(name = "active_days", length = 50)
    private String activeDays = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY"; // Comma-separated
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RouteStatus status = RouteStatus.ACTIVE;
    
    @Column(name = "priority_level")
    private Integer priorityLevel = 1; // 1=High, 2=Medium, 3=Low
    
    @Column(name = "special_requirements", length = 500)
    private String specialRequirements;
    
    @Column(name = "weather_dependent")
    private Boolean weatherDependent = false;
    
    @Column(name = "seasonal_route")
    private Boolean seasonalRoute = false;
    
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
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pupil> pupils = new ArrayList<>();
    
    // Removed riders mapping to fix startup error
    // @OneToMany(mappedBy = "assignedRoute", fetch = FetchType.LAZY)
    // private List<User> riders = new ArrayList<>();
    
    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Trip> trips = new ArrayList<>();
    
    // Enums
    public enum RouteType {
        PICKUP,     // Morning pickup route
        DROPOFF,    // Afternoon dropoff route
        BOTH,       // Both pickup and dropoff
        FIELD_TRIP, // Special field trip route
        EMERGENCY   // Emergency route
    }
    
    public enum RouteStatus {
        ACTIVE,     // Currently active
        INACTIVE,   // Temporarily inactive
        SUSPENDED,  // Suspended due to issues
        PLANNED,    // Planned but not started
        RETIRED     // Permanently retired
    }
    
    // Constructors
    public Route() {
        super();
    }
    
    public Route(String routeNumber, String routeName, RouteType routeType, School school) {
        super();
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.routeType = routeType;
        this.school = school;
    }
    
    // Getters and Setters
    
    public String getRouteNumber() {
        return routeNumber;
    }
    
    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }
    
    public String getRouteName() {
        return routeName;
    }
    
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public RouteType getRouteType() {
        return routeType;
    }
    
    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public Integer getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }
    
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }
    
    public Double getDistanceKm() {
        return distanceKm;
    }
    
    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
    
    public Integer getMaxCapacity() {
        return maxCapacity;
    }
    
    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    
    public Integer getCurrentEnrollment() {
        return currentEnrollment;
    }
    
    public void setCurrentEnrollment(Integer currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }
    
    public String getPickupInstructions() {
        return pickupInstructions;
    }
    
    public void setPickupInstructions(String pickupInstructions) {
        this.pickupInstructions = pickupInstructions;
    }
    
    public String getDropoffInstructions() {
        return dropoffInstructions;
    }
    
    public void setDropoffInstructions(String dropoffInstructions) {
        this.dropoffInstructions = dropoffInstructions;
    }
    
    public String getActiveDays() {
        return activeDays;
    }
    
    public void setActiveDays(String activeDays) {
        this.activeDays = activeDays;
    }
    
    public RouteStatus getStatus() {
        return status;
    }
    
    public void setStatus(RouteStatus status) {
        this.status = status;
    }
    
    public Integer getPriorityLevel() {
        return priorityLevel;
    }
    
    public void setPriorityLevel(Integer priorityLevel) {
        this.priorityLevel = priorityLevel;
    }
    
    public String getSpecialRequirements() {
        return specialRequirements;
    }
    
    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }
    
    public Boolean getWeatherDependent() {
        return weatherDependent;
    }
    
    public void setWeatherDependent(Boolean weatherDependent) {
        this.weatherDependent = weatherDependent;
    }
    
    public Boolean getSeasonalRoute() {
        return seasonalRoute;
    }
    
    public void setSeasonalRoute(Boolean seasonalRoute) {
        this.seasonalRoute = seasonalRoute;
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
    
    public School getSchool() {
        return school;
    }
    
    public void setSchool(School school) {
        this.school = school;
    }
    
    public List<Pupil> getPupils() {
        return pupils;
    }
    
    public void setPupils(List<Pupil> pupils) {
        this.pupils = pupils;
    }
    
    public List<Trip> getTrips() {
        return trips;
    }
    
    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }
    
    // Utility methods
    public void setStartCoordinates(Double latitude, Double longitude) {
        this.startLatitude = latitude;
        this.startLongitude = longitude;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public void setEndCoordinates(Double latitude, Double longitude) {
        this.endLatitude = latitude;
        this.endLongitude = longitude;
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public boolean hasStartCoordinates() {
        return startLatitude != null && startLongitude != null;
    }
    
    public boolean hasEndCoordinates() {
        return endLatitude != null && endLongitude != null;
    }
    
    public boolean hasAvailableCapacity() {
        return maxCapacity != null && currentEnrollment != null && 
               currentEnrollment < maxCapacity;
    }
    
    public Integer getAvailableCapacity() {
        if (maxCapacity == null || currentEnrollment == null) return null;
        return maxCapacity - currentEnrollment;
    }
    
    public double getCapacityUtilization() {
        if (maxCapacity == null || currentEnrollment == null || maxCapacity == 0) return 0.0;
        return (double) currentEnrollment / maxCapacity * 100;
    }
    
    public boolean isActiveToday(String dayOfWeek) {
        return activeDays != null && activeDays.toUpperCase().contains(dayOfWeek.toUpperCase());
    }
    
    public String getPriorityDescription() {
        if (priorityLevel == null) return "Unknown";
        return switch (priorityLevel) {
            case 1 -> "High Priority";
            case 2 -> "Medium Priority";
            case 3 -> "Low Priority";
            default -> "Unknown Priority";
        };
    }
    
    public void addPupil(Pupil pupil) {
        pupils.add(pupil);
        pupil.setRoute(this);
        // Auto-update current enrollment
        this.currentEnrollment = pupils.size();
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public void removePupil(Pupil pupil) {
        pupils.remove(pupil);
        pupil.setRoute(null);
        // Auto-update current enrollment
        this.currentEnrollment = pupils.size();
        this.setUpdatedAt(LocalDateTime.now());
    }
    
    public int getActualEnrollment() {
        return pupils != null ? pupils.size() : 0;
    }
    
    public boolean isPupilEnrolled(Pupil pupil) {
        return pupils != null && pupils.contains(pupil);
    }
    
    public int getAssignedRidersCount() {
        return 0; // Simplified since riders mapping was removed
    }
    
    public boolean hasDriver() {
        // Simplified since riders mapping was removed
        return true; // Assume routes have drivers for now
    }
    
    public User getPrimaryDriver() {
        // Simplified since riders mapping was removed
        return null; // No driver relationship available
    }
    
    public boolean isReadyToOperate() {
        return hasDriver() && status == RouteStatus.ACTIVE;
    }
    
    public Trip getCurrentTrip() {
        return trips.stream()
                .filter(Trip::isInProgress)
                .findFirst()
                .orElse(null);
    }
    
    public boolean hasActiveTrip() {
        return getCurrentTrip() != null;
    }
    
    public int getTotalTripsCount() {
        return trips != null ? trips.size() : 0;
    }
    
    public int getCompletedTripsToday() {
        if (trips == null) return 0;
        return (int) trips.stream()
                .filter(trip -> trip.isCompleted() && 
                        trip.getTripDate().toLocalDate().equals(java.time.LocalDate.now()))
                .count();
    }
    
    public double getOnTimePerformance() {
        if (trips == null || trips.isEmpty()) return 0.0;
        long onTimeTrips = trips.stream()
                .filter(trip -> trip.isCompleted() && trip.isOnTime())
                .count();
        long completedTrips = trips.stream()
                .filter(Trip::isCompleted)
                .count();
        return completedTrips > 0 ? (double) onTimeTrips / completedTrips * 100 : 0.0;
    }
    
    @Override
    public String toString() {
        return "Route{" +
                "id=" + getId() +
                ", routeNumber='" + routeNumber + '\'' +
                ", routeName='" + routeName + '\'' +
                ", routeType=" + routeType +
                ", status=" + status +
                ", school=" + (school != null ? school.getName() : "null") +
                ", enrolledPupils=" + getActualEnrollment() +
                ", assignedRiders=" + getAssignedRidersCount() +
                ", maxCapacity=" + maxCapacity +
                '}';
    }
}
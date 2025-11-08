package com.sqool.sqoolbus.tenant.controller;

import com.sqool.sqoolbus.security.Permission;
import com.sqool.sqoolbus.security.RequirePermissions;
import com.sqool.sqoolbus.tenant.entity.hail.Trip;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import com.sqool.sqoolbus.tenant.entity.hail.Route;
import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.service.TripService;
import com.sqool.sqoolbus.tenant.service.SchoolService;
import com.sqool.sqoolbus.tenant.service.RouteService;
import com.sqool.sqoolbus.tenant.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing trips
 */
@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*")
@Tag(name = "Trip Management", description = "APIs for managing school bus trips and real-time tracking")
public class TripController {
    
    @Autowired
    private TripService tripService;
    
    @Autowired
    private RouteService routeService;
    
    @Autowired
    private UserManagementService userManagementService;
    
    @GetMapping
    @Operation(summary = "Get all trips", description = "Retrieve a list of all trips in the system")
    @SecurityRequirement(name = "bearerAuth")
    @RequirePermissions(Permission.PERM_VIEW_TRIPS)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved trips",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Trip.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Trip>> getAllTrips() {
        List<Trip> trips = tripService.findAll();
        return ResponseEntity.ok(trips);
    }
    
    @GetMapping("/{id}")
    @RequirePermissions(Permission.PERM_VIEW_TRIPS)
    public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
        Optional<Trip> trip = tripService.findById(id);
        return trip.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/route/{routeId}")
    @RequirePermissions(Permission.PERM_VIEW_TRIPS)
    public ResponseEntity<List<Trip>> getTripsByRoute(@PathVariable Long routeId) {
        List<Trip> trips = tripService.findByRouteId(routeId);
        return ResponseEntity.ok(trips);
    }
    
    @GetMapping("/status/{status}")
    @RequirePermissions(Permission.PERM_VIEW_TRIPS)
    public ResponseEntity<List<Trip>> getTripsByStatus(@PathVariable String status) {
        List<Trip> trips = tripService.findByStatus(status);
        return ResponseEntity.ok(trips);
    }
    
    @GetMapping("/active")
    @RequirePermissions(Permission.PERM_VIEW_TRIPS)
    public ResponseEntity<List<Trip>> getActiveTrips() {
        List<Trip> activeTrips = tripService.findActiveTrips();
        return ResponseEntity.ok(activeTrips);
    }
    
    @GetMapping("/today")
    @RequirePermissions(Permission.PERM_VIEW_TRIPS)
    public ResponseEntity<List<Trip>> getTodaysTrips() {
        List<Trip> todaysTrips = tripService.findTodaysTrips();
        return ResponseEntity.ok(todaysTrips);
    }
    
    @PostMapping
    @RequirePermissions(Permission.PERM_CREATE_TRIPS)
    public ResponseEntity<Trip> createTrip(@Valid @RequestBody CreateTripRequest request) {
        try {
            Optional<Route> route = routeService.findById(request.getRouteId());
            if (!route.isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            User rider = userManagementService.findUserById(request.getRiderId());
            if (rider == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Trip trip = new Trip();
            trip.setRoute(route.get());
            trip.setRider(rider);
            Trip savedTrip = tripService.save(trip);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTrip);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/start")
    @RequirePermissions(Permission.PERM_START_TRIPS)
    public ResponseEntity<Trip> startTrip(@PathVariable Long id) {
        try {
            Trip trip = tripService.startTrip(id);
            return ResponseEntity.ok(trip);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/complete")
    @RequirePermissions(Permission.PERM_END_TRIPS)
    public ResponseEntity<Trip> completeTrip(@PathVariable Long id) {
        try {
            Trip trip = tripService.completeTrip(id);
            return ResponseEntity.ok(trip);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/passenger-count")
    @RequirePermissions(Permission.PERM_UPDATE_TRIPS)
    public ResponseEntity<Trip> updatePassengerCount(@PathVariable Long id, @RequestBody UpdatePassengerCountRequest request) {
        try {
            Trip trip = tripService.updatePassengerCount(id, request.getPassengerCount());
            return ResponseEntity.ok(trip);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @RequirePermissions(Permission.PERM_DELETE_TRIPS)
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        try {
            tripService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Request DTOs
    public static class CreateTripRequest {
        private Long routeId;
        private Long riderId;
        private String tripType;
        private LocalDateTime plannedStartTime;
        private LocalDateTime plannedEndTime;
        
        // Getters and setters
        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
        
        public Long getRiderId() { return riderId; }
        public void setRiderId(Long riderId) { this.riderId = riderId; }
        
        public String getTripType() { return tripType; }
        public void setTripType(String tripType) { this.tripType = tripType; }
        
        public LocalDateTime getPlannedStartTime() { return plannedStartTime; }
        public void setPlannedStartTime(LocalDateTime plannedStartTime) { this.plannedStartTime = plannedStartTime; }
        
        public LocalDateTime getPlannedEndTime() { return plannedEndTime; }
        public void setPlannedEndTime(LocalDateTime plannedEndTime) { this.plannedEndTime = plannedEndTime; }
    }
    
    public static class UpdateLocationRequest {
        private Double latitude;
        private Double longitude;
        
        // Getters and setters
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }
    
    public static class UpdatePassengerCountRequest {
        private Integer passengerCount;
        
        // Getters and setters
        public Integer getPassengerCount() { return passengerCount; }
        public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }
    }
    
    public static class ReportDelayRequest {
        private String reason;
        
        // Getters and setters
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
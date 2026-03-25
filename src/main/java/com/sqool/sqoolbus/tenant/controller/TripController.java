package com.sqool.sqoolbus.tenant.controller;

import com.sqool.sqoolbus.dto.TripDetailResponse;
import com.sqool.sqoolbus.security.Permission;
import com.sqool.sqoolbus.security.RequirePermissions;
import com.sqool.sqoolbus.tenant.entity.hail.Trip;
import com.sqool.sqoolbus.tenant.entity.hail.TripStudentCheckout;
import com.sqool.sqoolbus.tenant.entity.hail.TripTrailPoint;
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

    @GetMapping("/driver/{driverId}")
    @RequirePermissions(Permission.PERM_VIEW_TRIPS)
    public ResponseEntity<List<Trip>> getTripsByDriver(@PathVariable Long driverId) {
        List<Trip> trips = tripService.findByDriverId(driverId);
        return ResponseEntity.ok(trips);
    }
    
    @GetMapping("/status/{status}")
    @RequirePermissions(Permission.PERM_VIEW_TRIPS)
    public ResponseEntity<List<Trip>> getTripsByStatus(@PathVariable String status) {
        String normalized = status == null ? "" : status.trim().toLowerCase();

        String internalStatus;
        switch (normalized) {
            case "completed":
                internalStatus = "COMPLETED";
                break;
            case "inprogress":
            case "in_progress":
                internalStatus = "IN_PROGRESS";
                break;
            case "paused":
                internalStatus = "SUSPENDED";
                break;
            case "cancelled":
            case "canceled":
                internalStatus = "CANCELLED";
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        List<Trip> trips = tripService.findByStatus(internalStatus);
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
            trip.setRoute(route.get()); // This automatically sets school from route
            trip.setRider(rider);
            
            // Set trip type
            if (request.getTripType() != null) {
                try {
                    trip.setTripType(Trip.TripType.valueOf(request.getTripType()));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().build();
                }
            }
            
            // Set planned times
            if (request.getPlannedStartTime() != null) {
                trip.setPlannedStartTime(request.getPlannedStartTime());
            }
            if (request.getPlannedEndTime() != null) {
                trip.setPlannedEndTime(request.getPlannedEndTime());
            }
            
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

    @PostMapping("/driver/start")
    @RequirePermissions(Permission.PERM_START_TRIPS)
    @Operation(summary = "Driver start trip", description = "Driver starts a trip by selecting assigned route, bus, and optional initial student check-ins")
    public ResponseEntity<Trip> driverStartTrip(@Valid @RequestBody DriverStartTripRequest request) {
        try {
            Trip.TripType tripType = request.getTripType() != null
                    ? Trip.TripType.valueOf(request.getTripType())
                    : Trip.TripType.PICKUP;

            Trip trip = tripService.startDriverTrip(
                    request.getRouteId(),
                    request.getBusId(),
                    tripType,
                    request.getPlannedStartTime(),
                    request.getPlannedEndTime(),
                    request.getCheckedInPupilIds()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(trip);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/route/{routeId}/start-with-checkins")
    @RequirePermissions(Permission.PERM_START_TRIPS)
    @Operation(summary = "Create trip with checked-in students", description = "One-time trip creation for a single route with an optional list of checked-in students")
    public ResponseEntity<Trip> createTripWithCheckedInStudents(
            @PathVariable Long routeId,
            @Valid @RequestBody RouteTripStartRequest request) {
        try {
            Trip.TripType tripType = request.getTripType() != null
                    ? Trip.TripType.valueOf(request.getTripType())
                    : Trip.TripType.PICKUP;

            Trip trip = tripService.startDriverTrip(
                    routeId,
                    request.getBusId(),
                    tripType,
                    request.getPlannedStartTime(),
                    request.getPlannedEndTime(),
                    request.getCheckedInPupilIds()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(trip);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/check-in")
    @RequirePermissions(Permission.PERM_UPDATE_TRIPS)
    @Operation(summary = "Check in one student", description = "Check in one pupil assigned to the trip route")
    public ResponseEntity<Trip> checkInOneStudent(@PathVariable Long id, @RequestBody StudentCheckInRequest request) {
        try {
            Trip trip = tripService.checkInPupil(id, request.getPupilId());
            return ResponseEntity.ok(trip);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/check-in/bulk")
    @RequirePermissions(Permission.PERM_UPDATE_TRIPS)
    @Operation(summary = "Check in multiple students", description = "Check in multiple pupils assigned to the trip route")
    public ResponseEntity<Trip> checkInStudentsBulk(@PathVariable Long id, @RequestBody BulkStudentCheckInRequest request) {
        try {
            Trip trip = tripService.checkInPupilsBulk(id, request.getPupilIds());
            return ResponseEntity.ok(trip);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/pause")
    @RequirePermissions(Permission.PERM_UPDATE_TRIPS)
    @Operation(summary = "Pause trip", description = "Pause an in-progress trip with optional reason")
    public ResponseEntity<Trip> pauseTrip(@PathVariable Long id, @RequestBody(required = false) StopOrPauseRequest request) {
        try {
            String reason = request != null ? request.getReason() : null;
            Trip trip = tripService.pauseTrip(id, reason);
            return ResponseEntity.ok(trip);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/resume")
    @RequirePermissions(Permission.PERM_UPDATE_TRIPS)
    @Operation(summary = "Resume trip", description = "Resume a paused trip")
    public ResponseEntity<Trip> resumeTrip(@PathVariable Long id) {
        try {
            Trip trip = tripService.resumeTrip(id);
            return ResponseEntity.ok(trip);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/stop")
    @RequirePermissions(Permission.PERM_END_TRIPS)
    @Operation(summary = "Stop trip", description = "Stop an in-progress or paused trip with reason")
    public ResponseEntity<Trip> stopTrip(@PathVariable Long id, @RequestBody StopOrPauseRequest request) {
        try {
            Trip trip = tripService.stopTrip(id, request.getReason());
            return ResponseEntity.ok(trip);
        } catch (RuntimeException e) {
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

    @PutMapping("/{id}")
    @RequirePermissions(Permission.PERM_UPDATE_TRIPS)
    @Operation(summary = "Update trip", description = "Update trip details. Route and rider remain unchanged")
    public ResponseEntity<Trip> updateTrip(@PathVariable Long id, @Valid @RequestBody UpdateTripRequest request) {
        try {
            Trip tripDetails = new Trip();

            if (request.getStatus() != null) {
                tripDetails.setStatus(Trip.TripStatus.valueOf(request.getStatus()));
            }

            if (request.getTripType() != null) {
                tripDetails.setTripType(Trip.TripType.valueOf(request.getTripType()));
            }

            tripDetails.setPlannedStartTime(request.getPlannedStartTime());
            tripDetails.setPlannedEndTime(request.getPlannedEndTime());
            tripDetails.setPassengerCount(request.getPassengerCount());
            tripDetails.setPickedUpCount(request.getPickedUpCount());
            tripDetails.setDroppedOffCount(request.getDroppedOffCount());
            tripDetails.setStartLocation(request.getStartLocation());
            tripDetails.setEndLocation(request.getEndLocation());
            tripDetails.setNotes(request.getNotes());
            tripDetails.setDelayReason(request.getDelayReason());

            Trip updatedTrip = tripService.update(id, tripDetails);
            return ResponseEntity.ok(updatedTrip);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
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

    @PostMapping("/{id}/trail")
    @RequirePermissions(Permission.PERM_UPDATE_TRIPS)
    @Operation(summary = "Record trip trail point", description = "Record mobile GPS x,y coordinate for a trip for historical tracking")
    public ResponseEntity<TripTrailPoint> recordTripTrailPoint(@PathVariable Long id, @RequestBody TripTrailPointRequest request) {
        try {
            TripTrailPoint point = tripService.recordTripTrailPoint(id, request.getLatitude(), request.getLongitude(), request.getRecordedAt());
            return ResponseEntity.status(HttpStatus.CREATED).body(point);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/trail")
    @RequirePermissions(Permission.PERM_VIEW_TRIPS)
    @Operation(summary = "Get trip trail history", description = "Retrieve historical GPS points captured during the trip")
    public ResponseEntity<List<TripTrailPoint>> getTripTrailHistory(@PathVariable Long id) {
        try {
            List<TripTrailPoint> points = tripService.getTripTrailHistory(id);
            return ResponseEntity.ok(points);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/checkout")
    @RequirePermissions(Permission.PERM_UPDATE_TRIPS)
    @Operation(summary = "Checkout one student", description = "Checkout one student from the trip while en route")
    public ResponseEntity<TripStudentCheckout> checkoutOneStudent(@PathVariable Long id, @RequestBody TripStudentCheckoutRequest request) {
        try {
            TripStudentCheckout checkout = tripService.checkOutStudent(
                    id,
                    request.getPupilId(),
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getNotes(),
                    request.getCheckoutTime()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(checkout);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/checkout/bulk")
    @RequirePermissions(Permission.PERM_UPDATE_TRIPS)
    @Operation(summary = "Checkout multiple students", description = "Checkout multiple students from the trip in one request")
    public ResponseEntity<List<TripStudentCheckout>> checkoutMultipleStudents(@PathVariable Long id,
                                                                              @RequestBody BulkTripStudentCheckoutRequest request) {
        try {
            List<TripStudentCheckout> checkouts = tripService.checkOutStudentsBulk(
                    id,
                    request.getPupilIds(),
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getNotes(),
                    request.getCheckoutTime()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(checkouts);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/checkouts")
    @RequirePermissions(Permission.PERM_VIEW_TRIPS)
    @Operation(summary = "Get student checkout history", description = "Retrieve historical student checkout events for the trip")
    public ResponseEntity<List<TripStudentCheckout>> getTripStudentCheckouts(@PathVariable Long id) {
        try {
            List<TripStudentCheckout> checkouts = tripService.getTripStudentCheckouts(id);
            return ResponseEntity.ok(checkouts);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/detail")
    @RequirePermissions(Permission.PERM_VIEW_TRIPS)
    @Operation(summary = "Get trip detail", description = "Retrieve trip detail including realtime logs, checked-in students, and checkout events with their locations")
    public ResponseEntity<TripDetailResponse> getTripDetail(@PathVariable Long id) {
        try {
            TripDetailResponse detail = tripService.getTripDetail(id);
            return ResponseEntity.ok(detail);
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

    public static class DriverStartTripRequest {
        private Long routeId;
        private Long busId;
        private String tripType;
        private LocalDateTime plannedStartTime;
        private LocalDateTime plannedEndTime;
        private List<Long> checkedInPupilIds;

        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }

        public Long getBusId() { return busId; }
        public void setBusId(Long busId) { this.busId = busId; }

        public String getTripType() { return tripType; }
        public void setTripType(String tripType) { this.tripType = tripType; }

        public LocalDateTime getPlannedStartTime() { return plannedStartTime; }
        public void setPlannedStartTime(LocalDateTime plannedStartTime) { this.plannedStartTime = plannedStartTime; }

        public LocalDateTime getPlannedEndTime() { return plannedEndTime; }
        public void setPlannedEndTime(LocalDateTime plannedEndTime) { this.plannedEndTime = plannedEndTime; }

        public List<Long> getCheckedInPupilIds() { return checkedInPupilIds; }
        public void setCheckedInPupilIds(List<Long> checkedInPupilIds) { this.checkedInPupilIds = checkedInPupilIds; }
    }

    public static class RouteTripStartRequest {
        private Long busId;
        private String tripType;
        private LocalDateTime plannedStartTime;
        private LocalDateTime plannedEndTime;
        private List<Long> checkedInPupilIds;

        public Long getBusId() { return busId; }
        public void setBusId(Long busId) { this.busId = busId; }

        public String getTripType() { return tripType; }
        public void setTripType(String tripType) { this.tripType = tripType; }

        public LocalDateTime getPlannedStartTime() { return plannedStartTime; }
        public void setPlannedStartTime(LocalDateTime plannedStartTime) { this.plannedStartTime = plannedStartTime; }

        public LocalDateTime getPlannedEndTime() { return plannedEndTime; }
        public void setPlannedEndTime(LocalDateTime plannedEndTime) { this.plannedEndTime = plannedEndTime; }

        public List<Long> getCheckedInPupilIds() { return checkedInPupilIds; }
        public void setCheckedInPupilIds(List<Long> checkedInPupilIds) { this.checkedInPupilIds = checkedInPupilIds; }
    }

    public static class UpdateTripRequest {
        private String status;
        private String tripType;
        private LocalDateTime plannedStartTime;
        private LocalDateTime plannedEndTime;
        private Integer passengerCount;
        private Integer pickedUpCount;
        private Integer droppedOffCount;
        private String startLocation;
        private String endLocation;
        private String notes;
        private String delayReason;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getTripType() { return tripType; }
        public void setTripType(String tripType) { this.tripType = tripType; }

        public LocalDateTime getPlannedStartTime() { return plannedStartTime; }
        public void setPlannedStartTime(LocalDateTime plannedStartTime) { this.plannedStartTime = plannedStartTime; }

        public LocalDateTime getPlannedEndTime() { return plannedEndTime; }
        public void setPlannedEndTime(LocalDateTime plannedEndTime) { this.plannedEndTime = plannedEndTime; }

        public Integer getPassengerCount() { return passengerCount; }
        public void setPassengerCount(Integer passengerCount) { this.passengerCount = passengerCount; }

        public Integer getPickedUpCount() { return pickedUpCount; }
        public void setPickedUpCount(Integer pickedUpCount) { this.pickedUpCount = pickedUpCount; }

        public Integer getDroppedOffCount() { return droppedOffCount; }
        public void setDroppedOffCount(Integer droppedOffCount) { this.droppedOffCount = droppedOffCount; }

        public String getStartLocation() { return startLocation; }
        public void setStartLocation(String startLocation) { this.startLocation = startLocation; }

        public String getEndLocation() { return endLocation; }
        public void setEndLocation(String endLocation) { this.endLocation = endLocation; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public String getDelayReason() { return delayReason; }
        public void setDelayReason(String delayReason) { this.delayReason = delayReason; }
    }

    public static class StudentCheckInRequest {
        private Long pupilId;

        public Long getPupilId() { return pupilId; }
        public void setPupilId(Long pupilId) { this.pupilId = pupilId; }
    }

    public static class BulkStudentCheckInRequest {
        private List<Long> pupilIds;

        public List<Long> getPupilIds() { return pupilIds; }
        public void setPupilIds(List<Long> pupilIds) { this.pupilIds = pupilIds; }
    }

    public static class StopOrPauseRequest {
        private String reason;

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class TripTrailPointRequest {
        private Double latitude;
        private Double longitude;
        private LocalDateTime recordedAt;

        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }

        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }

        public LocalDateTime getRecordedAt() { return recordedAt; }
        public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
    }

    public static class TripStudentCheckoutRequest {
        private Long pupilId;
        private Double latitude;
        private Double longitude;
        private String notes;
        private LocalDateTime checkoutTime;

        public Long getPupilId() { return pupilId; }
        public void setPupilId(Long pupilId) { this.pupilId = pupilId; }

        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }

        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public LocalDateTime getCheckoutTime() { return checkoutTime; }
        public void setCheckoutTime(LocalDateTime checkoutTime) { this.checkoutTime = checkoutTime; }
    }

    public static class BulkTripStudentCheckoutRequest {
        private List<Long> pupilIds;
        private Double latitude;
        private Double longitude;
        private String notes;
        private LocalDateTime checkoutTime;

        public List<Long> getPupilIds() { return pupilIds; }
        public void setPupilIds(List<Long> pupilIds) { this.pupilIds = pupilIds; }

        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }

        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public LocalDateTime getCheckoutTime() { return checkoutTime; }
        public void setCheckoutTime(LocalDateTime checkoutTime) { this.checkoutTime = checkoutTime; }
    }
}
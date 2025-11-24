package com.sqool.sqoolbus.tenant.controller;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sqool.sqoolbus.config.FlexibleLocalTimeDeserializer;
import com.sqool.sqoolbus.dto.ErrorResponse;
import com.sqool.sqoolbus.security.Permission;
import com.sqool.sqoolbus.security.RequirePermissions;
import com.sqool.sqoolbus.tenant.entity.hail.Route;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import com.sqool.sqoolbus.tenant.service.RouteService;
import com.sqool.sqoolbus.tenant.service.SchoolService;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing routes
 */
@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "*")
@Tag(name = "Route Management", description = "APIs for managing school bus routes")
@SecurityRequirement(name = "bearerAuth")
public class RouteController {
    
    @Autowired
    private RouteService routeService;
    
    @Autowired
    private SchoolService schoolService;
    
    @GetMapping
    @RequirePermissions(Permission.PERM_VIEW_ROUTES)
    @Operation(summary = "Get all routes", description = "Retrieve a list of all routes in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved routes",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Route.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<Route>> getAllRoutes() {
        List<Route> routes = routeService.findAll();
        return ResponseEntity.ok(routes);
    }
    
    @GetMapping("/{id}")
    @RequirePermissions(Permission.PERM_VIEW_ROUTES)
    @Operation(summary = "Get route by ID", description = "Retrieve a specific route by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Route found",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Route.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Route not found",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Route> getRouteById(
            @Parameter(description = "ID of the route to retrieve", required = true)
            @PathVariable Long id) {
        Optional<Route> route = routeService.findById(id);
        return route.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/number/{routeNumber}")
    @RequirePermissions(Permission.PERM_VIEW_ROUTES)
    public ResponseEntity<Route> getRouteByNumber(@PathVariable String routeNumber) {
        Optional<Route> route = routeService.findByRouteNumber(routeNumber);
        return route.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/school/{schoolId}")
    @RequirePermissions(Permission.PERM_VIEW_ROUTES)
    @Operation(summary = "Get routes by school", description = "Retrieve all routes belonging to a specific school")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Routes retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Route.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have permission to view routes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<Route>> getRoutesBySchool(
            @Parameter(description = "ID of the school", required = true)
            @PathVariable Long schoolId) {
        List<Route> routes = routeService.findBySchoolId(schoolId);
        return ResponseEntity.ok(routes);
    }
    
    @PostMapping
    @RequirePermissions(Permission.PERM_CREATE_ROUTES)
    public ResponseEntity<Route> createRoute(@Valid @RequestBody CreateRouteRequest request) {
        try {
            Optional<School> school = schoolService.findById(request.getSchoolId());
            if (!school.isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            Route route = new Route();
            route.setRouteNumber(request.getRouteNumber());
            route.setRouteName(request.getRouteName());
            route.setDescription(request.getDescription());
            route.setRouteType(Route.RouteType.valueOf(request.getRouteType()));
            route.setStartTime(request.getStartTime());
            route.setEndTime(request.getEndTime());
            route.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
            route.setDistanceKm(request.getDistanceKm());
            route.setMaxCapacity(request.getMaxCapacity());
            route.setPickupInstructions(request.getPickupInstructions());
            route.setSchool(school.get());
            
            Route savedRoute = routeService.save(route);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRoute);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @RequirePermissions(Permission.PERM_UPDATE_ROUTES)
    @Operation(summary = "Update route", description = "Update an existing route")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route updated successfully"),
            @ApiResponse(responseCode = "404", description = "Route not found",
                        content = @Content(mediaType = "application/json",
                                         schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                        content = @Content(mediaType = "application/json",
                                         schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                        content = @Content(mediaType = "application/json",
                                         schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                        content = @Content(mediaType = "application/json",
                                         schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Route> updateRoute(@PathVariable Long id, @Valid @RequestBody Route routeDetails) {
        Route updatedRoute = routeService.update(id, routeDetails);
        return ResponseEntity.ok(updatedRoute);
    }
    
    @DeleteMapping("/{id}")
    @RequirePermissions(Permission.PERM_DELETE_ROUTES)
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        try {
            routeService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/active")
    @RequirePermissions(Permission.PERM_VIEW_ROUTES)
    public ResponseEntity<List<Route>> getActiveRoutes() {
        List<Route> activeRoutes = routeService.findActiveRoutes();
        return ResponseEntity.ok(activeRoutes);
    }
    
    @GetMapping("/type/{routeType}")
    @RequirePermissions(Permission.PERM_VIEW_ROUTES)
    public ResponseEntity<List<Route>> getRoutesByType(@PathVariable String routeType) {
        List<Route> routes = routeService.findByType(routeType);
        return ResponseEntity.ok(routes);
    }
    
    @PostMapping("/{id}/assign-rider/{riderId}")
    @RequirePermissions(Permission.PERM_ASSIGN_ROUTES)
    @Operation(summary = "Assign rider to route", description = "Assign a rider to a specific route")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rider assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Route or rider not found",
                        content = @Content(mediaType = "application/json",
                                         schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                        content = @Content(mediaType = "application/json",
                                         schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                        content = @Content(mediaType = "application/json",
                                         schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Route> assignRider(@PathVariable Long id, @PathVariable Long riderId) {
        Route route = routeService.assignRider(id, riderId);
        return ResponseEntity.ok(route);
    }
    
    @DeleteMapping("/{id}/remove-rider/{riderId}")
    @RequirePermissions(Permission.PERM_ASSIGN_ROUTES)
    @Operation(summary = "Remove rider from route", description = "Remove a rider from a specific route")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rider removed successfully"),
            @ApiResponse(responseCode = "404", description = "Route or rider not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Route> removeRider(@PathVariable Long id, @PathVariable Long riderId) {
        Route route = routeService.removeRider(id, riderId);
        return ResponseEntity.ok(route);
    }
    
    @PostMapping("/{id}/assign-pupils")
    @RequirePermissions(Permission.PERM_ASSIGN_ROUTES)
    @Operation(summary = "Assign multiple pupils to route", description = "Assign multiple pupils to a specific route at once")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pupils assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Route not found"),
            @ApiResponse(responseCode = "400", description = "Invalid pupil IDs"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Route> assignMultiplePupils(
            @Parameter(description = "Route ID") @PathVariable Long id,
            @Parameter(description = "List of pupil IDs to assign") @RequestBody List<Long> pupilIds) {
        try {
            Route route = routeService.assignMultiplePupils(id, pupilIds);
            return ResponseEntity.ok(route);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}/remove-pupils")
    @RequirePermissions(Permission.PERM_ASSIGN_ROUTES)
    @Operation(summary = "Remove multiple pupils from route", description = "Remove multiple pupils from a specific route at once")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pupils removed successfully"),
            @ApiResponse(responseCode = "404", description = "Route not found"),
            @ApiResponse(responseCode = "400", description = "Invalid pupil IDs"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Route> removeMultiplePupils(
            @Parameter(description = "Route ID") @PathVariable Long id,
            @Parameter(description = "List of pupil IDs to remove") @RequestBody List<Long> pupilIds) {
        try {
            Route route = routeService.removeMultiplePupils(id, pupilIds);
            return ResponseEntity.ok(route);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Request DTO
    public static class CreateRouteRequest {
        private String routeNumber;
        private String routeName;
        private String description;
        private String routeType;
        
        @JsonDeserialize(using = FlexibleLocalTimeDeserializer.class)
        private LocalTime startTime;
        
        @JsonDeserialize(using = FlexibleLocalTimeDeserializer.class)
        private LocalTime endTime;
        
        private Integer estimatedDurationMinutes;
        private Double distanceKm;
        private Integer maxCapacity;
        private String pickupInstructions;
        private Long schoolId;
        
        // Getters and setters
        public String getRouteNumber() { return routeNumber; }
        public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
        
        public String getRouteName() { return routeName; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getRouteType() { return routeType; }
        public void setRouteType(String routeType) { this.routeType = routeType; }
        
        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
        
        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
        
        public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
        public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }
        
        public Double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
        
        public Integer getMaxCapacity() { return maxCapacity; }
        public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }
        
        public String getPickupInstructions() { return pickupInstructions; }
        public void setPickupInstructions(String pickupInstructions) { this.pickupInstructions = pickupInstructions; }
        
        public Long getSchoolId() { return schoolId; }
        public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    }
}
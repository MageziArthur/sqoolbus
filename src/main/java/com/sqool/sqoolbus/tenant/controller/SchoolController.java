package com.sqool.sqoolbus.tenant.controller;

import com.sqool.sqoolbus.dto.AddDriverToSchoolRequest;
import com.sqool.sqoolbus.dto.DriverDetailsResponse;
import com.sqool.sqoolbus.dto.ErrorResponse;
import com.sqool.sqoolbus.master.entity.User;
import com.sqool.sqoolbus.security.Permission;
import com.sqool.sqoolbus.security.RequirePermissions;
import com.sqool.sqoolbus.tenant.entity.hail.School;
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
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing schools
 */
@RestController
@RequestMapping("/api/schools")
@CrossOrigin(origins = "*")
@Tag(name = "School Management", description = "APIs for managing schools in the system")
public class SchoolController {
    
    @Autowired
    private SchoolService schoolService;
    
    @GetMapping
    @RequirePermissions(Permission.PERM_VIEW_SCHOOLS)
    @Operation(summary = "Get all schools", description = "Retrieve a list of all schools in the system")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved schools",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = School.class))),
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
    public ResponseEntity<List<School>> getAllSchools() {
        List<School> schools = schoolService.findAll();
        return ResponseEntity.ok(schools);
    }
    
    @GetMapping("/{id}")
    @RequirePermissions(Permission.PERM_VIEW_SCHOOLS)
    @Operation(summary = "Get school by ID", description = "Retrieve a specific school by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "School found",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = School.class))),
        @ApiResponse(responseCode = "404", description = "School not found",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<School> getSchoolById(
            @Parameter(description = "ID of the school to retrieve", required = true)
            @PathVariable Long id) {
        Optional<School> school = schoolService.findById(id);
        return school.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/code/{schoolCode}")
    @RequirePermissions(Permission.PERM_VIEW_SCHOOLS)
    @Operation(summary = "Get school by code", description = "Retrieve a specific school by its school code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "School found",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = School.class))),
        @ApiResponse(responseCode = "404", description = "School not found",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<School> getSchoolByCode(
            @Parameter(description = "School code to search for", required = true)
            @PathVariable String schoolCode) {
        Optional<School> school = schoolService.findBySchoolCode(schoolCode);
        return school.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @RequirePermissions(Permission.PERM_CREATE_SCHOOLS)
    @Operation(summary = "Create a new school", description = "School creation is disabled in tenant context")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "405", description = "School creation is not allowed in tenant context"),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class))),
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
    public ResponseEntity<School> createSchool(
            @Parameter(description = "School data to create", required = true)
            @Valid @RequestBody com.sqool.sqoolbus.dto.CreateSchoolRequest request) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
    
    @PutMapping("/{id}")
    @RequirePermissions(Permission.PERM_UPDATE_SCHOOLS)
    @Operation(summary = "Update a school", description = "Update an existing school by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "School updated successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = School.class))),
        @ApiResponse(responseCode = "404", description = "School not found",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<School> updateSchool(
            @Parameter(description = "ID of the school to update", required = true)
            @PathVariable Long id, 
            @Parameter(description = "Updated school data", required = true)
            @Valid @RequestBody School schoolDetails) {
        try {
            School updatedSchool = schoolService.update(id, schoolDetails);
            return ResponseEntity.ok(updatedSchool);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @RequirePermissions(Permission.PERM_DELETE_SCHOOLS)
    @Operation(summary = "Delete a school", description = "Delete a school by ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "School deleted successfully"),
        @ApiResponse(responseCode = "404", description = "School not found",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class))),
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
    public ResponseEntity<Void> deleteSchool(
            @Parameter(description = "ID of the school to delete", required = true)
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
    
    @GetMapping("/active")
    @RequirePermissions(Permission.PERM_VIEW_SCHOOLS)
    @Operation(
        summary = "Get all active schools",
        description = "Retrieve a list of all schools that are currently marked as active in the system"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of active schools",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = School.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - insufficient permissions",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<List<School>> getActiveSchools() {
        List<School> activeSchools = schoolService.findActiveSchools();
        return ResponseEntity.ok(activeSchools);
    }
    
    @GetMapping("/type/{schoolType}")
    @RequirePermissions(Permission.PERM_VIEW_SCHOOLS)
    @Operation(
        summary = "Get schools by type",
        description = "Retrieve a list of schools filtered by their type (e.g., Elementary, Middle, High School)"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved schools of the specified type",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = School.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - insufficient permissions",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<List<School>> getSchoolsByType(
            @Parameter(description = "Type of school to filter by", required = true, example = "Elementary")
            @PathVariable String schoolType) {
        List<School> schools = schoolService.findByType(schoolType);
        return ResponseEntity.ok(schools);
    }
    
    @GetMapping("/{id}/drivers")
    @RequirePermissions(Permission.PERM_VIEW_SCHOOLS)
    @Operation(
        summary = "Get all drivers for a school", 
        description = "Retrieve a list of all drivers (existing users with RIDER role from master database) assigned to a specific school. " +
                     "These are users who registered via the rider signup flow and have been assigned to this school."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved list of drivers",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DriverDetailsResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "School not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - insufficient permissions",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<List<DriverDetailsResponse>> getSchoolDrivers(
            @Parameter(description = "ID of the school to retrieve drivers for", required = true, example = "1")
            @PathVariable Long id) {
        try {
            List<DriverDetailsResponse> drivers = schoolService.listDriversForSchool(id);
            return ResponseEntity.ok(drivers);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/drivers")
    @RequirePermissions(Permission.PERM_CREATE_USERS)
    @Operation(
        summary = "Assign an existing driver to a school",
        description = "Assign an existing driver (user from master database) to the specified school by creating a mapping. " +
                     "Note: Drivers must be created first via the rider signup endpoint (POST /api/users/rider). " +
                     "This endpoint only creates the school-driver assignment, similar to how parents are assigned to children."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Driver assigned to school successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = com.sqool.sqoolbus.master.entity.SchoolDriverMapping.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data or driver already assigned to this school",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - insufficient permissions",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "School or driver not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<?> addDriverToSchool(
            @Parameter(description = "ID of the school to assign the driver to", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Driver assignment information", required = true)
            @Valid @RequestBody AddDriverToSchoolRequest request) {
        try {
            com.sqool.sqoolbus.master.entity.SchoolDriverMapping mapping = schoolService.addDriverToSchool(
                id,
                request.getDriverId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(mapping);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse());
        }
    }
    
    @GetMapping("/{id}/drivers/{driverId}")
    @RequirePermissions(Permission.PERM_VIEW_SCHOOLS)
    @Operation(
        summary = "Get a specific driver for a school",
        description = "Retrieve details of a specific driver (from master database) assigned to a school"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved driver details",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "School or driver not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - insufficient permissions",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<User> getSchoolDriver(
            @Parameter(description = "ID of the school", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "ID of the driver", required = true, example = "5")
            @PathVariable Long driverId) {
        try {
            User driver = schoolService.getDriverForSchool(id, driverId);
            return ResponseEntity.ok(driver);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}/drivers/{driverId}")
    @RequirePermissions(Permission.PERM_DELETE_USERS)
    @Operation(
        summary = "Remove a driver from a school",
        description = "Remove the association between a driver and a school. This only removes the assignment - " +
                     "the driver user in the master database is not deleted. Similar to how parent-child assignments can be removed."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Driver removed from school successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request - driver not associated with this school or driver is assigned to a route",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - insufficient permissions",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "School or driver not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<?> removeDriverFromSchool(
            @Parameter(description = "ID of the school", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "ID of the driver to remove", required = true, example = "5")
            @PathVariable Long driverId) {
        try {
            schoolService.removeDriverFromSchool(id, driverId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "INVALID_ARGUMENT",
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST.value(),
                    "/api/schools/" + id + "/drivers/" + driverId
            );
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
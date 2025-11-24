package com.sqool.sqoolbus.tenant.controller;

import com.sqool.sqoolbus.dto.ErrorResponse;
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
    @Operation(summary = "Create a new school", description = "Create a new school in the system")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "School created successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = School.class))),
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
        try {
            School savedSchool = schoolService.createSchool(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSchool);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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
        try {
            schoolService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/active")
    @RequirePermissions(Permission.PERM_VIEW_SCHOOLS)
    public ResponseEntity<List<School>> getActiveSchools() {
        List<School> activeSchools = schoolService.findActiveSchools();
        return ResponseEntity.ok(activeSchools);
    }
    
    @GetMapping("/type/{schoolType}")
    @RequirePermissions(Permission.PERM_VIEW_SCHOOLS)
    public ResponseEntity<List<School>> getSchoolsByType(@PathVariable String schoolType) {
        List<School> schools = schoolService.findByType(schoolType);
        return ResponseEntity.ok(schools);
    }
}
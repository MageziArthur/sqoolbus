package com.sqool.sqoolbus.tenant.controller;

import com.sqool.sqoolbus.config.multitenancy.TenantContext;
import com.sqool.sqoolbus.dto.ErrorResponse;
import com.sqool.sqoolbus.master.service.CrossTenantService;
import com.sqool.sqoolbus.security.SecurityUtils;
import com.sqool.sqoolbus.security.Permission;
import com.sqool.sqoolbus.security.RequirePermissions;
import com.sqool.sqoolbus.tenant.entity.hail.Pupil;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import com.sqool.sqoolbus.tenant.entity.hail.Route;
import com.sqool.sqoolbus.tenant.service.PupilService;
import com.sqool.sqoolbus.tenant.service.SchoolService;
import com.sqool.sqoolbus.tenant.service.RouteService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing pupils
 */
@RestController
@RequestMapping("/api/pupils")
@CrossOrigin(origins = "*")
@Tag(name = "Pupil Management", description = "APIs for managing pupils/students in the system")
public class PupilController {
    
    @Autowired
    private PupilService pupilService;
    
    @Autowired
    private SchoolService schoolService;
    
    @Autowired
    private RouteService routeService;

    @Autowired
    private CrossTenantService crossTenantService;
    
    @GetMapping
    @RequirePermissions(Permission.PERM_VIEW_PUPILS)
    @Operation(summary = "Get all pupils", description = "Retrieve a list of all pupils in the system")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pupils",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Pupil.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Pupil>> getAllPupils() {
        List<Pupil> pupils = pupilService.findAll();
        return ResponseEntity.ok(pupils);
    }
    
    @GetMapping("/{id}")
    @RequirePermissions(Permission.PERM_VIEW_PUPILS)
    public ResponseEntity<Pupil> getPupilById(@PathVariable Long id) {
        Optional<Pupil> pupil = pupilService.findById(id);
        return pupil.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/school/{schoolId}")
    @RequirePermissions(Permission.PERM_VIEW_PUPILS)
    @Operation(summary = "Get pupils by school", description = "Retrieve all pupils attached to the specified school in the tenant database")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pupils by school"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<List<Pupil>> getPupilsBySchool(@PathVariable Long schoolId) {
        List<Pupil> pupils = pupilService.findBySchoolId(schoolId);
        return ResponseEntity.ok(pupils);
    }
    
    @GetMapping("/parent/{parentId}")
    @RequirePermissions(Permission.PERM_VIEW_PUPILS)
    @Operation(summary = "Get students assigned to parent", description = "Retrieve students assigned to a parent ID (resolved from master mappings) within current tenant")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved students assigned to parent"),
        @ApiResponse(responseCode = "400", description = "Tenant context missing"),
        @ApiResponse(responseCode = "404", description = "Parent not found")
    })
    public ResponseEntity<?> getPupilsByParent(@PathVariable Long parentId) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "INVALID_ARGUMENT",
                    "Tenant context is required",
                    HttpStatus.BAD_REQUEST.value(),
                    "/api/pupils/parent/" + parentId
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }

        List<CrossTenantService.PupilDTO> pupils = crossTenantService.getStudentsForParentInTenant(parentId, tenantId);
        return ResponseEntity.ok(pupils);
    }

    @GetMapping("/parent/{parentId}/school/{schoolId}")
    @RequirePermissions(Permission.PERM_VIEW_PUPILS)
    @Operation(summary = "Get parent students by school", description = "Retrieve students assigned to a parent and attached to the specified school in current tenant")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved students"),
        @ApiResponse(responseCode = "400", description = "Tenant context missing")
    })
    public ResponseEntity<?> getPupilsByParentAndSchool(@PathVariable Long parentId, @PathVariable Long schoolId) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "INVALID_ARGUMENT",
                    "Tenant context is required",
                    HttpStatus.BAD_REQUEST.value(),
                    "/api/pupils/parent/" + parentId + "/school/" + schoolId
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }

        List<CrossTenantService.PupilDTO> pupils = crossTenantService
                .getStudentsForParentInTenantBySchool(parentId, tenantId, schoolId);
        return ResponseEntity.ok(pupils);
    }

    @GetMapping("/school/{schoolId}/assigned")
    @RequirePermissions(Permission.PERM_VIEW_PUPILS)
    @Operation(summary = "Get students attached to school", description = "Retrieve students attached to a school from master parent-student mappings, including parent details")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved students attached to school"),
        @ApiResponse(responseCode = "400", description = "Tenant context missing")
    })
    public ResponseEntity<?> getAssignedStudentsBySchool(@PathVariable Long schoolId) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "INVALID_ARGUMENT",
                    "Tenant context is required",
                    HttpStatus.BAD_REQUEST.value(),
                    "/api/pupils/school/" + schoolId + "/assigned"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }

        List<CrossTenantService.PupilDTO> pupils = crossTenantService.getStudentsForSchoolInTenant(tenantId, schoolId);
        return ResponseEntity.ok(pupils);
    }
    
    @PostMapping
    @RequirePermissions(Permission.PERM_CREATE_PUPILS)
    @Operation(summary = "Create a new pupil", description = "Create a new pupil without parent assignment")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pupil created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "School not found")
    })
    public ResponseEntity<?> createPupil(@Valid @RequestBody CreatePupilRequest request) {
        try {
            Long effectiveSchoolId = SecurityUtils.resolveEffectiveSchoolId(request.getSchoolId());
            if (effectiveSchoolId == null) {
                ErrorResponse errorResponse = new ErrorResponse(
                        "INVALID_ARGUMENT",
                        "School ID is required when adding a student to a school",
                        HttpStatus.BAD_REQUEST.value(),
                        "/api/pupils"
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Long currentUserSchoolId = SecurityUtils.getCurrentUserSchoolId();
            if (currentUserSchoolId != null && request.getSchoolId() != null && !currentUserSchoolId.equals(request.getSchoolId())) {
                ErrorResponse errorResponse = new ErrorResponse(
                        "ACCESS_DENIED",
                        "You can only add students to your assigned school",
                        HttpStatus.FORBIDDEN.value(),
                        "/api/pupils"
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

                Optional<School> school = schoolService.findById(effectiveSchoolId);
            if (!school.isPresent()) {
                ErrorResponse errorResponse = new ErrorResponse(
                        "RESOURCE_NOT_FOUND",
                    "School not found with id: " + effectiveSchoolId,
                        HttpStatus.NOT_FOUND.value(),
                        "/api/pupils"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            if (request.getStudentId() != null && pupilService.existsByStudentId(request.getStudentId())) {
                ErrorResponse errorResponse = new ErrorResponse(
                        "DUPLICATE_RESOURCE",
                        "Student ID already exists: " + request.getStudentId(),
                        HttpStatus.BAD_REQUEST.value(),
                        "/api/pupils"
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            Pupil pupil = new Pupil();
            pupil.setFirstName(request.getFirstName());
            pupil.setLastName(request.getLastName());
            pupil.setMiddleName(request.getMiddleName());
            pupil.setStudentId(request.getStudentId());
            pupil.setGradeLevel(request.getGradeLevel());
            pupil.setClassSection(request.getClassSection());
            pupil.setDateOfBirth(request.getDateOfBirth());
            pupil.setGender(request.getGender());
            pupil.setHomeAddress(request.getHomeAddress());
            pupil.setCity(request.getCity());
            pupil.setState(request.getState());
            pupil.setZipCode(request.getZipCode());
            pupil.setParentContact(request.getParentContact());
            pupil.setEmergencyContact(request.getEmergencyContact());
            pupil.setParentEmail(request.getParentEmail());
            pupil.setMedicalConditions(request.getMedicalConditions());
            pupil.setSpecialNeeds(request.getSpecialNeeds());
            pupil.setSchool(school.get());
            
            // Optionally assign route if provided
            if (request.getRouteId() != null) {
                Optional<Route> route = routeService.findById(request.getRouteId());
                if (!route.isPresent()) {
                    ErrorResponse errorResponse = new ErrorResponse(
                            "RESOURCE_NOT_FOUND",
                            "Route not found with id: " + request.getRouteId(),
                            HttpStatus.NOT_FOUND.value(),
                            "/api/pupils"
                    );
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                }

                if (route.get().getSchool() == null || !route.get().getSchool().getId().equals(effectiveSchoolId)) {
                    ErrorResponse errorResponse = new ErrorResponse(
                            "INVALID_ARGUMENT",
                            "Selected route is not assigned to the selected school",
                            HttpStatus.BAD_REQUEST.value(),
                            "/api/pupils"
                    );
                    return ResponseEntity.badRequest().body(errorResponse);
                }

                pupil.setRoute(route.get());
            }
            
            Pupil savedPupil = pupilService.save(pupil);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPupil);
        } catch (DataIntegrityViolationException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "DUPLICATE_RESOURCE",
                    "Unable to create student. Please ensure student ID is unique.",
                    HttpStatus.BAD_REQUEST.value(),
                    "/api/pupils"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "INVALID_ARGUMENT",
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST.value(),
                    "/api/pupils"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "INVALID_ARGUMENT",
                    "Invalid input data",
                    HttpStatus.BAD_REQUEST.value(),
                    "/api/pupils"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/{pupilId}/assign-parent/{parentId}")
    @RequirePermissions(Permission.PERM_UPDATE_PUPILS)
    @Operation(summary = "Assign parent to pupil", description = "Assign a parent to an existing pupil using parent ID from master tenant. Parent user must have PARENT role")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Parent assigned successfully"),
        @ApiResponse(responseCode = "404", description = "Pupil or parent not found")
    })
    public ResponseEntity<?> assignParentToPupil(
            @Parameter(description = "Pupil ID") @PathVariable Long pupilId,
            @Parameter(description = "Parent ID") @PathVariable Long parentId) {
        try {
            Optional<Pupil> pupilOpt = pupilService.findById(pupilId);
            if (!pupilOpt.isPresent()) {
                ErrorResponse errorResponse = new ErrorResponse(
                        "RESOURCE_NOT_FOUND",
                        "Pupil not found with id: " + pupilId,
                        HttpStatus.NOT_FOUND.value(),
                        "/api/pupils/" + pupilId + "/assign-parent/" + parentId
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            Pupil pupil = pupilOpt.get();
            pupilService.assignParentFromMaster(parentId, pupil);
            return ResponseEntity.ok(pupil);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "INVALID_ARGUMENT",
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST.value(),
                    "/api/pupils/" + pupilId + "/assign-parent/" + parentId
            );
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "RESOURCE_NOT_FOUND",
                    e.getMessage(),
                    HttpStatus.NOT_FOUND.value(),
                    "/api/pupils/" + pupilId + "/assign-parent/" + parentId
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "INVALID_ARGUMENT",
                    "Unable to assign parent to pupil",
                    HttpStatus.BAD_REQUEST.value(),
                    "/api/pupils/" + pupilId + "/assign-parent/" + parentId
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @DeleteMapping("/{pupilId}/remove-parent")
    @RequirePermissions(Permission.PERM_UPDATE_PUPILS)
    @Operation(summary = "Remove parent from pupil", description = "Remove the parent assignment from a pupil")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Parent removed successfully"),
        @ApiResponse(responseCode = "404", description = "Pupil not found")
    })
    public ResponseEntity<Pupil> removeParentFromPupil(
            @Parameter(description = "Pupil ID") @PathVariable Long pupilId) {
        try {
            Optional<Pupil> pupilOpt = pupilService.findById(pupilId);
            if (!pupilOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Pupil pupil = pupilOpt.get();
            pupil.setParent(null);
            Pupil updatedPupil = pupilService.save(pupil);
            return ResponseEntity.ok(updatedPupil);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{pupilId}/assign-route/{routeId}")
    @RequirePermissions(Permission.PERM_ASSIGN_ROUTES)
    @Operation(summary = "Assign route to pupil", description = "Assign a route to an existing pupil")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Route assigned successfully"),
        @ApiResponse(responseCode = "404", description = "Pupil or route not found")
    })
    public ResponseEntity<Pupil> assignRouteToPupil(
            @Parameter(description = "Pupil ID") @PathVariable Long pupilId,
            @Parameter(description = "Route ID") @PathVariable Long routeId) {
        try {
            Optional<Pupil> pupilOpt = pupilService.findById(pupilId);
            if (!pupilOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Optional<Route> routeOpt = routeService.findById(routeId);
            if (!routeOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Pupil pupil = pupilOpt.get();
            pupil.setRoute(routeOpt.get());
            Pupil updatedPupil = pupilService.save(pupil);
            return ResponseEntity.ok(updatedPupil);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{pupilId}/remove-route")
    @RequirePermissions(Permission.PERM_ASSIGN_ROUTES)
    @Operation(summary = "Remove route from pupil", description = "Remove the route assignment from a pupil")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Route removed successfully"),
        @ApiResponse(responseCode = "404", description = "Pupil not found")
    })
    public ResponseEntity<Pupil> removeRouteFromPupil(
            @Parameter(description = "Pupil ID") @PathVariable Long pupilId) {
        try {
            Optional<Pupil> pupilOpt = pupilService.findById(pupilId);
            if (!pupilOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Pupil pupil = pupilOpt.get();
            pupil.setRoute(null);
            Pupil updatedPupil = pupilService.save(pupil);
            return ResponseEntity.ok(updatedPupil);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @RequirePermissions(Permission.PERM_UPDATE_PUPILS)
    public ResponseEntity<Pupil> updatePupil(@PathVariable Long id, @Valid @RequestBody Pupil pupilDetails) {
        try {
            Pupil updatedPupil = pupilService.update(id, pupilDetails);
            return ResponseEntity.ok(updatedPupil);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @RequirePermissions(Permission.PERM_DELETE_PUPILS)
    public ResponseEntity<Void> deletePupil(@PathVariable Long id) {
        try {
            pupilService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/grade/{gradeLevel}")
    @RequirePermissions(Permission.PERM_VIEW_PUPILS)
    public ResponseEntity<List<Pupil>> getPupilsByGrade(@PathVariable String gradeLevel) {
        List<Pupil> pupils = pupilService.findByGradeLevel(gradeLevel);
        return ResponseEntity.ok(pupils);
    }
    
    @GetMapping("/active")
    @RequirePermissions(Permission.PERM_VIEW_PUPILS)
    public ResponseEntity<List<Pupil>> getActivePupils() {
        List<Pupil> activePupils = pupilService.findActiveStudents();
        return ResponseEntity.ok(activePupils);
    }
    
    // Request DTO
    public static class CreatePupilRequest {
        private String firstName;
        private String lastName;
        private String middleName;
        private String studentId;
        private String gradeLevel;
        private String classSection;
        private LocalDate dateOfBirth;
        private String gender;
        private String homeAddress;
        private String city;
        private String state;
        private String zipCode;
        private String parentContact;
        private String emergencyContact;
        private String parentEmail;
        private String medicalConditions;
        private String specialNeeds;
        private Long schoolId;
        private Long routeId; // Optional: assign route during creation
        
        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getMiddleName() { return middleName; }
        public void setMiddleName(String middleName) { this.middleName = middleName; }
        
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        
        public String getGradeLevel() { return gradeLevel; }
        public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
        
        public String getClassSection() { return classSection; }
        public void setClassSection(String classSection) { this.classSection = classSection; }
        
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        
        public String getHomeAddress() { return homeAddress; }
        public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        
        public String getParentContact() { return parentContact; }
        public void setParentContact(String parentContact) { this.parentContact = parentContact; }
        
        public String getEmergencyContact() { return emergencyContact; }
        public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
        
        public String getParentEmail() { return parentEmail; }
        public void setParentEmail(String parentEmail) { this.parentEmail = parentEmail; }
        
        public String getMedicalConditions() { return medicalConditions; }
        public void setMedicalConditions(String medicalConditions) { this.medicalConditions = medicalConditions; }
        
        public String getSpecialNeeds() { return specialNeeds; }
        public void setSpecialNeeds(String specialNeeds) { this.specialNeeds = specialNeeds; }
        
        public Long getSchoolId() { return schoolId; }
        public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
        
        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
    }
}
package com.sqool.sqoolbus.tenant.controller;

import com.sqool.sqoolbus.security.Permission;
import com.sqool.sqoolbus.security.RequirePermissions;
import com.sqool.sqoolbus.tenant.entity.hail.Pupil;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import com.sqool.sqoolbus.tenant.entity.hail.Route;
import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.service.PupilService;
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
    private UserManagementService userManagementService;
    
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
    public ResponseEntity<List<Pupil>> getPupilsBySchool(@PathVariable Long schoolId) {
        List<Pupil> pupils = pupilService.findBySchoolId(schoolId);
        return ResponseEntity.ok(pupils);
    }
    
    @GetMapping("/parent/{parentId}")
    @RequirePermissions(Permission.PERM_VIEW_PUPILS)
    public ResponseEntity<List<Pupil>> getPupilsByParent(@PathVariable Long parentId) {
        List<Pupil> pupils = pupilService.findByParentId(parentId);
        return ResponseEntity.ok(pupils);
    }
    
    @PostMapping
    @RequirePermissions(Permission.PERM_CREATE_PUPILS)
    public ResponseEntity<Pupil> createPupil(@Valid @RequestBody CreatePupilRequest request) {
        try {
            Optional<School> school = schoolService.findById(request.getSchoolId());
            if (!school.isPresent()) {
                return ResponseEntity.badRequest().build();
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
            
            // Set parent if provided
            if (request.getParentId() != null) {
                User parent = userManagementService.findUserById(request.getParentId());
                if (parent != null) {
                    pupil.setParent(parent);
                }
            }
            
            // Set route if provided
            if (request.getRouteId() != null) {
                Optional<Route> route = routeService.findById(request.getRouteId());
                if (route.isPresent()) {
                    pupil.setRoute(route.get());
                    pupil.setUsesBusService(true);
                }
            }
            
            Pupil savedPupil = pupilService.save(pupil);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPupil);
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
    public ResponseEntity<List<Pupil>> getPupilsByGrade(@PathVariable Integer gradeLevel) {
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
        private Integer gradeLevel;
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
        private Long parentId;
        private Long routeId;
        
        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getMiddleName() { return middleName; }
        public void setMiddleName(String middleName) { this.middleName = middleName; }
        
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        
        public Integer getGradeLevel() { return gradeLevel; }
        public void setGradeLevel(Integer gradeLevel) { this.gradeLevel = gradeLevel; }
        
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
        
        public Long getParentId() { return parentId; }
        public void setParentId(Long parentId) { this.parentId = parentId; }
        
        public Long getRouteId() { return routeId; }
        public void setRouteId(Long routeId) { this.routeId = routeId; }
    }
}
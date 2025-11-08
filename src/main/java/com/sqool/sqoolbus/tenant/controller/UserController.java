package com.sqool.sqoolbus.tenant.controller;

import com.sqool.sqoolbus.security.Permission;
import com.sqool.sqoolbus.security.RequirePermissions;
import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.entity.hail.UserProfile;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import com.sqool.sqoolbus.tenant.service.UserManagementService;
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
import java.util.Map;

/**
 * REST Controller for managing users with role-based system
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "APIs for managing users and profiles with role-based access")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    @Autowired
    private UserManagementService userManagementService;
    
    @Autowired
    private SchoolService schoolService;
    
    @PostMapping("/parent")
    @RequirePermissions(Permission.PERM_CREATE_USERS)
    @Operation(summary = "Create a parent user", description = "Create a new parent user with associated profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Parent created successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or school not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<User> createParent(
            @Parameter(description = "Parent creation data", required = true)
            @Valid @RequestBody CreateParentRequest request) {
        try {
            Optional<School> school = schoolService.findById(request.getSchoolId());
            if (!school.isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            User parent = userManagementService.createParent(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                school.get()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(parent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/rider")
    @RequirePermissions(Permission.PERM_CREATE_USERS)
    @Operation(summary = "Create a rider user", description = "Create a new rider user with driver profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rider created successfully",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data or school not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<User> createRider(
            @Parameter(description = "Rider creation data", required = true)
            @Valid @RequestBody CreateRiderRequest request) {
        try {
            Optional<School> school = schoolService.findById(request.getSchoolId());
            if (!school.isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            User rider = userManagementService.createRider(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                school.get(),
                request.getLicenseNumber(),
                request.getEmployeeId()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(rider);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/school-admin")
    @RequirePermissions(Permission.PERM_CREATE_SCHOOL_ADMINS)
    public ResponseEntity<User> createSchoolAdmin(@Valid @RequestBody CreateSchoolAdminRequest request) {
        try {
            Optional<School> school = schoolService.findById(request.getSchoolId());
            if (!school.isPresent()) {
                return ResponseEntity.badRequest().build();
            }
            
            User admin = userManagementService.createSchoolAdmin(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                school.get(),
                request.getJobTitle(),
                request.getDepartment()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(admin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/system-admin")
    @RequirePermissions(Permission.PERM_CREATE_SYSTEM_ADMINS)
    public ResponseEntity<User> createSystemAdmin(@Valid @RequestBody CreateSystemAdminRequest request) {
        try {
            User admin = userManagementService.createSystemAdmin(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(admin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    @RequirePermissions(Permission.PERM_VIEW_USERS)
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userManagementService.findUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/username/{username}")
    @RequirePermissions(Permission.PERM_VIEW_USERS)
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userManagementService.findUserByUsername(username);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/email/{email}")
    @RequirePermissions(Permission.PERM_VIEW_USERS)
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userManagementService.findUserByEmail(email);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/profile")
    @RequirePermissions(Permission.PERM_UPDATE_USERS)
    public ResponseEntity<User> updateUserProfile(@PathVariable Long id, @Valid @RequestBody UserProfile profileData) {
        try {
            User user = userManagementService.findUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            userManagementService.updateUserProfile(user, profileData);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}/roles")
    @RequirePermissions(Permission.PERM_VIEW_USERS)
    public ResponseEntity<Map<String, Object>> getUserRoles(@PathVariable Long id) {
        User user = userManagementService.findUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = Map.of(
            "userId", user.getId(),
            "username", user.getUsername(),
            "roles", user.getRoles()
        );
        
        return ResponseEntity.ok(response);
    }
    
    // Request DTOs
    public static class CreateParentRequest {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private Long schoolId;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public Long getSchoolId() { return schoolId; }
        public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    }
    
    public static class CreateRiderRequest {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private Long schoolId;
        private String licenseNumber;
        private String employeeId;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public Long getSchoolId() { return schoolId; }
        public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
        
        public String getLicenseNumber() { return licenseNumber; }
        public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
        
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    }
    
    public static class CreateSchoolAdminRequest {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private Long schoolId;
        private String jobTitle;
        private String department;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public Long getSchoolId() { return schoolId; }
        public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
        
        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
    }
    
    public static class CreateSystemAdminRequest {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }
}
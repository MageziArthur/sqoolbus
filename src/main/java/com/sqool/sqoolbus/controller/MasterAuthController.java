package com.sqool.sqoolbus.controller;

import com.sqool.sqoolbus.dto.ApiResponse;
import com.sqool.sqoolbus.dto.MasterLoginRequest;
import com.sqool.sqoolbus.dto.MasterLoginResponse;
import com.sqool.sqoolbus.master.entity.ParentPupilMapping;
import com.sqool.sqoolbus.master.repository.ParentPupilMappingRepository;
import com.sqool.sqoolbus.service.MasterAuthService;
import com.sqool.sqoolbus.service.TenantResolutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Master Authentication", description = "System-level authentication endpoints for master database access")
@RestController
@RequestMapping("/api/master/auth")
public class MasterAuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(MasterAuthController.class);
    
    @Autowired
    private MasterAuthService masterAuthService;
    
    @Autowired
    private com.sqool.sqoolbus.master.service.CrossTenantService crossTenantService;

    @Autowired
    private TenantResolutionService tenantResolutionService;

    @Autowired
    private ParentPupilMappingRepository parentPupilMappingRepository;
    
    @Operation(
        summary = "Master System Login",
        description = "Authenticate user against master database for system-level access. " +
                     "This endpoint does not require tenant context and provides access to system administration features."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Authentication successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "Authentication successful",
                        "data": {
                            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                            "tokenType": "Bearer",
                            "expiresIn": 3600,
                            "userId": 1,
                            "username": "superadmin",
                            "email": "admin@sqoolbus.com",
                            "fullName": "Super Administrator",
                            "roles": ["SUPER_ADMIN"],
                            "permissions": ["SYSTEM_ADMIN", "TENANT_CREATE", "TENANT_READ", "TENANT_UPDATE", "TENANT_DELETE"],
                            "isActive": true,
                            "lastLoginAt": "2025-11-05T23:15:30"
                        },
                        "timestamp": "2025-11-05T23:15:30.123456",
                        "path": "/api/master/auth/login",
                        "errors": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Authentication failed - Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": false,
                        "message": "Invalid credentials",
                        "data": null,
                        "timestamp": "2025-11-05T23:15:30.123456",
                        "path": "/api/master/auth/login",
                        "errors": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error - Invalid request format",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": false,
                        "message": "Validation failed",
                        "data": null,
                        "timestamp": "2025-11-05T23:15:30.123456",
                        "path": "/api/master/auth/login",
                        "errors": {
                            "usernameOrEmail": "Username or email is required",
                            "password": "Password must be between 6 and 100 characters"
                        }
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @Valid @RequestBody MasterLoginRequest loginRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {
        
        logger.info("Master login attempt for user: {}", loginRequest.getUsernameOrEmail());
        
        try {
            // Validate request
            if (bindingResult.hasErrors()) {
                String errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage()
                    )).toString();
                
                logger.warn("Master login validation failed: {}", errors);
                
            Map<String, String> errorMap = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(
                    error -> error.getField(),
                    error -> error.getDefaultMessage()
                ));
            
            return ResponseEntity.badRequest().body(
                ApiResponse.<Map<String, String>>error("Validation failed", errorMap)
            );
            }
            
            // Authenticate user
            MasterLoginResponse response = masterAuthService.authenticateUser(loginRequest);
            
            logger.info("Master authentication successful for user: {}", response.getUsername());
            
            return ResponseEntity.ok(
                ApiResponse.success("Authentication successful", response)
            );
            
        } catch (RuntimeException e) {
            logger.error("Master authentication failed for user: {}", loginRequest.getUsernameOrEmail(), e);
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error("Invalid credentials")
            );
        }
    }
    
    @Operation(
        summary = "Get Current Master User",
        description = "Get information about the currently authenticated master user"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User information retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token"
        )
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getCurrentUser(
            HttpServletRequest request,
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            // Extract token from Authorization header
            String token = authHeader.replace("Bearer ", "");
            
            // Validate token and get user
            var user = masterAuthService.validateTokenAndGetUser(token);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponse.error("Invalid or expired token")
                );
            }
            
            // Create response with current user info (without new token generation)
            MasterLoginResponse response = new MasterLoginResponse();
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setFullName(user.getFullName());
            response.setIsActive(user.getIsActive());
            response.setLastLoginAt(user.getLastLoginAt());
            
            // Get current roles and permissions
            response.setRoles(user.getUserRoles().stream()
                .filter(ur -> ur.getTenant() == null)
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList()));
            
            return ResponseEntity.ok(
                ApiResponse.success("User information retrieved", response)
            );
            
        } catch (Exception e) {
            logger.error("Error getting current master user", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error("Authentication required")
            );
        }
    }
    
    @Operation(
        summary = "Register Parent User (Master DB)",
        description = "Register a new parent user in the master database with tenant association"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "Parent registration successful",
            content = @Content(schema = @Schema(implementation = com.sqool.sqoolbus.dto.ParentSignupResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid input data or validation errors"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409", 
            description = "Username or email already exists"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "Tenant not found"
        )
    })
    @PostMapping("/signup/parent")
    public ResponseEntity<ApiResponse<com.sqool.sqoolbus.dto.ParentSignupResponse>> registerParent(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Parent registration details",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = com.sqool.sqoolbus.dto.ParentSignupRequest.class)
                )
            )
            @Valid @RequestBody com.sqool.sqoolbus.dto.ParentSignupRequest parentSignupRequest,
            @io.swagger.v3.oas.annotations.Parameter(
                description = "Tenant identifier",
                example = "default_sqool",
                required = true
            )
            @RequestHeader(value = "X-Tenant-ID", required = true) String tenantId,
            BindingResult bindingResult) {
        
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                String errors = bindingResult.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.joining(", "));
                
                logger.warn("Validation errors in parent signup: {}", errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error("Validation failed: " + errors)
                );
            }

            logger.info("Processing parent signup request for user: {} on tenant: {}", 
                       parentSignupRequest.getUsername(), tenantId);

            // Register parent in master database
            com.sqool.sqoolbus.dto.ParentSignupResponse signupResponse = masterAuthService.registerParent(parentSignupRequest, tenantId);

            logger.info("Parent registered successfully: {}", parentSignupRequest.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.success("Parent registered successfully in master database", signupResponse)
            );

        } catch (Exception e) {
            logger.error("Error in parent signup: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error("Registration failed: " + e.getMessage())
            );
        }
    }

    @Operation(
        summary = "Get All Kids for Parent",
        description = "Get all pupils/kids owned by a parent across all tenant databases. Returns kids grouped by tenant."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved kids"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Parent not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    @GetMapping("/parent/{parentId}/kids")
    public ResponseEntity<ApiResponse<Map<String, List<com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO>>>> getParentKids(
            @io.swagger.v3.oas.annotations.Parameter(
                description = "Parent user ID from master database",
                example = "1",
                required = true
            )
            @PathVariable Long parentId) {
        
        try {
            logger.info("Fetching all kids for parent ID: {}", parentId);
            
            Map<String, List<com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO>> kids = 
                    crossTenantService.getAllKidsForParentById(parentId);
            
            int totalKids = kids.values().stream().mapToInt(List::size).sum();
            logger.info("Found {} kids across {} tenants for parent ID: {}", 
                       totalKids, kids.size(), parentId);
            
            return ResponseEntity.ok(
                ApiResponse.success(
                    String.format("Found %d kids across %d tenants", totalKids, kids.size()),
                    kids
                )
            );
            
        } catch (RuntimeException e) {
            logger.error("Error fetching kids for parent {}: {}", parentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("Parent not found or error retrieving kids: " + e.getMessage())
            );
        } catch (Exception e) {
            logger.error("Unexpected error fetching kids for parent {}: {}", parentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error("Error retrieving kids: " + e.getMessage())
            );
        }
    }

    @Operation(
        summary = "Get All Students for Parent",
        description = "Get all students belonging to a parent across all tenants as a single flattened list."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved students"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Parent not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    @GetMapping("/parent/{parentId}/students")
    public ResponseEntity<ApiResponse<List<com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO>>> getParentStudents(
            @io.swagger.v3.oas.annotations.Parameter(
                description = "Parent user ID from master database",
                example = "1",
                required = true
            )
            @PathVariable Long parentId) {

        try {
            logger.info("Fetching all students for parent ID: {}", parentId);

            Map<String, List<com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO>> kidsByTenant =
                    crossTenantService.getAllKidsForParentById(parentId);

            List<com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO> students = kidsByTenant.values()
                    .stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            logger.info("Found {} students for parent ID: {}", students.size(), parentId);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            String.format("Found %d students", students.size()),
                            students
                    )
            );

        } catch (RuntimeException e) {
            logger.error("Error fetching students for parent {}: {}", parentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("Parent not found or error retrieving students: " + e.getMessage())
            );
        } catch (Exception e) {
            logger.error("Unexpected error fetching students for parent {}: {}", parentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Error retrieving students: " + e.getMessage())
            );
        }
    }

    @Operation(
        summary = "Get Parent Students by School",
        description = "Get all students belonging to a parent for a specific school. schoolId is resolved to tenant database configuration, then queried from master mappings."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved students"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Parent or school not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    @GetMapping("/parent/{parentId}/students/school/{schoolId}")
    public ResponseEntity<ApiResponse<List<com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO>>> getParentStudentsBySchool(
            @io.swagger.v3.oas.annotations.Parameter(description = "Parent user ID", example = "1", required = true)
            @PathVariable Long parentId,
            @io.swagger.v3.oas.annotations.Parameter(description = "School ID", example = "10", required = true)
            @PathVariable Long schoolId) {

        try {
            String tenantId = tenantResolutionService.resolveTenantIdForSchool(schoolId);
            if (tenantId == null || tenantId.isBlank()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiResponse.error("School not found: " + schoolId)
                );
            }

            List<ParentPupilMapping> mappings = parentPupilMappingRepository.findAllActiveByParentIdAndTenantId(parentId, tenantId);

            List<com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO> students = mappings.stream()
                    .map(mapping -> {
                        com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO dto = new com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO();
                        dto.setId(mapping.getPupilId());
                        dto.setFirstName(mapping.getPupilFirstName());
                        dto.setLastName(mapping.getPupilLastName());
                        dto.setStudentId(mapping.getStudentId());
                        dto.setGradeLevel(mapping.getGradeLevel());
                        dto.setSchoolId(mapping.getSchoolId());
                        dto.setSchoolName(mapping.getSchoolName());
                        dto.setRouteId(mapping.getRouteId());
                        dto.setRouteName(mapping.getRouteName());
                        dto.setTenantId(mapping.getTenant().getTenantId());
                        dto.setActive(Boolean.TRUE.equals(mapping.getIsActive()));
                        return dto;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(
                    String.format("Found %d students for parent %d in school %d", students.size(), parentId, schoolId),
                    students
            ));

        } catch (RuntimeException e) {
            logger.error("Error fetching parent students by school. parentId={}, schoolId={}, error={}", parentId, schoolId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("Parent not found or error retrieving students: " + e.getMessage())
            );
        } catch (Exception e) {
            logger.error("Unexpected error fetching parent students by school", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Error retrieving students: " + e.getMessage())
            );
        }
    }

    @Operation(
        summary = "Get Parent Students Grouped by School",
        description = "Get all students belonging to a parent across all schools, grouped by school. Useful when a parent has children in multiple schools."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved students grouped by school"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Parent not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    @GetMapping("/parent/{parentId}/students/grouped-by-school")
    public ResponseEntity<ApiResponse<Map<Long, List<com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO>>>> getParentStudentsGroupedBySchool(
            @io.swagger.v3.oas.annotations.Parameter(
                description = "Parent user ID from master database",
                example = "1",
                required = true
            )
            @PathVariable Long parentId) {

        try {
            logger.info("Fetching students grouped by school for parent ID: {}", parentId);

            Map<String, List<com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO>> kidsByTenant =
                    crossTenantService.getAllKidsForParentById(parentId);

            List<com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO> allStudents = kidsByTenant.values()
                    .stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            Map<Long, List<com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO>> studentsBySchool = allStudents.stream()
                    .filter(student -> student.getSchoolId() != null)
                    .collect(Collectors.groupingBy(
                            com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO::getSchoolId,
                            java.util.LinkedHashMap::new,
                            Collectors.toList()
                    ));

            return ResponseEntity.ok(
                    ApiResponse.success(
                            String.format("Found %d students across %d schools", allStudents.size(), studentsBySchool.size()),
                            studentsBySchool
                    )
            );

        } catch (RuntimeException e) {
            logger.error("Error fetching grouped students for parent {}: {}", parentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("Parent not found or error retrieving students: " + e.getMessage())
            );
        } catch (Exception e) {
            logger.error("Unexpected error fetching grouped students for parent {}: {}", parentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Error retrieving students: " + e.getMessage())
            );
        }
    }

    @Operation(
        summary = "Get Students by School",
        description = "Get all students attached to a school from master parent-student mappings."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved students"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "School not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    @GetMapping("/school/{schoolId}/students")
    public ResponseEntity<ApiResponse<List<com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO>>> getStudentsBySchool(
            @io.swagger.v3.oas.annotations.Parameter(description = "School ID", example = "10", required = true)
            @PathVariable Long schoolId) {

        try {
            String tenantId = tenantResolutionService.resolveTenantIdForSchool(schoolId);
            if (tenantId == null || tenantId.isBlank()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiResponse.error("School not found: " + schoolId)
                );
            }

            List<com.sqool.sqoolbus.master.service.CrossTenantService.PupilDTO> students =
                    crossTenantService.getStudentsForSchoolInTenant(tenantId, schoolId);

            return ResponseEntity.ok(ApiResponse.success(
                    String.format("Found %d students attached to school %d", students.size(), schoolId),
                    students
            ));
        } catch (Exception e) {
            logger.error("Error fetching students by school. schoolId={}, error={}", schoolId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Error retrieving students: " + e.getMessage())
            );
        }
    }

    @Operation(
        summary = "Get All Schools for Driver",
        description = "Get all schools assigned to a driver across all tenants as a single flattened list."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved schools"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Driver not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    @GetMapping("/driver/{driverId}/schools")
    public ResponseEntity<ApiResponse<List<com.sqool.sqoolbus.master.service.CrossTenantService.DriverSchoolDTO>>> getDriverSchools(
            @io.swagger.v3.oas.annotations.Parameter(
                description = "Driver user ID from master database",
                example = "15",
                required = true
            )
            @PathVariable Long driverId) {

        try {
            logger.info("Fetching all schools for driver ID: {}", driverId);

            Map<String, List<com.sqool.sqoolbus.master.service.CrossTenantService.DriverSchoolDTO>> schoolsByTenant =
                    crossTenantService.getAllSchoolsForDriverById(driverId);

            List<com.sqool.sqoolbus.master.service.CrossTenantService.DriverSchoolDTO> schools = schoolsByTenant.values()
                    .stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    ApiResponse.success(
                            String.format("Found %d school assignments", schools.size()),
                            schools
                    )
            );
        } catch (RuntimeException e) {
            logger.error("Error fetching schools for driver {}: {}", driverId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("Driver not found or error retrieving schools: " + e.getMessage())
            );
        } catch (Exception e) {
            logger.error("Unexpected error fetching schools for driver {}: {}", driverId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Error retrieving schools: " + e.getMessage())
            );
        }
    }

    @Operation(
        summary = "Get Driver Schools Grouped by Tenant",
        description = "Get all schools assigned to a driver across tenants, grouped by tenant."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved grouped school assignments"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Driver not found"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    @GetMapping("/driver/{driverId}/schools/grouped-by-tenant")
    public ResponseEntity<ApiResponse<Map<String, List<com.sqool.sqoolbus.master.service.CrossTenantService.DriverSchoolDTO>>>> getDriverSchoolsGroupedByTenant(
            @io.swagger.v3.oas.annotations.Parameter(
                description = "Driver user ID from master database",
                example = "15",
                required = true
            )
            @PathVariable Long driverId) {

        try {
            logger.info("Fetching schools grouped by tenant for driver ID: {}", driverId);

            Map<String, List<com.sqool.sqoolbus.master.service.CrossTenantService.DriverSchoolDTO>> schoolsByTenant =
                    crossTenantService.getAllSchoolsForDriverById(driverId);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            String.format("Found school assignments across %d tenants", schoolsByTenant.size()),
                            schoolsByTenant
                    )
            );
        } catch (RuntimeException e) {
            logger.error("Error fetching grouped schools for driver {}: {}", driverId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.error("Driver not found or error retrieving schools: " + e.getMessage())
            );
        } catch (Exception e) {
            logger.error("Unexpected error fetching grouped schools for driver {}: {}", driverId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Error retrieving schools: " + e.getMessage())
            );
        }
    }

    @Operation(
        summary = "Master Logout",
        description = "Logout from master system (client-side token removal)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Logout successful"
        )
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest request) {
        logger.info("Master logout request from IP: {}", request.getRemoteAddr());
        
        // Since JWT is stateless, logout is handled client-side by removing the token
        // Here we just confirm the logout action
        
        return ResponseEntity.ok(
            ApiResponse.success("Logout successful. Please remove the token from client storage.", 
                              "Logged out")
        );
    }
}
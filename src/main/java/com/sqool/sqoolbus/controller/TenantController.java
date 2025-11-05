package com.sqool.sqoolbus.controller;

import com.sqool.sqoolbus.dto.ApiResponse;
import com.sqool.sqoolbus.dto.TenantRegistrationRequest;
import com.sqool.sqoolbus.dto.TenantRegistrationResponse;
import com.sqool.sqoolbus.dto.TenantSetupResponse;
import com.sqool.sqoolbus.service.TenantDataSourceService;
import com.sqool.sqoolbus.service.TenantManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tenants")
@Tag(name = "Tenant Management", description = "Operations for managing tenant information, registration, and setup")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TenantController {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantController.class);
    
    @Autowired
    private TenantDataSourceService tenantDataSourceService;
    
    @Autowired
    private TenantManagementService tenantManagementService;
    
    @Operation(
        summary = "Validate Tenant",
        description = "Validate if a tenant exists and is active in the system",
        tags = {"Tenant Management"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Tenant validation result",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\":true,\"message\":\"Tenant validation completed\",\"data\":{\"tenantId\":\"default-sqool\",\"isValid\":true,\"isActive\":true}}"
                )
            )
        )
    })
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateTenant(
            @Parameter(
                description = "Tenant identifier to validate",
                example = "default-sqool",
                required = false
            )
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId,
            HttpServletRequest request) {
        
        try {
            String currentTenant = (tenantId != null && !tenantId.isEmpty()) ? tenantId : "default-sqool";
            
            boolean isValid = tenantDataSourceService.isTenantValidFromDatabase(currentTenant);
            
            Map<String, Object> validationResult = new HashMap<>();
            validationResult.put("tenantId", currentTenant);
            validationResult.put("isValid", isValid);
            validationResult.put("isDefault", "default-sqool".equals(currentTenant));
            
            if (isValid) {
                validationResult.put("hasDataSource", tenantDataSourceService.getDataSourceForTenant(currentTenant) != null);
            }
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                isValid ? "Tenant is valid" : "Tenant is invalid", 
                validationResult
            );
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error validating tenant: {}", tenantId, e);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.error("Error validating tenant: " + e.getMessage());
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @Operation(
        summary = "Get Cached Tenants",
        description = "Retrieve list of currently cached tenant datasources",
        tags = {"Tenant Management"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Cached tenants information retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\":true,\"message\":\"Cached tenants retrieved\",\"data\":{\"cachedTenantsCount\":1,\"cachedTenants\":[\"default-sqool\"]}}"
                )
            )
        )
    })
    @GetMapping("/cached")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCachedTenants(HttpServletRequest request) {
        try {
            Set<String> cachedTenants = tenantDataSourceService.getCachedTenants();
            
            Map<String, Object> cacheInfo = new HashMap<>();
            cacheInfo.put("cachedTenantsCount", cachedTenants.size());
            cacheInfo.put("cachedTenants", cachedTenants);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success("Cached tenants retrieved", cacheInfo);
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting cached tenants", e);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.error("Error getting cached tenants: " + e.getMessage());
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/cache/clear/{tenantId}")
    public ResponseEntity<ApiResponse<String>> clearTenantCache(
            @PathVariable String tenantId,
            HttpServletRequest request) {
        
        try {
            tenantDataSourceService.removeTenantDataSource(tenantId);
            
            ApiResponse<String> response = ApiResponse.success("Tenant cache cleared for: " + tenantId);
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error clearing tenant cache for: {}", tenantId, e);
            
            ApiResponse<String> response = ApiResponse.error("Error clearing tenant cache: " + e.getMessage());
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @Operation(
        summary = "Get Current Tenant",
        description = "Get information about the current tenant based on X-Tenant-ID header",
        tags = {"Tenant Management"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Current tenant information retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\":true,\"message\":\"Current tenant information\",\"data\":{\"tenantId\":\"default-sqool\",\"isDefault\":true,\"isValid\":true}}"
                )
            )
        )
    })
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentTenant(
            @Parameter(
                description = "Tenant identifier to get information for",
                example = "default-sqool",
                required = false
            )
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId,
            HttpServletRequest request) {
        
        try {
            String currentTenant = (tenantId != null && !tenantId.isEmpty()) ? tenantId : "default-sqool";
            
            Map<String, Object> tenantInfo = new HashMap<>();
            tenantInfo.put("tenantId", currentTenant);
            tenantInfo.put("isDefault", "default-sqool".equals(currentTenant));
            tenantInfo.put("isValid", tenantDataSourceService.isTenantValidFromDatabase(currentTenant));
            tenantInfo.put("headerName", "X-Tenant-ID");
            tenantInfo.put("requestPath", request.getRequestURI());
            
            // Get tenant from request attributes (set by filter)
            String filterTenantId = (String) request.getAttribute("tenantId");
            if (filterTenantId != null) {
                tenantInfo.put("resolvedByFilter", filterTenantId);
            }
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success("Current tenant information", tenantInfo);
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting current tenant information", e);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.error("Error getting tenant information: " + e.getMessage());
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @Operation(
        summary = "Register New Tenant",
        description = "Register a new tenant with database creation and configuration",
        tags = {"Tenant Management"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Tenant registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\":true,\"message\":\"Tenant registered successfully\",\"data\":{\"tenantId\":\"company_abc\",\"tenantName\":\"Company ABC Ltd\",\"databaseName\":\"company_abc_db\",\"databaseUrl\":\"jdbc:mysql://localhost:3306/company_abc_db\",\"isActive\":true,\"setupStatus\":\"Tenant registered successfully. Run setup to initialize database.\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation errors or tenant already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\":false,\"message\":\"Tenant with ID 'company_abc' already exists\",\"data\":null}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\":false,\"message\":\"Registration failed\",\"data\":null}"
                )
            )
        )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TenantRegistrationResponse>> registerTenant(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Tenant registration details",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = TenantRegistrationRequest.class),
                    examples = @ExampleObject(
                        value = "{\"tenantId\":\"company_abc\",\"tenantName\":\"Company ABC Ltd\",\"description\":\"ABC Company tenant for learning management\",\"databaseName\":\"company_abc_db\"}"
                    )
                )
            )
            @Valid @RequestBody TenantRegistrationRequest registerRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {
        
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(
                                error -> error.getField(),
                                error -> error.getDefaultMessage(),
                                (existing, replacement) -> existing
                        ));
                
                ApiResponse<TenantRegistrationResponse> response = ApiResponse.error("Validation failed", null);
                response.setErrors(errors);
                response.setPath(request.getRequestURI());
                return ResponseEntity.badRequest().body(response);
            }
            
            logger.info("Registering tenant: {} with name: {}", 
                       registerRequest.getTenantId(), 
                       registerRequest.getTenantName());
            
            TenantRegistrationResponse tenantResponse = tenantManagementService.registerTenant(registerRequest);
            
            ApiResponse<TenantRegistrationResponse> response = ApiResponse.success("Tenant registered successfully", tenantResponse);
            response.setPath(request.getRequestURI());
            
            logger.info("Tenant {} registered successfully", registerRequest.getTenantId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Registration failed for tenant: {}", registerRequest.getTenantId(), e);
            
            ApiResponse<TenantRegistrationResponse> response = ApiResponse.error("Registration failed: " + e.getMessage());
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @Operation(
        summary = "Setup Tenant Database",
        description = "Initialize tenant database with migrations and seed data including admin user (admin/admin123)",
        tags = {"Tenant Management"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Tenant setup completed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\":true,\"message\":\"Tenant setup completed\",\"data\":{\"tenantId\":\"company_abc\",\"migrationStatus\":\"SUCCESS\",\"changesetsExecuted\":13,\"message\":\"Tenant setup completed successfully. Admin user created with credentials: admin/admin123\",\"success\":true,\"adminCredentials\":\"Username: admin, Password: admin123\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Tenant not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\":false,\"message\":\"Tenant not found: company_abc\",\"data\":null}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Setup failed",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\":false,\"message\":\"Tenant setup failed\",\"data\":null}"
                )
            )
        )
    })
    @PostMapping("/{tenantId}/setup")
    public ResponseEntity<ApiResponse<TenantSetupResponse>> setupTenant(
            @Parameter(
                description = "Tenant identifier to setup",
                example = "company_abc",
                required = true
            )
            @PathVariable String tenantId,
            HttpServletRequest request) {
        
        try {
            logger.info("Setting up tenant: {}", tenantId);
            
            TenantSetupResponse setupResponse = tenantManagementService.setupTenant(tenantId);
            
            if (setupResponse.isSuccess()) {
                ApiResponse<TenantSetupResponse> response = ApiResponse.success("Tenant setup completed", setupResponse);
                response.setPath(request.getRequestURI());
                
                logger.info("Tenant {} setup completed successfully", tenantId);
                
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<TenantSetupResponse> response = ApiResponse.error("Tenant setup failed", setupResponse);
                response.setPath(request.getRequestURI());
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Setup failed for tenant: {}", tenantId, e);
            
            ApiResponse<TenantSetupResponse> response = ApiResponse.error("Setup failed: " + e.getMessage());
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
package com.sqool.sqoolbus.controller;

import com.sqool.sqoolbus.dto.ApiResponse;
import com.sqool.sqoolbus.service.TenantDataSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/tenants")
@Tag(name = "Tenant Management", description = "Operations for managing tenant information and validation")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TenantController {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantController.class);
    
    @Autowired
    private TenantDataSourceService tenantDataSourceService;
    
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
}
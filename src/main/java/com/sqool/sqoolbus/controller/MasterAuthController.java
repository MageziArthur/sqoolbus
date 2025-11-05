package com.sqool.sqoolbus.controller;

import com.sqool.sqoolbus.dto.ApiResponse;
import com.sqool.sqoolbus.dto.MasterLoginRequest;
import com.sqool.sqoolbus.dto.MasterLoginResponse;
import com.sqool.sqoolbus.service.MasterAuthService;
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

import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Master Authentication", description = "System-level authentication endpoints for master database access")
@RestController
@RequestMapping("/api/master/auth")
public class MasterAuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(MasterAuthController.class);
    
    @Autowired
    private MasterAuthService masterAuthService;
    
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
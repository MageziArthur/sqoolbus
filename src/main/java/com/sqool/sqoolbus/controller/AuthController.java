package com.sqool.sqoolbus.controller;

import com.sqool.sqoolbus.config.SqoolbusProperties;
import com.sqool.sqoolbus.dto.ApiResponse;
import com.sqool.sqoolbus.dto.LoginRequest;
import com.sqool.sqoolbus.dto.LoginResponse;
import com.sqool.sqoolbus.dto.RegisterRequest;
import com.sqool.sqoolbus.dto.EnhancedLoginRequest;
import com.sqool.sqoolbus.dto.EnhancedLoginResponse;
import com.sqool.sqoolbus.dto.OtpResponse;
import com.sqool.sqoolbus.tenant.entity.UserOtp;
import com.sqool.sqoolbus.security.JwtTokenProvider;
import com.sqool.sqoolbus.service.AuthService;
import com.sqool.sqoolbus.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Authentication REST Controller for SqoolBus Multitenant Application
 * 
 * This controller provides standardized authentication endpoints for the application.
 * All endpoints work with the default tenant configuration.
 * 
 * Features:
 * - Username or email-based authentication
 * - JWT token-based stateless authentication
 * - Comprehensive error handling and validation
 * - Detailed API documentation via OpenAPI/Swagger
 * 
 * Supported Headers:
 * - Authorization: Bearer token for authenticated endpoints
 * 
 * Endpoints:
 * - POST /api/auth/login - User authentication with username/email
 * - POST /api/auth/register - User registration
 * - GET /api/auth/health - Health check endpoint
 * - GET /api/auth/validate - Token validation
 * - GET /api/auth/me - Get current user info
 * - GET /api/auth/health - Service health check
 * - GET /api/auth/tenants/info - Tenant information
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and tenant management operations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private OtpService otpService;
    
    @Autowired
    private SqoolbusProperties sqoolbusProperties;
    
    @Value("${sqoolbus.multitenancy.default-tenant}")
    private String defaultTenant;
    
    @Operation(
        summary = "User Login",
        description = "Authenticate user with username/email and password for a specific tenant. Returns JWT token for API access.",
        tags = {"Authentication"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "Successful login",
                    value = "{\"success\":true,\"message\":\"Login successful\",\"data\":{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\"tokenType\":\"Bearer\",\"expiresIn\":86400,\"expiresAt\":\"2025-11-02T18:30:00\",\"user\":{\"id\":1,\"username\":\"admin\",\"email\":\"admin@sqool.com\",\"firstName\":\"System\",\"lastName\":\"Administrator\",\"roles\":[\"ADMIN\"],\"permissions\":[\"USER_READ\",\"USER_WRITE\",\"TENANT_ADMIN\"]}},\"path\":\"/api/auth/login\",\"timestamp\":\"2025-11-01T18:30:00Z\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid credentials or validation errors",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Invalid credentials",
                        value = "{\"success\":false,\"message\":\"Invalid credentials\",\"data\":null,\"path\":\"/api/auth/login\",\"timestamp\":\"2025-11-01T18:30:00Z\"}"
                    ),
                    @ExampleObject(
                        name = "Validation error",
                        value = "{\"success\":false,\"message\":\"Validation failed\",\"data\":null,\"errors\":{\"usernameOrEmail\":\"Username or email is required\"},\"path\":\"/api/auth/login\",\"timestamp\":\"2025-11-01T18:30:00Z\"}"
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Authentication failed",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Authentication failed",
                    value = "{\"success\":false,\"message\":\"User not found or invalid password\",\"data\":null,\"path\":\"/api/auth/login\",\"timestamp\":\"2025-11-01T18:30:00Z\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Account deactivated",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Account deactivated",
                    value = "{\"success\":false,\"message\":\"User account is deactivated\",\"data\":null,\"path\":\"/api/auth/login\",\"timestamp\":\"2025-11-01T18:30:00Z\"}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Server error",
                    value = "{\"success\":false,\"message\":\"Internal server error\",\"data\":null,\"path\":\"/api/auth/login\",\"timestamp\":\"2025-11-01T18:30:00Z\"}"
                )
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User login credentials (username/email and password)",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = {
                        @ExampleObject(
                            name = "Login with username",
                            value = "{\"usernameOrEmail\":\"admin\",\"password\":\"admin123\"}"
                        ),
                        @ExampleObject(
                            name = "Login with email",
                            value = "{\"usernameOrEmail\":\"admin@sqool.com\",\"password\":\"admin123\"}"
                        )
                    }
                )
            )
            @Valid @RequestBody LoginRequest loginRequest,
            @Parameter(
                description = "Tenant identifier for multi-tenant operation. If not provided, defaults to the system default tenant.",
                example = "default_sqool",
                required = false
            )
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId,
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
                
                ApiResponse<LoginResponse> response = ApiResponse.error("Validation failed", null);
                response.setPath(request.getRequestURI());
                response.setErrors(errors);
                return ResponseEntity.badRequest().body(response);
            }
            
            logger.info("Login attempt for username: {} with tenant: {}", 
                       loginRequest.getUsername(), 
                       tenantId);
            
            // Check if 2FA is enforced globally
            if (sqoolbusProperties.getSecurity().isEnforce2FA()) {
                logger.warn("Regular login attempted but 2FA is enforced for user: {} in tenant: {}", 
                           loginRequest.getUsername(), tenantId);
                ApiResponse<LoginResponse> response = ApiResponse.error(
                    "Two-factor authentication is required. Please use the enhanced login endpoint (/api/auth/login/enhanced).");
                response.setPath(request.getRequestURI());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            LoginResponse loginResponse = authService.authenticateUser(loginRequest, tenantId);
            
            ApiResponse<LoginResponse> response = ApiResponse.success("Login successful", loginResponse);
            response.setPath(request.getRequestURI());
            
            logger.info("User {} logged in successfully", loginRequest.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Login failed for username: {}", loginRequest.getUsername(), e);
            
            String errorMessage = e.getMessage();
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            
            // Determine appropriate HTTP status based on error type
            if (errorMessage.contains("not found")) {
                status = HttpStatus.UNAUTHORIZED; // Don't reveal if user exists
                errorMessage = "Invalid credentials";
            } else if (errorMessage.contains("deactivated")) {
                status = HttpStatus.FORBIDDEN;
            } else if (errorMessage.contains("Invalid password")) {
                status = HttpStatus.UNAUTHORIZED;
                errorMessage = "Invalid credentials";
            }
            
            ApiResponse<LoginResponse> response = ApiResponse.error(errorMessage);
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            logger.error("Unexpected error during login for username: {}", loginRequest.getUsername(), e);
            
            ApiResponse<LoginResponse> response = ApiResponse.error("Internal server error");
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @Operation(
        summary = "User Registration",
        description = "Register a new user for a specific tenant",
        tags = {"Authentication"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Registration successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\":true,\"message\":\"User registered successfully\",\"data\":{\"token\":\"jwt-token-here\",\"type\":\"Bearer\",\"userId\":1,\"email\":\"user@example.com\",\"tenantId\":\"default-sqool\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation errors or user already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\":false,\"message\":\"Email already registered\",\"data\":null}"
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
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User registration details",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = RegisterRequest.class),
                    examples = @ExampleObject(
                        value = "{\"username\":\"john_doe\",\"email\":\"john.doe@example.com\",\"password\":\"password123\",\"firstName\":\"John\",\"lastName\":\"Doe\"}"
                    )
                )
            )
            @Valid @RequestBody RegisterRequest registerRequest,
            @Parameter(
                description = "Tenant identifier for multi-tenant operation",
                example = "default-sqool",
                required = false
            )
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId,
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
                
                ApiResponse<LoginResponse> response = ApiResponse.error("Validation failed", null);
                response.setPath(request.getRequestURI());
                return ResponseEntity.badRequest().body(response);
            }
            
            logger.info("Registration attempt for username: {} with email: {} for tenant: {}", 
                       registerRequest.getUsername(), 
                       registerRequest.getEmail(),
                       tenantId);
            
            LoginResponse registerResponse = authService.registerUser(registerRequest, tenantId);
            
            ApiResponse<LoginResponse> response = ApiResponse.success("Registration successful", registerResponse);
            response.setPath(request.getRequestURI());
            
            logger.info("User {} registered successfully", registerRequest.getUsername());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Registration failed for username: {}", registerRequest.getUsername(), e);
            
            ApiResponse<LoginResponse> response = ApiResponse.error("Registration failed: " + e.getMessage());
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        try {
            // In a stateless JWT implementation, logout is typically handled client-side
            // by discarding the token. For server-side logout, you'd need to maintain
            // a blacklist of invalid tokens or use shorter expiration times with refresh tokens
            
            logger.info("Logout request received");
            
            ApiResponse<String> response = ApiResponse.success("Logout successful", 
                    "Token has been invalidated. Please discard it on client side.");
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Logout failed", e);
            
            ApiResponse<String> response = ApiResponse.error("Logout failed: " + e.getMessage());
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest request) {
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ApiResponse<Map<String, Object>> response = ApiResponse.error("Invalid token format");
                response.setPath(request.getRequestURI());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String token = authHeader.substring(7);
            boolean isValid = authService.validateToken(token);
            
            if (isValid) {
                Map<String, Object> tokenInfo = new HashMap<>();
                tokenInfo.put("valid", true);
                tokenInfo.put("username", tokenProvider.getUsernameFromToken(token));
                tokenInfo.put("tenantId", tokenProvider.getTenantIdFromToken(token));
                tokenInfo.put("roles", tokenProvider.getRolesFromToken(token));
                tokenInfo.put("permissions", tokenProvider.getPermissionsFromToken(token));
                tokenInfo.put("expiresAt", tokenProvider.getExpirationLocalDateTimeFromToken(token));
                
                ApiResponse<Map<String, Object>> response = ApiResponse.success("Token is valid", tokenInfo);
                response.setPath(request.getRequestURI());
                
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<Map<String, Object>> response = ApiResponse.error("Token is invalid or expired");
                response.setPath(request.getRequestURI());
                
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Token validation failed", e);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.error("Token validation failed: " + e.getMessage());
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<LoginResponse.UserInfo>> getCurrentUser(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest request) {
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ApiResponse<LoginResponse.UserInfo> response = ApiResponse.error("Invalid token format");
                response.setPath(request.getRequestURI());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String token = authHeader.substring(7);
            
            if (!authService.validateToken(token)) {
                ApiResponse<LoginResponse.UserInfo> response = ApiResponse.error("Invalid or expired token");
                response.setPath(request.getRequestURI());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Extract user info from token
            String username = tokenProvider.getUsernameFromToken(token);
            String tenantId = tokenProvider.getTenantIdFromToken(token);
            
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setUsername(username);
            userInfo.setRoles(tokenProvider.getRolesFromToken(token));
            userInfo.setPermissions(tokenProvider.getPermissionsFromToken(token));
            
            ApiResponse<LoginResponse.UserInfo> response = ApiResponse.success("User info retrieved", userInfo);
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to get current user info", e);
            
            ApiResponse<LoginResponse.UserInfo> response = ApiResponse.error("Failed to get user info: " + e.getMessage());
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @Operation(
        summary = "Health Check",
        description = "Check the health status of the authentication service",
        tags = {"Health"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Service is healthy",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\":true,\"message\":\"Authentication service is running\",\"data\":{\"status\":\"UP\",\"service\":\"Authentication Service\",\"version\":\"1.0.0\"}}"
                )
            )
        )
    })
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health(HttpServletRequest request) {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("service", "Authentication Service");
        healthInfo.put("version", "1.0.0");
        healthInfo.put("tenant", "Using default tenant configuration");
        
        ApiResponse<Map<String, Object>> response = ApiResponse.success("Authentication service is running", healthInfo);
        response.setPath(request.getRequestURI());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/tenants/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTenantInfo(
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId,
            HttpServletRequest request) {
        
        Map<String, Object> tenantInfo = new HashMap<>();
        String currentTenant = (tenantId != null && !tenantId.isEmpty()) ? tenantId : "default-sqool";
        
        tenantInfo.put("currentTenant", currentTenant);
        tenantInfo.put("defaultTenant", "default-sqool");
        tenantInfo.put("description", "Application configured to use default tenant");
        
        ApiResponse<Map<String, Object>> response = ApiResponse.success("Tenant information", tenantInfo);
        response.setPath(request.getRequestURI());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Enhanced tenant login endpoint with 2FA support
     */
    @Operation(
        summary = "Enhanced Tenant Login with 2FA",
        description = "Enhanced authentication endpoint that supports two-factor authentication for tenant users. " +
                     "Can handle both single-step login (without 2FA) and two-step login (with 2FA). " +
                     "When 2FA is enabled, first call authenticates credentials and sends OTP, " +
                     "second call with OTP code completes the authentication.",
        tags = {"Authentication"}
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Authentication successful or 2FA pending",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Successful Login (No 2FA)",
                        value = "{\"success\":true,\"message\":\"Authentication successful\",\"data\":{\"token\":\"eyJhbGciOiJIUzI1NiJ9...\",\"tokenType\":\"Bearer\",\"tenantId\":\"default_sqool\",\"pending2FA\":false}}"
                    ),
                    @ExampleObject(
                        name = "2FA Pending",
                        value = "{\"success\":true,\"message\":\"2FA required. OTP sent to your email.\",\"data\":{\"pending2FA\":true,\"twoFASessionId\":\"temp-123\",\"maskedDeliveryDestination\":\"us***@tenant.com\",\"otpExpiryMinutes\":5}}"
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "Invalid request data"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", 
            description = "Invalid credentials or OTP"
        )
    })
    @PostMapping("/login/enhanced")
    public ResponseEntity<ApiResponse<EnhancedLoginResponse>> enhancedLogin(
            @Valid @RequestBody EnhancedLoginRequest request,
            BindingResult bindingResult,
            @Parameter(description = "Tenant ID", example = "default_sqool")
            @RequestHeader(value = "X-Tenant-ID", defaultValue = "") String tenantId,
            HttpServletRequest httpRequest) {
        
        logger.info("Enhanced tenant login attempt for user: {} with 2FA: {} for tenant: {}", 
                   request.getUsernameOrEmail(), request.isEnable2FA(), tenantId);
        
        // Use default tenant if not provided
        if (tenantId == null || tenantId.trim().isEmpty()) {
            tenantId = defaultTenant;
        }
        
        // Validate request
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(java.util.stream.Collectors.joining(", "));
            
            logger.warn("Enhanced tenant login validation failed: {}", errors);
            
            return ResponseEntity.badRequest().body(
                ApiResponse.<EnhancedLoginResponse>error("Validation failed")
            );
        }
        
        try {
            // If OTP code is provided, this is step 2 of 2FA login
            if (request.getOtpCode() != null && !request.getOtpCode().trim().isEmpty()) {
                return handleTenantOtpVerification(request, tenantId, httpRequest);
            }
            
            // Step 1: Verify credentials
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(request.getUsernameOrEmail());
            loginRequest.setPassword(request.getPassword());
            LoginResponse authResult = authService.authenticateUser(loginRequest, tenantId);
            
            if (authResult == null) {
                logger.warn("Enhanced tenant login failed for user: {} in tenant: {}", request.getUsernameOrEmail(), tenantId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponse.error("Invalid credentials")
                );
            }
            
            // Check if 2FA should be enforced based on configuration or user request
            boolean require2FA = sqoolbusProperties.getSecurity().isEnforce2FA() || request.isEnable2FA();
            
            // If 2FA is not required, return token immediately
            if (!require2FA) {
                EnhancedLoginResponse.UserInfo userInfo = new EnhancedLoginResponse.UserInfo(
                    authResult.getUser().getId(),
                    authResult.getUser().getUsername(),
                    authResult.getUser().getEmail(),
                    authResult.getUser().getFirstName(),
                    authResult.getUser().getLastName(),
                    authResult.getUser().getRoles(),
                    authResult.getUser().getPermissions()
                );
                
                EnhancedLoginResponse response = EnhancedLoginResponse.successfulLogin(
                    authResult.getToken(),
                    authResult.getTokenType(),
                    authResult.getExpiresIn(),
                    authResult.getExpiresAt().toString(),
                    tenantId,
                    userInfo
                );
                
                logger.info("Enhanced tenant login successful for user: {} in tenant: {} (no 2FA)", 
                           request.getUsernameOrEmail(), tenantId);
                return ResponseEntity.ok(
                    ApiResponse.success("Authentication successful", response)
                );
            }
            
            // 2FA is enabled - generate and send OTP
            return handleTenant2FAInitiation(request, authResult, tenantId, httpRequest);
            
        } catch (Exception e) {
            logger.error("Unexpected error during enhanced tenant login for user {} in tenant {}: {}", 
                        request.getUsernameOrEmail(), tenantId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                ApiResponse.error("Internal server error occurred")
            );
        }
    }
    
    /**
     * Handle 2FA initiation for tenant authentication
     */
    private ResponseEntity<ApiResponse<EnhancedLoginResponse>> handleTenant2FAInitiation(
            EnhancedLoginRequest request, LoginResponse authResult, String tenantId, HttpServletRequest httpRequest) {
        
        try {
            // Parse delivery method
            UserOtp.DeliveryMethod deliveryMethod;
            try {
                deliveryMethod = UserOtp.DeliveryMethod.valueOf(request.getDeliveryMethod().toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid delivery method: {}", request.getDeliveryMethod());
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("Invalid delivery method")
                );
            }
            
            // Get client information
            String clientIp = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            
            // Note: For tenant users, we need to use the username from the tenant database
            // The OTP service will need to be modified to handle tenant users vs master users
            // For now, we'll use the username, but this needs tenant-aware OTP management
            
            // Generate OTP using the tenant user's username
            OtpResponse otpResult = otpService.generateOtp(
                authResult.getUser().getUsername(), // Use tenant username
                UserOtp.OtpType.LOGIN_2FA,
                deliveryMethod,
                authResult.getUser().getEmail(), // Use tenant user's email
                clientIp,
                userAgent
            );
            
            if (!otpResult.isSuccess()) {
                logger.warn("OTP generation failed for tenant user {}: {}", request.getUsernameOrEmail(), otpResult.getMessage());
                return ResponseEntity.badRequest().body(
                    ApiResponse.error(otpResult.getMessage())
                );
            }
            
            // Create 2FA pending response
            String sessionId = "tenant-2fa-" + System.currentTimeMillis() + "-" + request.getUsernameOrEmail().hashCode();
            
            EnhancedLoginResponse.UserInfo userInfo = new EnhancedLoginResponse.UserInfo(
                authResult.getUser().getId(),
                authResult.getUser().getUsername(),
                authResult.getUser().getEmail(),
                authResult.getUser().getFirstName(),
                authResult.getUser().getLastName(),
                authResult.getUser().getRoles(),
                authResult.getUser().getPermissions()
            );
            
            EnhancedLoginResponse response = EnhancedLoginResponse.pending2FA(
                sessionId,
                otpResult.getMaskedDestination(),
                otpResult.getExpiryMinutes(),
                tenantId,
                userInfo
            );
            
            logger.info("2FA initiated for tenant user: {} in tenant: {}, OTP sent to: {}", 
                       request.getUsernameOrEmail(), tenantId, otpResult.getMaskedDestination());
            
            return ResponseEntity.ok(
                ApiResponse.success("2FA required. OTP sent to your registered email.", response)
            );
            
        } catch (Exception e) {
            logger.error("Error during tenant 2FA initiation for user {} in tenant {}: {}", 
                        request.getUsernameOrEmail(), tenantId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                ApiResponse.error("Failed to initiate 2FA")
            );
        }
    }
    
    /**
     * Handle OTP verification for tenant authentication
     */
    private ResponseEntity<ApiResponse<EnhancedLoginResponse>> handleTenantOtpVerification(
            EnhancedLoginRequest request, String tenantId, HttpServletRequest httpRequest) {
        
        try {
            // First, authenticate the user to get their information for OTP verification
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(request.getUsernameOrEmail());
            loginRequest.setPassword(request.getPassword());
            LoginResponse authResult = authService.authenticateUser(loginRequest, tenantId);
            
            if (authResult == null) {
                logger.warn("Authentication failed during OTP verification for tenant user: {} in tenant: {}", 
                           request.getUsernameOrEmail(), tenantId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponse.error("Invalid credentials")
                );
            }
            
            // Verify OTP using the tenant user's username
            OtpResponse otpResult = otpService.verifyOtp(
                authResult.getUser().getUsername(), // Use tenant username
                request.getOtpCode(),
                UserOtp.OtpType.LOGIN_2FA
            );
            
            if (!otpResult.isSuccess()) {
                logger.warn("OTP verification failed for tenant user {}: {}", request.getUsernameOrEmail(), otpResult.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponse.error(otpResult.getMessage())
                );
            }
            
            // OTP verified - create successful login response
            EnhancedLoginResponse.UserInfo userInfo = new EnhancedLoginResponse.UserInfo(
                authResult.getUser().getId(),
                authResult.getUser().getUsername(),
                authResult.getUser().getEmail(),
                authResult.getUser().getFirstName(),
                authResult.getUser().getLastName(),
                authResult.getUser().getRoles(),
                authResult.getUser().getPermissions()
            );
            
            EnhancedLoginResponse response = EnhancedLoginResponse.successfulLogin(
                authResult.getToken(),
                authResult.getTokenType(),
                authResult.getExpiresIn(),
                authResult.getExpiresAt().toString(),
                tenantId,
                userInfo
            );
            
            logger.info("2FA authentication completed successfully for tenant user: {} in tenant: {}", 
                       request.getUsernameOrEmail(), tenantId);
            
            return ResponseEntity.ok(
                ApiResponse.success("Authentication successful", response)
            );
            
        } catch (Exception e) {
            logger.error("Error during tenant OTP verification for user {} in tenant {}: {}", 
                        request.getUsernameOrEmail(), tenantId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                ApiResponse.error("Failed to verify OTP")
            );
        }
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
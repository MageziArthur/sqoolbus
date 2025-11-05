package com.sqool.sqoolbus.service;

import com.sqool.sqoolbus.config.multitenancy.TenantContext;
import com.sqool.sqoolbus.dto.LoginRequest;
import com.sqool.sqoolbus.dto.LoginResponse;
import com.sqool.sqoolbus.dto.RegisterRequest;
import com.sqool.sqoolbus.security.JwtTokenProvider;
import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.entity.Role;
import com.sqool.sqoolbus.tenant.entity.Permission;
import com.sqool.sqoolbus.tenant.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Value("${sqoolbus.multitenancy.default-tenant}")
    private String defaultTenant;
    
    public LoginResponse authenticateUser(LoginRequest loginRequest, String tenantId) {
        try {
            // Set tenant context
            if (tenantId != null && !tenantId.isEmpty()) {
                TenantContext.setTenantId(tenantId);
            } else {
                // Default to default tenant
                TenantContext.setTenantId(defaultTenant);
            }
            
            // Find user by username or email
            Optional<User> userOptional = userRepository.findByUsernameOrEmailAndIsActive(loginRequest.getUsername());
            
            if (userOptional.isEmpty()) {
                throw new RuntimeException("User not found");
            }
            
            User user = userOptional.get();
            
            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid password");
            }
            
            // Extract roles and permissions
            Set<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            
            Set<String> permissions = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(Permission::getName)
                    .collect(Collectors.toSet());
            
            // Generate JWT token
            String token = tokenProvider.generateToken(
                    user.getUsername(),
                    TenantContext.getTenantId(),
                    roles,
                    permissions
            );
            
            // Create user info
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    roles,
                    permissions
            );
            
            // Create login response
            return new LoginResponse(
                    token,
                    tokenProvider.getExpirationTimeInSeconds(),
                    tokenProvider.getExpirationLocalDateTimeFromToken(token),
                    userInfo
            );
            
        } finally {
            TenantContext.clear();
        }
    }
    
    public LoginResponse registerUser(RegisterRequest registerRequest, String tenantId) {
        try {
            // Set tenant context
            if (tenantId != null && !tenantId.isEmpty()) {
                TenantContext.setTenantId(tenantId);
            } else {
                // Default to default tenant
                TenantContext.setTenantId(defaultTenant);
            }
            
            // Check if username already exists
            if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
                throw new RuntimeException("Username is already taken: " + registerRequest.getUsername());
            }
            
            // Check if email already exists
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                throw new RuntimeException("Email is already registered: " + registerRequest.getEmail());
            }
            
            // Create new user
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setIsActive(true);
            user.setIsEmailVerified(false);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            // Save user (without roles for now - can be assigned separately)
            User savedUser = userRepository.save(user);
            
            logger.info("User registered successfully: {}", savedUser.getUsername());
            
            // For registration, we'll return a simple response without auto-login
            // In a real app, you might want to send verification email first
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                    savedUser.getId(),
                    savedUser.getUsername(),
                    savedUser.getEmail(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
                    Set.of(), // No roles assigned yet
                    Set.of()  // No permissions yet
            );
            
            // Generate basic token (or you might prefer to require email verification first)
            String token = tokenProvider.generateToken(
                    savedUser.getUsername(),
                    TenantContext.getTenantId(),
                    Set.of(),
                    Set.of()
            );
            
            return new LoginResponse(
                    token,
                    tokenProvider.getExpirationTimeInSeconds(),
                    tokenProvider.getExpirationLocalDateTimeFromToken(token),
                    userInfo
            );
            
        } finally {
            TenantContext.clear();
        }
    }
    
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }
}
package com.sqool.sqoolbus.service;

import com.sqool.sqoolbus.dto.MasterLoginRequest;
import com.sqool.sqoolbus.dto.MasterLoginResponse;
import com.sqool.sqoolbus.master.entity.Permission;
import com.sqool.sqoolbus.master.entity.Role;
import com.sqool.sqoolbus.master.entity.User;
import com.sqool.sqoolbus.master.entity.UserRole;
import com.sqool.sqoolbus.master.repository.MasterPermissionRepository;
import com.sqool.sqoolbus.master.repository.MasterUserRepository;
import com.sqool.sqoolbus.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MasterAuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(MasterAuthService.class);
    
    @Autowired
    private MasterUserRepository masterUserRepository;
    
    @Autowired
    private MasterPermissionRepository masterPermissionRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Value("${app.jwtExpirationInMs:3600000}") // Default 1 hour
    private Long jwtExpirationInMs;
    
    /**
     * Authenticate user against master database without tenant context
     */
    public MasterLoginResponse authenticateUser(MasterLoginRequest loginRequest) {
        logger.info("Attempting master authentication for user: {}", loginRequest.getUsernameOrEmail());
        
        // Find user by username or email
        User user = masterUserRepository.findActiveUserByUsernameOrEmail(loginRequest.getUsernameOrEmail())
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            logger.warn("Invalid password attempt for user: {}", user.getUsername());
            throw new RuntimeException("Invalid credentials");
        }
        
        // Check if user is active
        if (!user.getIsActive()) {
            logger.warn("Login attempt for inactive user: {}", user.getUsername());
            throw new RuntimeException("User account is inactive");
        }
        
        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        masterUserRepository.save(user);
        
        // Get user's system-level roles (roles without tenant scope)
        List<String> roles = user.getUserRoles().stream()
            .filter(ur -> ur.getTenant() == null) // Only system-level roles
            .map(ur -> ur.getRole().getName())
            .collect(Collectors.toList());
        
        // Get user's system-level permissions
        List<String> permissions = getUserSystemPermissions(user);
        
        // Generate JWT token with system context
        String token = jwtTokenProvider.generateMasterToken(user.getUsername(), user.getId(), roles);
        
        logger.info("Master authentication successful for user: {}", user.getUsername());
        
        MasterLoginResponse response = new MasterLoginResponse(
            token,
            jwtExpirationInMs / 1000, // Convert to seconds
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            roles,
            permissions
        );
        
        response.setIsActive(user.getIsActive());
        response.setLastLoginAt(user.getLastLoginAt());
        
        return response;
    }
    
    /**
     * Get user's system-level permissions (permissions without tenant scope)
     */
    private List<String> getUserSystemPermissions(User user) {
        return user.getUserRoles().stream()
            .filter(ur -> ur.getTenant() == null) // Only system-level roles
            .map(UserRole::getRole)
            .flatMap(role -> role.getRolePermissions().stream())
            .map(rp -> rp.getPermission().getName())
            .distinct()
            .collect(Collectors.toList());
    }
    
    /**
     * Check if user has a specific system-level permission
     */
    public boolean hasSystemPermission(String username, String permissionName) {
        User user = masterUserRepository.findByUsername(username)
            .orElse(null);
        
        if (user == null || !user.getIsActive()) {
            return false;
        }
        
        List<String> permissions = getUserSystemPermissions(user);
        return permissions.contains(permissionName) || permissions.contains("SYSTEM_ADMIN");
    }
    
    /**
     * Check if user has a specific system-level role
     */
    public boolean hasSystemRole(String username, String roleName) {
        User user = masterUserRepository.findByUsername(username)
            .orElse(null);
        
        if (user == null || !user.getIsActive()) {
            return false;
        }
        
        return user.getUserRoles().stream()
            .filter(ur -> ur.getTenant() == null) // Only system-level roles
            .map(ur -> ur.getRole().getName())
            .anyMatch(role -> role.equals(roleName));
    }
    
    /**
     * Get user by username for authentication
     */
    public User getUserByUsername(String username) {
        return masterUserRepository.findByUsername(username).orElse(null);
    }
    
    /**
     * Validate JWT token and return user details
     */
    public User validateTokenAndGetUser(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return null;
        }
        
        String username = jwtTokenProvider.getUsernameFromToken(token);
        return getUserByUsername(username);
    }
}
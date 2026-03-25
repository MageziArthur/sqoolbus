package com.sqool.sqoolbus.service;

import com.sqool.sqoolbus.dto.MasterLoginRequest;
import com.sqool.sqoolbus.dto.MasterLoginResponse;
import com.sqool.sqoolbus.dto.ParentSignupRequest;
import com.sqool.sqoolbus.dto.ParentSignupResponse;
import com.sqool.sqoolbus.dto.RiderSignupResponse;
import com.sqool.sqoolbus.exception.DuplicateResourceException;
import com.sqool.sqoolbus.exception.ResourceNotFoundException;
import com.sqool.sqoolbus.master.entity.*;
import com.sqool.sqoolbus.master.repository.*;
import com.sqool.sqoolbus.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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
    private MasterRoleRepository roleRepository;
    
    @Autowired
    private MasterUserRoleRepository userRoleRepository;
    
    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantResolutionService tenantResolutionService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Value("${app.jwtExpirationInMs:3600000}") // Default 1 hour
    private Long jwtExpirationInMs;
    
    /**
     * Register a new parent user in the master database
     */
    public ParentSignupResponse registerParent(ParentSignupRequest request, String tenantId) {
        try {
            logger.info("Starting parent registration for username: {}, tenant: {}", request.getUsername(), tenantId);

            Tenant tenant = resolveTenantForSchoolOrNull(tenantId, request.getSchoolId());

            // Check if username already exists
            if (masterUserRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("User", "username", request.getUsername());
            }

            // Check if email already exists
            if (masterUserRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("User", "email", request.getEmail());
            }

            // Get PARENT role
            Optional<Role> parentRoleOptional = roleRepository.findByName("PARENT");
            if (parentRoleOptional.isEmpty()) {
                throw new ResourceNotFoundException("Role", "name", "PARENT");
            }
            Role parentRole = parentRoleOptional.get();

            // Create new user in master database
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setIsActive(true);
            user.setIsEmailVerified(false);

            // Save user
            User savedUser = masterUserRepository.save(user);

            createUserRole(savedUser, parentRole, tenant);

            String tenantName = tenant != null ? tenant.getTenantName() : "<none>";
            logger.info("Parent user registered successfully in master DB: {} for tenant: {}", 
                       savedUser.getUsername(), tenantName);

            // Get roles and permissions for this user-tenant combination
            Set<String> roles = new HashSet<>();
            roles.add(parentRole.getName());

            Set<String> permissions = parentRole.getRolePermissions().stream()
                    .map(rp -> rp.getPermission().getName())
                    .collect(Collectors.toSet());

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(
                    savedUser.getUsername(),
                    tenant != null ? tenant.getTenantId() : null,
                    roles,
                    permissions,
                    request.getSchoolId()
            );

            // Create response objects
            ParentSignupResponse.ParentUserInfo userInfo = new ParentSignupResponse.ParentUserInfo(
                    savedUser.getId(),
                    savedUser.getUsername(),
                    savedUser.getEmail(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
                    roles,
                    permissions,
                    savedUser.getIsActive(),
                    savedUser.getIsEmailVerified()
            );

            // Profile info will be stored in tenant database separately if needed
            ParentSignupResponse.ParentProfileInfo profileInfo = new ParentSignupResponse.ParentProfileInfo(
                    null, // Profile ID will be in tenant DB
                    request.getPhoneNumber(),
                    request.getEmergencyContact(),
                    request.getEmergencyContactName(),
                    request.getAddress(),
                    request.getCity(),
                    request.getState(),
                    request.getZipCode(),
                    request.getOccupation(),
                    request.getWorkplace(),
                    request.getWorkPhone(),
                    request.getPreferredContactMethod()
            );

                ParentSignupResponse.SchoolInfo schoolInfo = null;
                if (request.getSchoolId() != null) {
                schoolInfo = new ParentSignupResponse.SchoolInfo(
                    request.getSchoolId(),
                    null,
                    null,
                    null,
                    null,
                    null
                );
                }

            ParentSignupResponse response = new ParentSignupResponse();
            response.setToken(token);
            response.setUser(userInfo);
            response.setProfile(profileInfo);
            response.setSchool(schoolInfo);
            
            return response;

        } catch (Exception e) {
            logger.error("Error during parent registration: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ParentSignupResponse registerParentBasic(String username, String email, String password,
                                                    String firstName, String lastName,
                                                    Long schoolId, String tenantId) {
        ParentSignupRequest request = new ParentSignupRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setSchoolId(schoolId);
        return registerParent(request, tenantId);
    }

    public RiderSignupResponse registerRiderBasic(String username, String email, String password,
                                                  String firstName, String lastName,
                                                  Long schoolId, String licenseNumber, String employeeId,
                                                  String tenantId) {
        try {
            logger.info("Starting rider registration for username: {}, tenant: {}", username, tenantId);

            Tenant tenant = resolveTenantForSchoolOrNull(tenantId, schoolId);

            if (username != null && !username.isBlank() && masterUserRepository.existsByUsername(username)) {
                throw new DuplicateResourceException("User", "username", username);
            }

            if (masterUserRepository.existsByEmail(email)) {
                throw new DuplicateResourceException("User", "email", email);
            }

            Optional<Role> riderRoleOptional = roleRepository.findByName("RIDER");
            if (riderRoleOptional.isEmpty()) {
                throw new ResourceNotFoundException("Role", "name", "RIDER");
            }
            Role riderRole = riderRoleOptional.get();

            String initialUsername = (username == null || username.isBlank())
                ? "tmp-" + UUID.randomUUID()
                : username;

            User user = new User();
            user.setUsername(initialUsername);
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setIsActive(true);
            user.setIsEmailVerified(false);

            User savedUser = masterUserRepository.save(user);

            if (username == null || username.isBlank()) {
                String riderCode = generateRiderCode(savedUser.getId());
                savedUser.setUsername(riderCode);
                savedUser = masterUserRepository.save(savedUser);
            }

            createUserRole(savedUser, riderRole, tenant);

            Set<String> roles = Set.of(riderRole.getName());
            Set<String> permissions = riderRole.getRolePermissions().stream()
                    .map(rp -> rp.getPermission().getName())
                    .collect(Collectors.toSet());

            String token = jwtTokenProvider.generateToken(
                    savedUser.getUsername(),
                    tenant != null ? tenant.getTenantId() : null,
                    roles,
                    permissions,
                    schoolId
            );

            RiderSignupResponse.RiderUserInfo userInfo = new RiderSignupResponse.RiderUserInfo(
                    savedUser.getId(),
                    savedUser.getUsername(),
                    savedUser.getEmail(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
                    roles,
                    permissions,
                    savedUser.getIsActive(),
                    savedUser.getIsEmailVerified()
            );

            RiderSignupResponse.RiderProfileInfo profileInfo = new RiderSignupResponse.RiderProfileInfo(
                    licenseNumber,
                    employeeId
            );

            return new RiderSignupResponse(
                    token,
                    jwtTokenProvider.getExpirationTimeInSeconds(),
                    jwtTokenProvider.getExpirationLocalDateTimeFromToken(token),
                    userInfo,
                    profileInfo
            );

        } catch (Exception e) {
            logger.error("Error during rider registration: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Tenant resolveTenantForSchoolOrNull(String tenantId, Long schoolId) {
        String resolvedTenantId = tenantId;
        if (resolvedTenantId == null && schoolId != null) {
            resolvedTenantId = tenantResolutionService.resolveTenantIdForSchool(schoolId);
        }
        if (resolvedTenantId == null || resolvedTenantId.isBlank()) {
            return null;
        }

        Optional<Tenant> tenantOptional = tenantRepository.findByTenantId(resolvedTenantId);
        if (tenantOptional.isEmpty()) {
            throw new ResourceNotFoundException("Tenant", "tenantId", resolvedTenantId);
        }

        return tenantOptional.get();
    }

    private void createUserRole(User user, Role role, Tenant tenant) {
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setTenant(tenant);
        userRoleRepository.save(userRole);
    }

    private String generateRiderCode(Long userId) {
        String paddedId = String.format("%06d", userId);
        return "RDR" + paddedId;
    }

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
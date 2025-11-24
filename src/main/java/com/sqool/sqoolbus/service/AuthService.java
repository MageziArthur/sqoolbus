package com.sqool.sqoolbus.service;

import com.sqool.sqoolbus.config.multitenancy.TenantContext;
import com.sqool.sqoolbus.dto.LoginRequest;
import com.sqool.sqoolbus.dto.LoginResponse;
import com.sqool.sqoolbus.dto.RegisterRequest;
import com.sqool.sqoolbus.dto.ParentSignupRequest;
import com.sqool.sqoolbus.dto.ParentSignupResponse;
import com.sqool.sqoolbus.exception.AuthenticationException;
import com.sqool.sqoolbus.exception.DuplicateResourceException;
import com.sqool.sqoolbus.exception.ResourceNotFoundException;
import com.sqool.sqoolbus.security.ActivityTrackingService;
import com.sqool.sqoolbus.security.JwtTokenProvider;
import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.entity.Role;
import com.sqool.sqoolbus.tenant.entity.Permission;
import com.sqool.sqoolbus.tenant.entity.hail.UserProfile;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import com.sqool.sqoolbus.tenant.repository.UserRepository;
import com.sqool.sqoolbus.tenant.repository.RoleRepository;
import com.sqool.sqoolbus.tenant.repository.SchoolRepository;
import com.sqool.sqoolbus.tenant.repository.UserProfileRepository;
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
    private RoleRepository roleRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private ActivityTrackingService activityTrackingService;
    
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
            Optional<User> userOptional = userRepository.findActiveByUsernameOrEmail(loginRequest.getUsername());
            
            if (userOptional.isEmpty()) {
                throw new AuthenticationException("Invalid credentials");
            }
            
            User user = userOptional.get();
            
            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new AuthenticationException("Invalid credentials");
            }
            
            // Extract roles and permissions
            Set<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            
            Set<String> permissions = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(Permission::getName)
                    .collect(Collectors.toSet());
            
            // Get school ID from user profile
            Long schoolId = null;
            if (user.getProfile() != null && user.getProfile().getSchool() != null) {
                schoolId = user.getProfile().getSchool().getId();
            }
            
            // Generate JWT token
            String token = tokenProvider.generateToken(
                    user.getUsername(),
                    TenantContext.getTenantId(),
                    roles,
                    permissions,
                    schoolId
            );
            
            // Start activity tracking for this session
            String sessionKey = activityTrackingService.createSessionKey(user.getUsername(), TenantContext.getTenantId());
            activityTrackingService.updateActivity(sessionKey);
            logger.debug("Started activity tracking for user: {} in tenant: {}", user.getUsername(), TenantContext.getTenantId());
            
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
                throw new DuplicateResourceException("User", "username", registerRequest.getUsername());
            }
            
            // Check if email already exists
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                throw new DuplicateResourceException("User", "email", registerRequest.getEmail());
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
                    Set.of(),
                    null  // No school ID for newly registered users
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
    
    public ParentSignupResponse registerParent(ParentSignupRequest request, String tenantId) {
        try {
            // Set tenant context
            if (tenantId != null && !tenantId.isEmpty()) {
                TenantContext.setTenantId(tenantId);
            } else {
                TenantContext.setTenantId(defaultTenant);
            }
            
            // Validate school exists
            Optional<School> schoolOptional = schoolRepository.findById(request.getSchoolId());
            if (schoolOptional.isEmpty()) {
                throw new ResourceNotFoundException("School", "id", String.valueOf(request.getSchoolId()));
            }
            School school = schoolOptional.get();
            
            // Check if username already exists
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new DuplicateResourceException("User", "username", request.getUsername());
            }
            
            // Check if email already exists
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new DuplicateResourceException("User", "email", request.getEmail());
            }
            
            // Get PARENT role
            Optional<Role> parentRoleOptional = roleRepository.findByName("PARENT");
            if (parentRoleOptional.isEmpty()) {
                throw new ResourceNotFoundException("Role", "name", "PARENT");
            }
            Role parentRole = parentRoleOptional.get();
            
            // Create new user
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setIsActive(true);
            user.setIsEmailVerified(false); // Email verification required
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            // Assign PARENT role
            user.getRoles().add(parentRole);
            
            // Save user first
            User savedUser = userRepository.save(user);
            
            // Create user profile with parent-specific information
            UserProfile profile = new UserProfile();
            profile.setUser(savedUser);
            profile.setSchool(school);
            profile.setPhoneNumber(request.getPhoneNumber());
            profile.setEmergencyContact(request.getEmergencyContact());
            profile.setEmergencyContactName(request.getEmergencyContactName());
            profile.setAddress(request.getAddress());
            profile.setCity(request.getCity());
            profile.setState(request.getState());
            profile.setZipCode(request.getZipCode());
            profile.setOccupation(request.getOccupation());
            profile.setWorkplace(request.getWorkplace());
            profile.setWorkPhone(request.getWorkPhone());
            profile.setPreferredContactMethod(request.getPreferredContactMethod());
            
            // Save profile
            UserProfile savedProfile = userProfileRepository.save(profile);
            
            // Update user with profile reference
            savedUser.setProfile(savedProfile);
            savedUser = userRepository.save(savedUser);
            
            logger.info("Parent user registered successfully: {} for school: {}", 
                       savedUser.getUsername(), school.getName());
            
            // Extract roles and permissions
            Set<String> roles = savedUser.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            
            Set<String> permissions = savedUser.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(Permission::getName)
                    .collect(Collectors.toSet());
            
            // Generate JWT token
            String token = tokenProvider.generateToken(
                    savedUser.getUsername(),
                    TenantContext.getTenantId(),
                    roles,
                    permissions,
                    school.getId()
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
            
            ParentSignupResponse.ParentProfileInfo profileInfo = new ParentSignupResponse.ParentProfileInfo(
                    savedProfile.getId(),
                    savedProfile.getPhoneNumber(),
                    savedProfile.getEmergencyContact(),
                    savedProfile.getEmergencyContactName(),
                    savedProfile.getAddress(),
                    savedProfile.getCity(),
                    savedProfile.getState(),
                    savedProfile.getZipCode(),
                    savedProfile.getOccupation(),
                    savedProfile.getWorkplace(),
                    savedProfile.getWorkPhone(),
                    savedProfile.getPreferredContactMethod()
            );
            
            ParentSignupResponse.SchoolInfo schoolInfo = new ParentSignupResponse.SchoolInfo(
                    school.getId(),
                    school.getName(),
                    school.getCode(),
                    school.getAddress(),
                    school.getPhoneNumber(),
                    school.getEmail()
            );
            
            return new ParentSignupResponse(
                    token,
                    tokenProvider.getExpirationTimeInSeconds(),
                    tokenProvider.getExpirationLocalDateTimeFromToken(token),
                    userInfo,
                    profileInfo,
                    schoolInfo
            );
            
        } finally {
            TenantContext.clear();
        }
    }
}
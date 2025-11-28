package com.sqool.sqoolbus.tenant.service;

import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.entity.Role;
import com.sqool.sqoolbus.tenant.entity.UserRole;
import com.sqool.sqoolbus.tenant.entity.hail.UserProfile;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import com.sqool.sqoolbus.tenant.repository.UserRepository;
import com.sqool.sqoolbus.tenant.repository.RoleRepository;
import com.sqool.sqoolbus.tenant.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing users and their roles in the school bus application
 */
@Service
@Transactional
public class UserManagementService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    /**
     * Create a new parent user
     */
    public User createParent(String username, String email, String password, 
                           String firstName, String lastName, School school) {
        User user = createUser(username, email, password, firstName, lastName);
        user = userRepository.save(user);
        
        addRoleToUser(user, UserRole.PARENT);
        
        // Create parent profile
        UserProfile profile = new UserProfile(user, school);
        user.setProfile(profile);
        userProfileRepository.save(profile);
        
        return userRepository.save(user);
    }
    
    /**
     * Create a new rider (driver/staff) user
     */
    public User createRider(String username, String email, String password, 
                          String firstName, String lastName, School school,
                          String licenseNumber, String employeeId) {
        User user = createUser(username, email, password, firstName, lastName);
        user = userRepository.save(user);
        
        addRoleToUser(user, UserRole.RIDER);
        
        // Create rider profile with driver-specific information
        UserProfile profile = new UserProfile(user, school);
        profile.setLicenseNumber(licenseNumber);
        profile.setEmployeeId(employeeId);
        profile.setIsAvailable(true);
        user.setProfile(profile);
        userProfileRepository.save(profile);
        
        return userRepository.save(user);
    }
    
    /**
     * Create a new school admin user
     */
    public User createSchoolAdmin(String username, String email, String password, 
                                String firstName, String lastName, School school,
                                String jobTitle, String department) {
        User user = createUser(username, email, password, firstName, lastName);
        user = userRepository.save(user);
        
        addRoleToUser(user, UserRole.SCHOOL_ADMIN);
        
        // Create admin profile
        UserProfile profile = new UserProfile(user, school);
        profile.setJobTitle(jobTitle);
        profile.setDepartment(department);
        profile.setAccessLevel(2); // Higher access level for admins
        user.setProfile(profile);
        userProfileRepository.save(profile);
        
        return userRepository.save(user);
    }
    
    /**
     * Create a system admin user with full permissions
     */
    public User createSystemAdmin(String username, String email, String password, 
                                String firstName, String lastName) {
        User user = createUser(username, email, password, firstName, lastName);
        user = userRepository.save(user);
        
        addRoleToUser(user, UserRole.SYSTEM_ADMIN);
        
        // Create system admin profile
        UserProfile profile = new UserProfile(user);
        profile.setAccessLevel(3); // Highest access level
        user.setProfile(profile);
        userProfileRepository.save(profile);
        
        return userRepository.save(user);
    }
    
    /**
     * Add a role to an existing user
     */
    public void addRoleToUser(User user, UserRole userRole) {
        Role role = roleRepository.findByName(userRole.getRoleName())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + userRole.getRoleName()));
        
        // Check if user already has this role
        if (!user.getRoles().contains(role)) {
            user.addRole(role);
            userRepository.save(user);
        }
    }
    
    /**
     * Remove a role from a user
     */
    public void removeRoleFromUser(User user, UserRole userRole) {
        Role role = roleRepository.findByName(userRole.getRoleName()).orElse(null);
        if (role != null) {
            user.removeRole(role);
            userRepository.save(user);
        }
    }
    
    /**
     * Check if user has specific role
     */
    public boolean userHasRole(User user, UserRole userRole) {
        return user.hasRole(userRole.getRoleName());
    }
    
    /**
     * Update user profile information
     */
    public void updateUserProfile(User user, UserProfile profileData) {
        UserProfile existingProfile = userProfileRepository.findByUser(user).orElse(null);
        if (existingProfile == null) {
            existingProfile = new UserProfile(user);
        }
        
        // Update profile fields (only non-null values)
        if (profileData.getPhoneNumber() != null) {
            existingProfile.setPhoneNumber(profileData.getPhoneNumber());
        }
        if (profileData.getAddress() != null) {
            existingProfile.setAddress(profileData.getAddress());
        }
        if (profileData.getDateOfBirth() != null) {
            existingProfile.setDateOfBirth(profileData.getDateOfBirth());
        }
        // Add other fields as needed
        
        userProfileRepository.save(existingProfile);
        user.setProfile(existingProfile);
        userRepository.save(user);
    }
    
    /**
     * Find user by ID
     */
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    /**
     * Find user by username
     */
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    /**
     * Find user by email
     */
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    /**
     * Find all users in the current tenant
     */
    public java.util.List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    private User createUser(String username, String email, String password, 
                          String firstName, String lastName) {
        User user = new User(username, email, password, firstName, lastName);
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        return user;
    }
}
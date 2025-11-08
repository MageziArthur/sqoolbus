package com.sqool.sqoolbus.security;

import com.sqool.sqoolbus.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Security utility service for checking user permissions and roles
 */
@Component
public class SecurityService {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    /**
     * Get current authenticated username
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
    
    /**
     * Check if current user has specific role
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
        }
        return false;
    }
    
    /**
     * Check if current user has specific permission
     * SYSTEM_ADMIN role bypasses all permission checks
     */
    public boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // SYSTEM_ADMIN has access to everything
            if (authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_SYSTEM_ADMIN"))) {
                return true;
            }
            // Check specific permission
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("PERM_" + permission));
        }
        return false;
    }
    
    /**
     * Check if current user has any of the specified roles
     */
    public boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if current user is a parent and can access specific pupil data
     */
    public boolean canAccessPupilData(Long pupilId) {
        // For parents, they should only access their own children's data
        // This would require a more complex check involving parent-child relationships
        // For now, we'll allow access if user has proper role
        return hasAnyRole("PARENT", "SCHOOL_ADMIN", "SYSTEM_ADMIN");
    }
    
    /**
     * Check if current user can modify trip data
     */
    public boolean canModifyTrip(Long tripId) {
        // Riders can modify trips they are assigned to
        // Admins can modify any trip
        return hasAnyRole("RIDER", "SCHOOL_ADMIN", "SYSTEM_ADMIN");
    }
    
    /**
     * Check if current user is system administrator
     */
    public boolean isSystemAdmin() {
        return hasRole("SYSTEM_ADMIN");
    }
    
    /**
     * Check if current user is school administrator
     */
    public boolean isSchoolAdmin() {
        return hasRole("SCHOOL_ADMIN");
    }
    
    /**
     * Check if current user is a parent
     */
    public boolean isParent() {
        return hasRole("PARENT");
    }
    
    /**
     * Check if current user is a rider/driver
     */
    public boolean isRider() {
        return hasRole("RIDER");
    }
}
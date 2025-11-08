package com.sqool.sqoolbus.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class to access current user's school context from security context
 */
public class SecurityUtils {
    
    /**
     * Get the current user's school ID from the security context
     * @return school ID or null if not available or user is not authenticated
     */
    public static Long getCurrentUserSchoolId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof SchoolAwareAuthentication) {
            return ((SchoolAwareAuthentication) authentication).getSchoolId();
        }
        
        return null;
    }
    
    /**
     * Check if the current user has SYSTEM_ADMIN role
     * @return true if user has SYSTEM_ADMIN role, false otherwise
     */
    public static boolean isSystemAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_SYSTEM_ADMIN".equals(authority.getAuthority()));
        }
        
        return false;
    }
    
    /**
     * Check if the current user has ADMIN role (regular admin)
     * @return true if user has ADMIN role, false otherwise
     */
    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        }
        
        return false;
    }
    
    /**
     * Check if the current user has either SYSTEM_ADMIN or ADMIN role (any admin privileges)
     * @return true if user has any admin role, false otherwise
     */
    public static boolean isAnyAdmin() {
        return isSystemAdmin() || isAdmin();
    }
    
    /**
     * Get the current authenticated username
     * @return username or null if not authenticated
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        
        return null;
    }
}
package com.sqool.sqoolbus.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for working with permissions in the application
 */
public class PermissionUtils {

    /**
     * Check if the current user has a specific permission
     */
    public static boolean hasPermission(Permission permission) {
        return hasAnyPermission(permission);
    }

    /**
     * Check if the current user has any of the specified permissions
     */
    public static boolean hasAnyPermission(Permission... permissions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // System admin has all permissions
        Set<String> userAuthorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        
        if (userAuthorities.contains("ROLE_SYSTEM_ADMIN")) {
            return true;
        }

        // Check for specific permissions
        for (Permission permission : permissions) {
            if (userAuthorities.contains(permission.getAuthority())) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Check if the current user has all of the specified permissions
     */
    public static boolean hasAllPermissions(Permission... permissions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // System admin has all permissions
        Set<String> userAuthorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        
        if (userAuthorities.contains("ROLE_SYSTEM_ADMIN")) {
            return true;
        }

        // Check that user has all specified permissions
        for (Permission permission : permissions) {
            if (!userAuthorities.contains(permission.getAuthority())) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Get all permissions for the current user
     */
    public static Set<String> getCurrentUserPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Set.of();
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    /**
     * Check if the current user is a system administrator
     */
    public static boolean isSystemAdmin() {
        return getCurrentUserPermissions().contains("ROLE_SYSTEM_ADMIN");
    }
}
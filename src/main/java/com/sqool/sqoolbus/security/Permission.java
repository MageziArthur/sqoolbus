package com.sqool.sqoolbus.security;

/**
 * Enum defining all available permissions in the system
 */
public enum Permission {
    // User management permissions
    PERM_VIEW_USERS,
    PERM_CREATE_USERS,
    PERM_UPDATE_USERS,
    PERM_DELETE_USERS,
    PERM_MANAGE_USER_ROLES,
    
    // School management permissions
    PERM_VIEW_SCHOOLS,
    PERM_CREATE_SCHOOLS,
    PERM_UPDATE_SCHOOLS,
    PERM_DELETE_SCHOOLS,
    
    // Pupil management permissions
    PERM_VIEW_PUPILS,
    PERM_CREATE_PUPILS,
    PERM_UPDATE_PUPILS,
    PERM_DELETE_PUPILS,
    PERM_ASSIGN_PUPILS,
    
    // Route management permissions
    PERM_VIEW_ROUTES,
    PERM_CREATE_ROUTES,
    PERM_UPDATE_ROUTES,
    PERM_DELETE_ROUTES,
    PERM_MANAGE_ROUTES,
    PERM_ASSIGN_ROUTES,
    
    // Trip management permissions
    PERM_VIEW_TRIPS,
    PERM_CREATE_TRIPS,
    PERM_UPDATE_TRIPS,
    PERM_DELETE_TRIPS,
    PERM_MANAGE_TRIPS,
    PERM_START_TRIPS,
    PERM_END_TRIPS,
    PERM_COMPLETE_TRIPS,
    PERM_CANCEL_TRIPS,
    
    // System administration permissions
    PERM_SYSTEM_ADMIN,
    PERM_CREATE_SCHOOL_ADMINS,
    PERM_CREATE_SYSTEM_ADMINS,
    PERM_VIEW_SYSTEM_LOGS,
    PERM_MANAGE_TENANTS,
    
    // Reports and analytics permissions
    PERM_VIEW_REPORTS,
    PERM_GENERATE_REPORTS,
    PERM_EXPORT_DATA;
    
    /**
     * Get the string representation of the permission for use in Spring Security
     */
    public String getAuthority() {
        return this.name();
    }
}
package com.sqool.sqoolbus.tenant.entity;

/**
 * Standard permissions for the school bus application
 */
public enum UserPermission {
    // User management
    MANAGE_USERS("MANAGE_USERS", "Create, update, and delete users"),
    VIEW_USERS("VIEW_USERS", "View user information"),
    
    // School management
    MANAGE_SCHOOLS("MANAGE_SCHOOLS", "Create, update, and delete schools"),
    VIEW_SCHOOLS("VIEW_SCHOOLS", "View school information"),
    
    // Pupil management
    MANAGE_PUPILS("MANAGE_PUPILS", "Create, update, and delete pupils"),
    VIEW_PUPILS("VIEW_PUPILS", "View pupil information"),
    MANAGE_OWN_CHILDREN("MANAGE_OWN_CHILDREN", "Manage own children information"),
    VIEW_OWN_CHILDREN("VIEW_OWN_CHILDREN", "View own children information"),
    
    // Route management
    MANAGE_ROUTES("MANAGE_ROUTES", "Create, update, and delete routes"),
    VIEW_ROUTES("VIEW_ROUTES", "View route information"),
    ASSIGN_ROUTES("ASSIGN_ROUTES", "Assign routes to riders"),
    
    // Trip management
    START_TRIPS("START_TRIPS", "Start trips for assigned routes"),
    END_TRIPS("END_TRIPS", "End trips for assigned routes"),
    MANAGE_TRIPS("MANAGE_TRIPS", "Full trip management"),
    VIEW_TRIPS("VIEW_TRIPS", "View trip information"),
    VIEW_TRIP_HISTORY("VIEW_TRIP_HISTORY", "View historical trip data"),
    
    // Bus management
    MANAGE_BUSES("MANAGE_BUSES", "Create, update, and delete buses"),
    VIEW_BUSES("VIEW_BUSES", "View bus information"),
    
    // Parent-specific permissions
    TRACK_CHILD_BUS("TRACK_CHILD_BUS", "Track child's bus location in real-time"),
    RECEIVE_NOTIFICATIONS("RECEIVE_NOTIFICATIONS", "Receive notifications about child's transportation"),
    VIEW_CHILD_SCHEDULE("VIEW_CHILD_SCHEDULE", "View child's transportation schedule"),
    
    // Rider-specific permissions
    UPDATE_TRIP_STATUS("UPDATE_TRIP_STATUS", "Update trip status and location"),
    MARK_PUPIL_PICKUP("MARK_PUPIL_PICKUP", "Mark pupils as picked up or dropped off"),
    VIEW_ASSIGNED_ROUTES("VIEW_ASSIGNED_ROUTES", "View assigned routes and schedules"),
    EMERGENCY_CONTACT("EMERGENCY_CONTACT", "Contact emergency services"),
    
    // Admin-specific permissions
    MANAGE_ROLES("MANAGE_ROLES", "Create and manage user roles"),
    MANAGE_PERMISSIONS("MANAGE_PERMISSIONS", "Assign permissions to roles"),
    VIEW_REPORTS("VIEW_REPORTS", "Access system reports and analytics"),
    MANAGE_SETTINGS("MANAGE_SETTINGS", "Manage system settings"),
    AUDIT_LOGS("AUDIT_LOGS", "View audit logs"),
    
    // System permissions
    SYSTEM_ADMIN("SYSTEM_ADMIN", "Full system administration access");
    
    private final String permissionName;
    private final String description;
    
    UserPermission(String permissionName, String description) {
        this.permissionName = permissionName;
        this.description = description;
    }
    
    public String getPermissionName() {
        return permissionName;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return permissionName;
    }
}
package com.sqool.sqoolbus.tenant.entity;

/**
 * Standard user roles for the school bus application
 */
public enum UserRole {
    PARENT("PARENT", "Parent or guardian of pupils"),
    RIDER("RIDER", "Bus driver or staff member"),
    SCHOOL_ADMIN("SCHOOL_ADMIN", "School administrator"),
    SYSTEM_ADMIN("SYSTEM_ADMIN", "System administrator"),
    STUDENT("STUDENT", "Student user");
    
    private final String roleName;
    private final String description;
    
    UserRole(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return roleName;
    }
}
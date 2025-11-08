package com.sqool.sqoolbus.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify required permissions for accessing an API endpoint.
 * Can be applied to both controller classes and individual methods.
 * Method-level annotations override class-level annotations.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermissions {
    
    /**
     * Array of permissions required to access the endpoint.
     * User must have at least one of the specified permissions (OR logic).
     * 
     * @return array of required permissions
     */
    Permission[] value();
    
    /**
     * If true, user must have ALL specified permissions (AND logic).
     * If false, user needs only ONE of the specified permissions (OR logic).
     * Default is false (OR logic).
     * 
     * @return whether all permissions are required
     */
    boolean requireAll() default false;
    
    /**
     * Optional description of what these permissions grant access to.
     * Used for documentation purposes.
     * 
     * @return description of the permissions
     */
    String description() default "";
}
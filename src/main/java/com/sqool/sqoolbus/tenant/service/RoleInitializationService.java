package com.sqool.sqoolbus.tenant.service;

import com.sqool.sqoolbus.tenant.entity.Role;
import com.sqool.sqoolbus.tenant.entity.Permission;
import com.sqool.sqoolbus.tenant.entity.UserRole;
import com.sqool.sqoolbus.tenant.entity.UserPermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Set;
import java.util.HashSet;

/**
 * Service to initialize standard roles and permissions for the school bus application
 */
@Service
@Transactional
public class RoleInitializationService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Initialize all standard roles and permissions
     */
    public void initializeRolesAndPermissions() {
        // Create permissions first
        createPermissions();
        
        // Create roles with their assigned permissions
        createParentRole();
        createRiderRole();
        createSchoolAdminRole();
        createSystemAdminRole();
    }
    
    private void createPermissions() {
        for (UserPermission perm : UserPermission.values()) {
            Permission permission = findOrCreatePermission(perm.getPermissionName());
            permission.setDescription(perm.getDescription());
            entityManager.merge(permission);
        }
    }
    
    private void createParentRole() {
        Role parentRole = findOrCreateRole(UserRole.PARENT.getRoleName());
        parentRole.setDescription(UserRole.PARENT.getDescription());
        
        Set<Permission> parentPermissions = new HashSet<>();
        parentPermissions.add(findOrCreatePermission(UserPermission.VIEW_OWN_CHILDREN.getPermissionName()));
        parentPermissions.add(findOrCreatePermission(UserPermission.MANAGE_OWN_CHILDREN.getPermissionName()));
        parentPermissions.add(findOrCreatePermission(UserPermission.TRACK_CHILD_BUS.getPermissionName()));
        parentPermissions.add(findOrCreatePermission(UserPermission.RECEIVE_NOTIFICATIONS.getPermissionName()));
        parentPermissions.add(findOrCreatePermission(UserPermission.VIEW_CHILD_SCHEDULE.getPermissionName()));
        
        parentRole.setPermissions(parentPermissions);
        entityManager.merge(parentRole);
    }
    
    private void createRiderRole() {
        Role riderRole = findOrCreateRole(UserRole.RIDER.getRoleName());
        riderRole.setDescription(UserRole.RIDER.getDescription());
        
        Set<Permission> riderPermissions = new HashSet<>();
        riderPermissions.add(findOrCreatePermission(UserPermission.START_TRIPS.getPermissionName()));
        riderPermissions.add(findOrCreatePermission(UserPermission.END_TRIPS.getPermissionName()));
        riderPermissions.add(findOrCreatePermission(UserPermission.UPDATE_TRIP_STATUS.getPermissionName()));
        riderPermissions.add(findOrCreatePermission(UserPermission.MARK_PUPIL_PICKUP.getPermissionName()));
        riderPermissions.add(findOrCreatePermission(UserPermission.VIEW_ASSIGNED_ROUTES.getPermissionName()));
        riderPermissions.add(findOrCreatePermission(UserPermission.EMERGENCY_CONTACT.getPermissionName()));
        riderPermissions.add(findOrCreatePermission(UserPermission.VIEW_TRIPS.getPermissionName()));
        
        riderRole.setPermissions(riderPermissions);
        entityManager.merge(riderRole);
    }
    
    private void createSchoolAdminRole() {
        Role adminRole = findOrCreateRole(UserRole.SCHOOL_ADMIN.getRoleName());
        adminRole.setDescription(UserRole.SCHOOL_ADMIN.getDescription());
        
        Set<Permission> adminPermissions = new HashSet<>();
        adminPermissions.add(findOrCreatePermission(UserPermission.MANAGE_USERS.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.VIEW_USERS.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.MANAGE_SCHOOLS.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.VIEW_SCHOOLS.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.MANAGE_PUPILS.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.VIEW_PUPILS.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.MANAGE_ROUTES.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.VIEW_ROUTES.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.ASSIGN_ROUTES.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.MANAGE_TRIPS.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.VIEW_TRIPS.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.VIEW_TRIP_HISTORY.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.MANAGE_BUSES.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.VIEW_BUSES.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.VIEW_REPORTS.getPermissionName()));
        adminPermissions.add(findOrCreatePermission(UserPermission.MANAGE_SETTINGS.getPermissionName()));
        
        adminRole.setPermissions(adminPermissions);
        entityManager.merge(adminRole);
    }
    
    private void createSystemAdminRole() {
        Role systemAdminRole = findOrCreateRole(UserRole.SYSTEM_ADMIN.getRoleName());
        systemAdminRole.setDescription(UserRole.SYSTEM_ADMIN.getDescription());
        
        Set<Permission> systemAdminPermissions = new HashSet<>();
        systemAdminPermissions.add(findOrCreatePermission(UserPermission.SYSTEM_ADMIN.getPermissionName()));
        systemAdminPermissions.add(findOrCreatePermission(UserPermission.MANAGE_ROLES.getPermissionName()));
        systemAdminPermissions.add(findOrCreatePermission(UserPermission.MANAGE_PERMISSIONS.getPermissionName()));
        systemAdminPermissions.add(findOrCreatePermission(UserPermission.AUDIT_LOGS.getPermissionName()));
        
        // System admin gets all permissions
        for (UserPermission perm : UserPermission.values()) {
            systemAdminPermissions.add(findOrCreatePermission(perm.getPermissionName()));
        }
        
        systemAdminRole.setPermissions(systemAdminPermissions);
        entityManager.merge(systemAdminRole);
    }
    
    private Role findOrCreateRole(String roleName) {
        try {
            return entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", roleName)
                    .getSingleResult();
        } catch (Exception e) {
            Role role = new Role(roleName, "");
            entityManager.persist(role);
            return role;
        }
    }
    
    private Permission findOrCreatePermission(String permissionName) {
        try {
            return entityManager.createQuery("SELECT p FROM Permission p WHERE p.name = :name", Permission.class)
                    .setParameter("name", permissionName)
                    .getSingleResult();
        } catch (Exception e) {
            Permission permission = new Permission(permissionName, "", "", "");
            entityManager.persist(permission);
            return permission;
        }
    }
}
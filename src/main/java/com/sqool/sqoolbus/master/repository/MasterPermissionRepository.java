package com.sqool.sqoolbus.master.repository;

import com.sqool.sqoolbus.master.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterPermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByName(String name);
    
    List<Permission> findByResource(String resource);
    
    List<Permission> findByAction(String action);
    
    List<Permission> findByResourceAndAction(String resource, String action);
    
    List<Permission> findByIsActiveTrue();
    
    boolean existsByName(String name);
    
    @Query("SELECT p FROM Permission p JOIN p.rolePermissions rp WHERE rp.role.name = :roleName")
    List<Permission> findPermissionsByRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT p FROM Permission p JOIN p.rolePermissions rp JOIN rp.role.userRoles ur WHERE ur.user.id = :userId")
    List<Permission> findPermissionsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT p FROM Permission p JOIN p.rolePermissions rp JOIN rp.role.userRoles ur WHERE ur.user.id = :userId AND ur.tenant.id = :tenantId")
    List<Permission> findPermissionsByUserIdAndTenantId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
}
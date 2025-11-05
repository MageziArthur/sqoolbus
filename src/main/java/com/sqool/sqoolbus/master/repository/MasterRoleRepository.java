package com.sqool.sqoolbus.master.repository;

import com.sqool.sqoolbus.master.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterRoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
    
    List<Role> findByIsActiveTrue();
    
    boolean existsByName(String name);
    
    @Query("SELECT r FROM Role r JOIN r.rolePermissions rp WHERE rp.permission.name = :permissionName")
    List<Role> findRolesByPermissionName(@Param("permissionName") String permissionName);
    
    @Query("SELECT r FROM Role r JOIN r.userRoles ur WHERE ur.user.id = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);
    
    @Query("SELECT r FROM Role r JOIN r.userRoles ur WHERE ur.user.id = :userId AND ur.tenant.id = :tenantId")
    List<Role> findRolesByUserIdAndTenantId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
}
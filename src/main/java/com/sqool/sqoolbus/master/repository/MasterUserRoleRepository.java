package com.sqool.sqoolbus.master.repository;

import com.sqool.sqoolbus.master.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterUserRoleRepository extends JpaRepository<UserRole, Long> {
    
    List<UserRole> findByUserId(Long userId);
    
    List<UserRole> findByRoleId(Long roleId);
    
    List<UserRole> findByTenantId(Long tenantId);
    
    List<UserRole> findByUserIdAndTenantId(Long userId, Long tenantId);
    
    Optional<UserRole> findByUserIdAndRoleIdAndTenantId(Long userId, Long roleId, Long tenantId);
    
    Optional<UserRole> findByUserIdAndRoleIdAndTenantIsNull(Long userId, Long roleId);
    
    boolean existsByUserIdAndRoleIdAndTenantId(Long userId, Long roleId, Long tenantId);
    
    boolean existsByUserIdAndRoleIdAndTenantIsNull(Long userId, Long roleId);
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.user.username = :username")
    List<UserRole> findByUsername(@Param("username") String username);
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.user.username = :username AND ur.tenant.tenantId = :tenantId")
    List<UserRole> findByUsernameAndTenantId(@Param("username") String username, @Param("tenantId") String tenantId);
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.role.name = :roleName")
    List<UserRole> findByRoleName(@Param("roleName") String roleName);
    
    void deleteByUserIdAndRoleIdAndTenantId(Long userId, Long roleId, Long tenantId);
    
    void deleteByUserIdAndRoleIdAndTenantIsNull(Long userId, Long roleId);
}
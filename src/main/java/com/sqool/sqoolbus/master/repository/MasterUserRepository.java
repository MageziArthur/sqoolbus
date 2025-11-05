package com.sqool.sqoolbus.master.repository;

import com.sqool.sqoolbus.master.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterUserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true AND (u.username = :usernameOrEmail OR u.email = :usernameOrEmail)")
    Optional<User> findActiveUserByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
    
    @Query("SELECT u FROM User u JOIN u.userRoles ur WHERE ur.role.name = :roleName")
    java.util.List<User> findUsersByRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT u FROM User u JOIN u.userRoles ur WHERE ur.tenant.id = :tenantId")
    java.util.List<User> findUsersByTenantId(@Param("tenantId") Long tenantId);
    
    @Query("SELECT u FROM User u JOIN u.userRoles ur WHERE ur.tenant.tenantId = :tenantId")
    java.util.List<User> findUsersByTenantIdentifier(@Param("tenantId") String tenantId);
}
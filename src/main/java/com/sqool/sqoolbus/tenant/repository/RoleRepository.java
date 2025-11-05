package com.sqool.sqoolbus.tenant.repository;

import com.sqool.sqoolbus.tenant.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
    
    @Query("SELECT r FROM Role r WHERE r.isSystemRole = true")
    List<Role> findAllSystemRoles();
    
    @Query("SELECT r FROM Role r WHERE r.isSystemRole = false")
    List<Role> findAllCustomRoles();
    
    boolean existsByName(String name);
}
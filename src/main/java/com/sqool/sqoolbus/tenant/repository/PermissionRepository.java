package com.sqool.sqoolbus.tenant.repository;

import com.sqool.sqoolbus.tenant.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByName(String name);
    
    @Query("SELECT p FROM Permission p WHERE p.resource = :resource")
    List<Permission> findByResource(@Param("resource") String resource);
    
    @Query("SELECT p FROM Permission p WHERE p.action = :action")
    List<Permission> findByAction(@Param("action") String action);
    
    @Query("SELECT p FROM Permission p WHERE p.resource = :resource AND p.action = :action")
    Optional<Permission> findByResourceAndAction(@Param("resource") String resource, @Param("action") String action);
    
    boolean existsByName(String name);
}
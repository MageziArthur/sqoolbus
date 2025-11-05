package com.sqool.sqoolbus.master.repository;

import com.sqool.sqoolbus.master.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    Optional<Tenant> findByTenantId(String tenantId);
    
    @Query("SELECT t FROM Tenant t WHERE t.isActive = true")
    List<Tenant> findAllActiveTenants();
    
    @Query("SELECT t FROM Tenant t WHERE t.tenantId = :tenantId AND t.isActive = true")
    Optional<Tenant> findActiveTenantByTenantId(@Param("tenantId") String tenantId);
    
    boolean existsByTenantId(String tenantId);
    
    @Query("SELECT t.tenantId FROM Tenant t WHERE t.isActive = true")
    List<String> findAllActiveTenantIds();
}
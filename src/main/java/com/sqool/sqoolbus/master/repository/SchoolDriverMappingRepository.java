package com.sqool.sqoolbus.master.repository;

import com.sqool.sqoolbus.master.entity.SchoolDriverMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolDriverMappingRepository extends JpaRepository<SchoolDriverMapping, Long> {

    /**
     * Find all active driver mappings for a school by tenant ID and school ID
     */
    @Query("SELECT m FROM SchoolDriverMapping m JOIN FETCH m.driver " +
           "WHERE m.tenant.tenantId = :tenantId AND m.schoolId = :schoolId AND m.isActive = true")
    List<SchoolDriverMapping> findAllActiveByTenantIdAndSchoolId(
        @Param("tenantId") String tenantId, 
        @Param("schoolId") Long schoolId
    );

    /**
     * Find all driver mappings for a school (including inactive)
     */
    @Query("SELECT m FROM SchoolDriverMapping m " +
           "WHERE m.tenant.tenantId = :tenantId AND m.schoolId = :schoolId")
    List<SchoolDriverMapping> findAllByTenantIdAndSchoolId(
        @Param("tenantId") String tenantId, 
        @Param("schoolId") Long schoolId
    );

    /**
     * Find a specific active driver mapping for a school
     */
    @Query("SELECT m FROM SchoolDriverMapping m " +
           "WHERE m.tenant.tenantId = :tenantId AND m.schoolId = :schoolId " +
           "AND m.driver.id = :driverId AND m.isActive = true")
    Optional<SchoolDriverMapping> findActiveByTenantIdAndSchoolIdAndDriverId(
        @Param("tenantId") String tenantId,
        @Param("schoolId") Long schoolId,
        @Param("driverId") Long driverId
    );

    /**
     * Find a specific driver mapping for a school (active or inactive)
     */
    @Query("SELECT m FROM SchoolDriverMapping m " +
           "WHERE m.tenant.tenantId = :tenantId AND m.schoolId = :schoolId " +
           "AND m.driver.id = :driverId")
    Optional<SchoolDriverMapping> findByTenantIdAndSchoolIdAndDriverId(
        @Param("tenantId") String tenantId,
        @Param("schoolId") Long schoolId,
        @Param("driverId") Long driverId
    );

    /**
     * Find all active schools assigned to a driver
     */
    @Query("SELECT m FROM SchoolDriverMapping m " +
           "WHERE m.driver.id = :driverId AND m.isActive = true")
    List<SchoolDriverMapping> findAllActiveByDriverId(@Param("driverId") Long driverId);

    /**
     * Find all active schools assigned to a driver within a specific tenant
     */
    @Query("SELECT m FROM SchoolDriverMapping m " +
           "WHERE m.driver.id = :driverId AND m.tenant.tenantId = :tenantId AND m.isActive = true")
    List<SchoolDriverMapping> findAllActiveByDriverIdAndTenantId(
        @Param("driverId") Long driverId,
        @Param("tenantId") String tenantId
    );

    /**
     * Check if a driver is already assigned to a school
     */
    @Query("SELECT COUNT(m) > 0 FROM SchoolDriverMapping m " +
           "WHERE m.tenant.tenantId = :tenantId AND m.schoolId = :schoolId " +
           "AND m.driver.id = :driverId AND m.isActive = true")
    boolean existsByTenantIdAndSchoolIdAndDriverId(
        @Param("tenantId") String tenantId,
        @Param("schoolId") Long schoolId,
        @Param("driverId") Long driverId
    );
}

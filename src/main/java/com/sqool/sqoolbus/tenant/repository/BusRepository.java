package com.sqool.sqoolbus.tenant.repository;

import com.sqool.sqoolbus.tenant.entity.hail.Bus;
import com.sqool.sqoolbus.tenant.entity.hail.BusStatus;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    
    Optional<Bus> findByBusNumber(String busNumber);
    
    Optional<Bus> findByLicensePlate(String licensePlate);
    
    Optional<Bus> findByVin(String vin);
    
    List<Bus> findBySchool(School school);
    
    List<Bus> findBySchoolId(Long schoolId);
    
    List<Bus> findByStatus(BusStatus status);
    
    List<Bus> findBySchoolIdAndStatus(Long schoolId, BusStatus status);
    
    @Query("SELECT b FROM Bus b WHERE b.school.id = :schoolId AND b.isActive = true")
    List<Bus> findActiveBySchool(@Param("schoolId") Long schoolId);
    
    @Query("SELECT b FROM Bus b WHERE b.assignedRoute.id = :routeId")
    List<Bus> findByAssignedRouteId(@Param("routeId") Long routeId);
    
    @Query("SELECT b FROM Bus b WHERE b.assignedDriver.id = :driverId")
    Optional<Bus> findByAssignedDriverId(@Param("driverId") Long driverId);
    
    @Query("SELECT b FROM Bus b WHERE b.school.id = :schoolId AND b.assignedRoute IS NULL AND b.status = 'AVAILABLE'")
    List<Bus> findAvailableBusesWithoutRoute(@Param("schoolId") Long schoolId);
    
    @Query("SELECT b FROM Bus b WHERE b.school.id = :schoolId AND b.assignedDriver IS NULL AND b.status = 'AVAILABLE'")
    List<Bus> findAvailableBusesWithoutDriver(@Param("schoolId") Long schoolId);
    
    @Query("SELECT COUNT(b) FROM Bus b WHERE b.school.id = :schoolId AND b.isActive = true")
    Long countActiveBySchool(@Param("schoolId") Long schoolId);
    
    @Query("SELECT COUNT(b) FROM Bus b WHERE b.school.id = :schoolId AND b.status = :status")
    Long countBySchoolAndStatus(@Param("schoolId") Long schoolId, @Param("status") BusStatus status);
}

package com.sqool.sqoolbus.tenant.repository;

import com.sqool.sqoolbus.tenant.entity.hail.Trip;
import com.sqool.sqoolbus.tenant.entity.hail.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    
    /**
     * Find trips by route
     */
    List<Trip> findByRoute(Route route);
    
    /**
     * Find trips by route ID
     */
    List<Trip> findByRouteId(Long routeId);
    
    /**
     * Find trips by status
     */
    List<Trip> findByStatus(String status);
    
    /**
     * Find active trips
     */
    @Query("SELECT t FROM Trip t WHERE t.status IN ('SCHEDULED', 'IN_PROGRESS', 'STARTED')")
    List<Trip> findActiveTrips();
    
    /**
     * Find completed trips
     */
    @Query("SELECT t FROM Trip t WHERE t.status = 'COMPLETED'")
    List<Trip> findCompletedTrips();
    
    /**
     * Find trips for today
     */
    @Query("SELECT t FROM Trip t WHERE DATE(t.plannedStartTime) = CURRENT_DATE")
    List<Trip> findTodaysTrips();
    
    /**
     * Find trips by date range
     */
    @Query("SELECT t FROM Trip t WHERE t.plannedStartTime BETWEEN :startDate AND :endDate")
    List<Trip> findByScheduledStartTimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find trips by route and date
     */
    @Query("SELECT t FROM Trip t WHERE t.route = :route AND DATE(t.plannedStartTime) = :date")
    List<Trip> findByRouteAndDate(@Param("route") Route route, @Param("date") LocalDate date);
    
    /**
     * Find trips by rider ID
     */
    @Query("SELECT t FROM Trip t WHERE t.rider.id = :riderId")
    List<Trip> findByRiderId(@Param("riderId") Long riderId);
    
    /**
     * Find trips by rider ID and status
     */
    @Query("SELECT t FROM Trip t WHERE t.rider.id = :riderId AND t.status = :status")
    List<Trip> findByRiderIdAndStatus(@Param("riderId") Long riderId, @Param("status") String status);
    
    /**
     * Find ongoing trips by rider ID
     */
    @Query("SELECT t FROM Trip t WHERE t.rider.id = :riderId AND t.status IN ('STARTED', 'IN_PROGRESS')")
    List<Trip> findOngoingTripsByRiderId(@Param("riderId") Long riderId);
    
    /**
     * Find trips by school (through route)
     */
    @Query("SELECT t FROM Trip t WHERE t.route.school.id = :schoolId")
    List<Trip> findBySchoolId(@Param("schoolId") Long schoolId);
    
    /**
     * Find trips by school and status
     */
    @Query("SELECT t FROM Trip t WHERE t.route.school.id = :schoolId AND t.status = :status")
    List<Trip> findBySchoolIdAndStatus(@Param("schoolId") Long schoolId, @Param("status") String status);
    
    /**
     * Count trips by status
     */
    @Query("SELECT COUNT(t) FROM Trip t WHERE t.status = :status")
    Long countByStatus(@Param("status") String status);
    
    /**
     * Count today's trips by school
     */
    @Query("SELECT COUNT(t) FROM Trip t WHERE t.route.school.id = :schoolId AND DATE(t.plannedStartTime) = CURRENT_DATE")
    Long countTodaysTripsBySchoolId(@Param("schoolId") Long schoolId);
}
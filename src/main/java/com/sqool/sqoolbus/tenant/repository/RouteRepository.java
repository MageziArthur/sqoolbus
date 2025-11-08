package com.sqool.sqoolbus.tenant.repository;

import com.sqool.sqoolbus.tenant.entity.hail.Route;
import com.sqool.sqoolbus.tenant.entity.hail.Route.RouteStatus;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    /**
     * Find routes by school
     */
    List<Route> findBySchool(School school);
    
    /**
     * Find routes by school ID
     */
    List<Route> findBySchoolId(Long schoolId);
    
    /**
     * Find active routes
     */
    List<Route> findByStatus(RouteStatus status);
    
    /**
     * Find active routes by school
     */
    @Query("SELECT r FROM Route r WHERE r.school = :school AND r.status = 'ACTIVE'")
    List<Route> findActiveBySchool(@Param("school") School school);
    
    /**
     * Find active routes by school ID
     */
    @Query("SELECT r FROM Route r WHERE r.school.id = :schoolId AND r.status = 'ACTIVE'")
    List<Route> findActiveBySchoolId(@Param("schoolId") Long schoolId);
    
    /**
     * Find routes by type
     */
    List<Route> findByRouteType(String routeType);
    
    /**
     * Find routes by type and school
     */
    @Query("SELECT r FROM Route r WHERE r.routeType = :routeType AND r.school = :school")
    List<Route> findByRouteTypeAndSchool(@Param("routeType") String routeType, @Param("school") School school);
    
    /**
     * Find route by route name
     */
    Optional<Route> findByRouteName(String routeName);
    
    /**
     * Find routes by route name containing (case insensitive)
     */
    @Query("SELECT r FROM Route r WHERE LOWER(r.routeName) LIKE LOWER(CONCAT('%', :routeName, '%'))")
    List<Route> findByRouteNameContainingIgnoreCase(@Param("routeName") String routeName);
    
    /**
     * Check if route exists by route name and school
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Route r WHERE r.routeName = :routeName AND r.school = :school")
    boolean existsByRouteNameAndSchool(@Param("routeName") String routeName, @Param("school") School school);
    
    /**
     * Count active routes by school
     */
    @Query("SELECT COUNT(r) FROM Route r WHERE r.school = :school AND r.status = 'ACTIVE'")
    Long countActiveBySchool(@Param("school") School school);
    
    /**
     * Find available routes (not assigned to any driver)
     */
    @Query("SELECT r FROM Route r WHERE r.status = 'ACTIVE'")
    List<Route> findAvailableRoutes();
}
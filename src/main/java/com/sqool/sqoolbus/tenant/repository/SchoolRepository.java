package com.sqool.sqoolbus.tenant.repository;

import com.sqool.sqoolbus.tenant.entity.hail.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    
    /**
     * Find school by name
     */
    Optional<School> findByName(String name);
    
    /**
     * Find schools by active status
     */
    List<School> findByIsActiveTrue();
    
    /**
     * Find schools by type
     */
    List<School> findBySchoolType(String schoolType);
    
    /**
     * Find schools by city
     */
    List<School> findByCity(String city);
    
    /**
     * Check if school exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Find schools by name containing (case insensitive)
     */
    @Query("SELECT s FROM School s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<School> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find active schools in a specific region
     */
    @Query("SELECT s FROM School s WHERE s.isActive = true AND s.state = :state")
    List<School> findActiveByState(@Param("state") String state);
}
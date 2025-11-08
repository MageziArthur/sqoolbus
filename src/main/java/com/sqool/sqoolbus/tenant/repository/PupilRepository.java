package com.sqool.sqoolbus.tenant.repository;

import com.sqool.sqoolbus.tenant.entity.hail.Pupil;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PupilRepository extends JpaRepository<Pupil, Long> {
    
    /**
     * Find pupils by school
     */
    List<Pupil> findBySchool(School school);
    
    /**
     * Find pupils by school ID
     */
    List<Pupil> findBySchoolId(Long schoolId);
    
    /**
     * Find active pupils
     */
    List<Pupil> findByIsActiveTrue();
    
    /**
     * Find active pupils by school
     */
    @Query("SELECT p FROM Pupil p WHERE p.school = :school AND p.isActive = true")
    List<Pupil> findActiveBySchool(@Param("school") School school);
    
    /**
     * Find active pupils by school ID
     */
    @Query("SELECT p FROM Pupil p WHERE p.school.id = :schoolId AND p.isActive = true")
    List<Pupil> findActiveBySchoolId(@Param("schoolId") Long schoolId);
    
    /**
     * Find pupils by grade level
     */
    List<Pupil> findByGradeLevel(Integer gradeLevel);
    
    /**
     * Find pupils by grade level and school
     */
    @Query("SELECT p FROM Pupil p WHERE p.gradeLevel = :gradeLevel AND p.school = :school")
    List<Pupil> findByGradeLevelAndSchool(@Param("gradeLevel") Integer gradeLevel, @Param("school") School school);
    
    /**
     * Find pupils by parent ID
     */
    @Query("SELECT p FROM Pupil p WHERE p.parent.id = :parentId")
    List<Pupil> findByParentId(@Param("parentId") Long parentId);
    
    /**
     * Find pupils by parent (using Spring Data JPA naming convention)
     */
    List<Pupil> findByParent_Id(Long parentId);
    
    /**
     * Find pupils by age range
     */
    @Query("SELECT p FROM Pupil p WHERE p.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Pupil> findByDateOfBirthBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Find pupils by first and last name
     */
    @Query("SELECT p FROM Pupil p WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) AND LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Pupil> findByFirstNameAndLastNameContainingIgnoreCase(@Param("firstName") String firstName, @Param("lastName") String lastName);
    
    /**
     * Count active pupils by school
     */
    @Query("SELECT COUNT(p) FROM Pupil p WHERE p.school = :school AND p.isActive = true")
    Long countActiveBySchool(@Param("school") School school);
    
    /**
     * Check if pupil exists by student ID
     */
    boolean existsByStudentId(String studentId);
    
    /**
     * Find pupil by student ID
     */
    Optional<Pupil> findByStudentId(String studentId);
}
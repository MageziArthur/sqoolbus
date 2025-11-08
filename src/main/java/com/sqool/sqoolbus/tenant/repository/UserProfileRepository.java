package com.sqool.sqoolbus.tenant.repository;

import com.sqool.sqoolbus.tenant.entity.hail.UserProfile;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import com.sqool.sqoolbus.tenant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    /**
     * Find profile by user
     */
    Optional<UserProfile> findByUser(User user);
    
    /**
     * Find profile by user ID
     */
    Optional<UserProfile> findByUserId(Long userId);
    
    /**
     * Find profiles by school
     */
    List<UserProfile> findBySchool(School school);
    
    /**
     * Find profiles by school ID
     */
    List<UserProfile> findBySchoolId(Long schoolId);
    
    /**
     * Find profiles by phone number
     */
    Optional<UserProfile> findByPhoneNumber(String phoneNumber);
    
    /**
     * Find user profiles by user's first and last name (case insensitive)
     */
    @Query("SELECT up FROM UserProfile up WHERE LOWER(up.user.firstName) = LOWER(:firstName) AND LOWER(up.user.lastName) = LOWER(:lastName)")
    List<UserProfile> findByFirstNameAndLastNameIgnoreCase(@Param("firstName") String firstName, @Param("lastName") String lastName);
    
    /**
     * Find user profiles by user's full name containing (case insensitive)
     */
    @Query("SELECT up FROM UserProfile up WHERE LOWER(CONCAT(up.user.firstName, ' ', up.user.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<UserProfile> findByFullNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find profiles by email containing
     */
    @Query("SELECT up FROM UserProfile up WHERE LOWER(up.user.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    List<UserProfile> findByUserEmailContainingIgnoreCase(@Param("email") String email);
    
    /**
     * Check if profile exists for user
     */
    boolean existsByUser(User user);
    
    /**
     * Check if profile exists by user ID
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Check if phone number exists
     */
    boolean existsByPhoneNumber(String phoneNumber);
    
    /**
     * Find active profiles by school
     */
    @Query("SELECT up FROM UserProfile up WHERE up.school = :school AND up.user.isActive = true")
    List<UserProfile> findActiveBySchool(@Param("school") School school);
    
    /**
     * Count profiles by school
     */
    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.school = :school")
    Long countBySchool(@Param("school") School school);
}
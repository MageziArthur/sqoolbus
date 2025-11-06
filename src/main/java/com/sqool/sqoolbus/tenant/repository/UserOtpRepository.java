package com.sqool.sqoolbus.tenant.repository;

import com.sqool.sqoolbus.tenant.entity.UserOtp;
import com.sqool.sqoolbus.tenant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserOtp entity operations
 */
@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {
    
    /**
     * Find valid OTP for user and type
     */
    @Query("SELECT o FROM UserOtp o WHERE o.user = :user AND o.otpType = :otpType " +
           "AND o.isUsed = false AND o.expiresAt > :now AND o.attempts < o.maxAttempts " +
           "ORDER BY o.createdAt DESC")
    Optional<UserOtp> findValidOtpByUserAndType(@Param("user") User user, 
                                                @Param("otpType") UserOtp.OtpType otpType,
                                                @Param("now") LocalDateTime now);
    
    /**
     * Find OTP by code and type
     */
    @Query("SELECT o FROM UserOtp o WHERE o.otpCode = :otpCode AND o.otpType = :otpType " +
           "AND o.isUsed = false AND o.expiresAt > :now AND o.attempts < o.maxAttempts")
    Optional<UserOtp> findValidOtpByCodeAndType(@Param("otpCode") String otpCode,
                                                @Param("otpType") UserOtp.OtpType otpType,
                                                @Param("now") LocalDateTime now);
    
    /**
     * Find all OTPs for a user by type
     */
    List<UserOtp> findByUserAndOtpTypeOrderByCreatedAtDesc(User user, UserOtp.OtpType otpType);
    
    /**
     * Find recent OTPs for rate limiting
     */
    @Query("SELECT o FROM UserOtp o WHERE o.user = :user AND o.otpType = :otpType " +
           "AND o.createdAt > :since ORDER BY o.createdAt DESC")
    List<UserOtp> findRecentOtpsByUserAndType(@Param("user") User user,
                                              @Param("otpType") UserOtp.OtpType otpType,
                                              @Param("since") LocalDateTime since);
    
    /**
     * Mark all unused OTPs for user and type as expired
     */
    @Modifying
    @Query("UPDATE UserOtp o SET o.isUsed = true WHERE o.user = :user AND o.otpType = :otpType " +
           "AND o.isUsed = false")
    int invalidateAllOtpsForUserAndType(@Param("user") User user, @Param("otpType") UserOtp.OtpType otpType);
    
    /**
     * Clean up expired OTPs
     */
    @Modifying
    @Query("DELETE FROM UserOtp o WHERE o.expiresAt < :expiredBefore")
    int deleteExpiredOtps(@Param("expiredBefore") LocalDateTime expiredBefore);
    
    /**
     * Clean up used OTPs older than specified date
     */
    @Modifying
    @Query("DELETE FROM UserOtp o WHERE o.isUsed = true AND o.usedAt < :usedBefore")
    int deleteOldUsedOtps(@Param("usedBefore") LocalDateTime usedBefore);
    
    /**
     * Count OTPs generated for user in timeframe for rate limiting
     */
    @Query("SELECT COUNT(o) FROM UserOtp o WHERE o.user = :user AND o.otpType = :otpType " +
           "AND o.createdAt > :since")
    long countOtpsGeneratedSince(@Param("user") User user,
                                 @Param("otpType") UserOtp.OtpType otpType,
                                 @Param("since") LocalDateTime since);
    
    /**
     * Find OTPs by delivery method and destination
     */
    List<UserOtp> findByDeliveryMethodAndDeliveryDestinationOrderByCreatedAtDesc(
        UserOtp.DeliveryMethod deliveryMethod, String deliveryDestination);
    
    /**
     * Find failed attempts for user in timeframe
     */
    @Query("SELECT COUNT(o) FROM UserOtp o WHERE o.user = :user AND o.otpType = :otpType " +
           "AND o.attempts >= o.maxAttempts AND o.createdAt > :since")
    long countFailedAttemptsForUserSince(@Param("user") User user,
                                         @Param("otpType") UserOtp.OtpType otpType,
                                         @Param("since") LocalDateTime since);
}
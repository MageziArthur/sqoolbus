package com.sqool.sqoolbus.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service to track user activity and manage inactivity timeouts
 */
@Service
public class ActivityTrackingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ActivityTrackingService.class);
    
    @Value("${app.security.inactivity-timeout-minutes:5}")
    private int inactivityTimeoutMinutes;
    
    // Map to store last activity time for each user session
    private final ConcurrentHashMap<String, LocalDateTime> userActivityMap = new ConcurrentHashMap<>();
    
    // Scheduled executor for cleanup tasks
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public ActivityTrackingService() {
        // Start cleanup task that runs every minute to remove expired sessions
        scheduler.scheduleAtFixedRate(this::cleanupExpiredSessions, 1, 1, TimeUnit.MINUTES);
    }
    
    /**
     * Update the last activity time for a user session
     * @param sessionKey unique key for the user session (could be username + tenantId)
     */
    public void updateActivity(String sessionKey) {
        LocalDateTime now = LocalDateTime.now();
        userActivityMap.put(sessionKey, now);
        logger.debug("Updated activity for session: {} at {}", sessionKey, now);
    }
    
    /**
     * Check if a user session is inactive (exceeded the inactivity timeout)
     * @param sessionKey unique key for the user session
     * @return true if the session is inactive, false otherwise
     */
    public boolean isSessionInactive(String sessionKey) {
        LocalDateTime lastActivity = userActivityMap.get(sessionKey);
        if (lastActivity == null) {
            // No activity recorded, consider as inactive
            return true;
        }
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(inactivityTimeoutMinutes);
        boolean isInactive = lastActivity.isBefore(cutoffTime);
        
        if (isInactive) {
            logger.debug("Session {} is inactive. Last activity: {}, Cutoff time: {}", 
                        sessionKey, lastActivity, cutoffTime);
        }
        
        return isInactive;
    }
    
    /**
     * Remove a user session from tracking
     * @param sessionKey unique key for the user session
     */
    public void removeSession(String sessionKey) {
        LocalDateTime removed = userActivityMap.remove(sessionKey);
        if (removed != null) {
            logger.debug("Removed session: {} that was last active at {}", sessionKey, removed);
        }
    }
    
    /**
     * Get the last activity time for a session
     * @param sessionKey unique key for the user session
     * @return last activity time or null if not found
     */
    public LocalDateTime getLastActivityTime(String sessionKey) {
        return userActivityMap.get(sessionKey);
    }
    
    /**
     * Get the inactivity timeout in minutes
     * @return inactivity timeout in minutes
     */
    public int getInactivityTimeoutMinutes() {
        return inactivityTimeoutMinutes;
    }
    
    /**
     * Cleanup expired sessions from the activity map
     */
    private void cleanupExpiredSessions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(inactivityTimeoutMinutes + 1);
        
        userActivityMap.entrySet().removeIf(entry -> {
            boolean shouldRemove = entry.getValue().isBefore(cutoffTime);
            if (shouldRemove) {
                logger.debug("Cleaning up expired session: {} with last activity: {}", 
                            entry.getKey(), entry.getValue());
            }
            return shouldRemove;
        });
    }
    
    /**
     * Get the current number of active sessions
     * @return number of active sessions
     */
    public int getActiveSessionCount() {
        return userActivityMap.size();
    }
    
    /**
     * Create a session key from username and tenant ID
     * @param username the username
     * @param tenantId the tenant ID
     * @return session key
     */
    public String createSessionKey(String username, String tenantId) {
        return username + "@" + (tenantId != null ? tenantId : "master");
    }
}
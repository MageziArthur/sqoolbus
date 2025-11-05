package com.sqool.sqoolbus.service;

import com.sqool.sqoolbus.config.multitenancy.TenantContext;
import com.sqool.sqoolbus.master.entity.Tenant;
import com.sqool.sqoolbus.master.repository.TenantRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TenantDataSourceService {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantDataSourceService.class);
    
    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;
    
    @Autowired
    @Lazy
    private TenantRepository tenantRepository;
    
    @Value("${sqoolbus.multitenancy.default-tenant}")
    private String defaultTenant;
    
    // Cache for tenant datasources to avoid creating them repeatedly
    private final Map<String, DataSource> tenantDataSources = new ConcurrentHashMap<>();
    
    /**
     * Get or create a datasource for the specified tenant
     */
    public DataSource getDataSourceForTenant(String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            logger.warn("Tenant ID is null or empty, using default tenant");
            tenantId = defaultTenant;
        }
        
        // Return cached datasource if available
        if (tenantDataSources.containsKey(tenantId)) {
            logger.debug("Using cached datasource for tenant: {}", tenantId);
            return tenantDataSources.get(tenantId);
        }
        
        // Create new datasource for tenant
        DataSource dataSource = createDataSourceForTenant(tenantId);
        if (dataSource != null) {
            tenantDataSources.put(tenantId, dataSource);
            logger.info("Created and cached new datasource for tenant: {}", tenantId);
        }
        
        return dataSource;
    }
    
    /**
     * Create a new datasource for the specified tenant
     */
    private DataSource createDataSourceForTenant(String tenantId) {
        try {
            // Set master context to query tenant information
            String originalTenant = TenantContext.getTenantId();
            TenantContext.clear(); // Clear to use master database
            
            try {
                Optional<Tenant> tenantOptional = Optional.empty();
                
                // Handle the case where repository might not be initialized yet
                if (tenantRepository != null) {
                    tenantOptional = tenantRepository.findByTenantId(tenantId);
                }
                
                if (tenantOptional.isEmpty()) {
                    // If tenant doesn't exist and it's the default tenant, create default configuration
                    if (defaultTenant.equals(tenantId)) {
                        logger.info("Creating default tenant datasource configuration");
                        return createDefaultTenantDataSource();
                    } else {
                        logger.error("Tenant not found: {}", tenantId);
                        return null;
                    }
                }
                
                Tenant tenant = tenantOptional.get();
                
                if (!tenant.getIsActive()) {
                    logger.error("Tenant is not active: {}", tenantId);
                    return null;
                }
                
                // Create datasource from tenant configuration
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(tenant.getDatabaseUrl());
                config.setUsername(tenant.getDatabaseUsername());
                config.setPassword(tenant.getDatabasePassword());
                config.setDriverClassName(tenant.getDatabaseDriver());
                config.setMaximumPoolSize(tenant.getMaxPoolSize() != null ? tenant.getMaxPoolSize() : 10);
                config.setMinimumIdle(tenant.getMinIdleSize() != null ? tenant.getMinIdleSize() : 2);
                config.setConnectionTimeout(30000);
                config.setIdleTimeout(600000);
                config.setMaxLifetime(1800000);
                config.setLeakDetectionThreshold(60000);
                
                // Connection pool name for debugging
                config.setPoolName("TenantPool-" + tenantId);
                
                return new HikariDataSource(config);
                
            } finally {
                // Restore original tenant context
                if (originalTenant != null) {
                    TenantContext.setTenantId(originalTenant);
                }
            }
            
        } catch (Exception e) {
            logger.error("Failed to create datasource for tenant: {}", tenantId, e);
            return null;
        }
    }
    
    /**
     * Create the default tenant datasource
     */
    private DataSource createDefaultTenantDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/default_sqool?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        config.setUsername("root");
        config.setPassword("rootpassword");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("DefaultTenantPool");
        
        return new HikariDataSource(config);
    }
    
    /**
     * Validate if a tenant exists and is active
     * For now, we'll use simple validation - in production, this could be cached or configured
     */
    public boolean isTenantValid(String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            return false;
        }
        
        // Always allow default tenant
        if (defaultTenant.equals(tenantId)) {
            return true;
        }
        
        // For now, we'll allow any non-empty tenant ID
        // In production, you would want to validate against a cached list or configuration
        // To avoid database queries in filters which can cause session issues
        return tenantId.trim().length() > 0;
    }
    
    /**
     * Validate tenant using database query (use only when not in filter context)
     */
    public boolean isTenantValidFromDatabase(String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            return false;
        }
        
        if (defaultTenant.equals(tenantId)) {
            return true; // Default tenant is always valid
        }
        
        try {
            String originalTenant = TenantContext.getTenantId();
            TenantContext.clear(); // Clear to use master database
            
            try {
                Optional<Tenant> tenantOptional = tenantRepository.findByTenantId(tenantId);
                return tenantOptional.isPresent() && tenantOptional.get().getIsActive();
            } finally {
                if (originalTenant != null) {
                    TenantContext.setTenantId(originalTenant);
                }
            }
        } catch (Exception e) {
            logger.error("Error validating tenant from database: {}", tenantId, e);
            return false;
        }
    }
    
    /**
     * Remove a tenant datasource from cache (useful when tenant is deleted or updated)
     */
    public void removeTenantDataSource(String tenantId) {
        DataSource dataSource = tenantDataSources.remove(tenantId);
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
            logger.info("Closed and removed datasource for tenant: {}", tenantId);
        }
    }
    
    /**
     * Clear cache for a specific tenant to force recreation of datasource
     */
    public void clearCache(String tenantId) {
        removeTenantDataSource(tenantId);
        logger.info("Cleared cache for tenant: {}", tenantId);
    }
    
    /**
     * Get all cached tenant IDs
     */
    public java.util.Set<String> getCachedTenants() {
        return tenantDataSources.keySet();
    }
}
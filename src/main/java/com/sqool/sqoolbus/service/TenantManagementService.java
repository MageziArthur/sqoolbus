package com.sqool.sqoolbus.service;

import com.sqool.sqoolbus.config.SqoolbusProperties;
import com.sqool.sqoolbus.dto.TenantRegistrationRequest;
import com.sqool.sqoolbus.dto.TenantRegistrationResponse;
import com.sqool.sqoolbus.dto.TenantSetupResponse;
import com.sqool.sqoolbus.master.entity.Tenant;
import com.sqool.sqoolbus.master.repository.TenantRepository;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
public class TenantManagementService {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantManagementService.class);
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Autowired
    private TenantDataSourceService tenantDataSourceService;
    
    @Autowired
    private SqoolbusProperties sqoolbusProperties;
    
    @Transactional
    public TenantRegistrationResponse registerTenant(TenantRegistrationRequest request) {
        logger.info("Registering new tenant: {}", request.getTenantId());
        
        // Check if tenant already exists
        if (tenantRepository.findByTenantId(request.getTenantId()).isPresent()) {
            throw new RuntimeException("Tenant with ID '" + request.getTenantId() + "' already exists");
        }
        
        // Generate database name if not provided
        String databaseName = request.getDatabaseName();
        if (databaseName == null || databaseName.trim().isEmpty()) {
            databaseName = request.getTenantId();
        }
        
        // Create database
        createDatabase(databaseName);
        
        // Create tenant entity
        Tenant tenant = new Tenant();
        tenant.setTenantId(request.getTenantId());
        tenant.setTenantName(request.getTenantName());
        tenant.setDescription(request.getDescription());
        tenant.setDatabaseUrl(String.format("jdbc:mysql://%s:%s/%s", 
            sqoolbusProperties.getDatabase().getHost(), 
            sqoolbusProperties.getDatabase().getPort(), 
            databaseName));
        tenant.setDatabaseUsername(sqoolbusProperties.getDatabase().getUsername());
        tenant.setDatabasePassword(sqoolbusProperties.getDatabase().getPassword());
        tenant.setDatabaseDriver("com.mysql.cj.jdbc.Driver");
        tenant.setIsActive(true);
        tenant.setMinIdleSize(5);
        tenant.setMaxPoolSize(20);
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());
        
        // Save tenant
        Tenant savedTenant = tenantRepository.save(tenant);
        logger.info("Tenant '{}' registered successfully", request.getTenantId());
        
        return new TenantRegistrationResponse(
            savedTenant.getTenantId(),
            savedTenant.getTenantName(),
            databaseName,
            savedTenant.getDatabaseUrl(),
            savedTenant.getIsActive(),
            "Tenant registered successfully. Run setup to initialize database."
        );
    }
    
    public TenantSetupResponse setupTenant(String tenantId) {
        logger.info("Setting up tenant: {}", tenantId);
        
        // Get tenant details
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
            .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));
        
        try {
            // Run Liquibase migrations
            int changesetsExecuted = runTenantMigrations(tenant);
            
            // Clear any cached data sources to ensure fresh connection
            tenantDataSourceService.clearCache(tenantId);
            
            logger.info("Tenant '{}' setup completed successfully", tenantId);
            
            return new TenantSetupResponse(
                tenantId,
                "SUCCESS",
                changesetsExecuted,
                "Tenant setup completed successfully. Admin user created with credentials: admin/admin123",
                true,
                "Username: admin, Password: admin123"
            );
            
        } catch (Exception e) {
            logger.error("Failed to setup tenant: {}", tenantId, e);
            return new TenantSetupResponse(
                tenantId,
                "FAILED",
                0,
                "Tenant setup failed: " + e.getMessage(),
                false,
                null
            );
        }
    }
    
    private void createDatabase(String databaseName) {
        String rootUrl = String.format("jdbc:mysql://%s:%s/", 
            sqoolbusProperties.getDatabase().getHost(), 
            sqoolbusProperties.getDatabase().getPort());
        
        try (Connection connection = DriverManager.getConnection(rootUrl, 
                sqoolbusProperties.getDatabase().getUsername(), 
                sqoolbusProperties.getDatabase().getPassword())) {
            String createDbSql = "CREATE DATABASE IF NOT EXISTS `" + databaseName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            
            try (PreparedStatement statement = connection.prepareStatement(createDbSql)) {
                statement.executeUpdate();
                logger.info("Database '{}' created or already exists", databaseName);
            }
            
        } catch (SQLException e) {
            logger.error("Failed to create database: {}", databaseName, e);
            throw new RuntimeException("Failed to create database: " + e.getMessage(), e);
        }
    }
    
    private int runTenantMigrations(Tenant tenant) throws Exception {
        try (Connection connection = DriverManager.getConnection(
                tenant.getDatabaseUrl(), 
                tenant.getDatabaseUsername(), 
                tenant.getDatabasePassword())) {
            
            Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            
            Liquibase liquibase = new Liquibase(
                "db/changelog/tenant-changelog.yml",
                new ClassLoaderResourceAccessor(),
                database
            );
            
            // Run migrations
            liquibase.update(new Contexts(), new LabelExpression());
            
            // Get number of changesets executed (approximate)
            return 13; // This is the current number of changesets in tenant-changelog.yml
            
        }
    }
}
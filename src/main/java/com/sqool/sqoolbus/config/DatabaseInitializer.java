package com.sqool.sqoolbus.config;

import com.sqool.sqoolbus.config.multitenancy.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DatabaseInitializer implements ApplicationRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    
    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;
    
    @Autowired
    private LiquibaseConfig liquibaseConfig;
    
    @Value("${spring.datasource.master.url}")
    private String masterUrl;
    
    @Value("${spring.datasource.master.username}")
    private String masterUsername;
    
    @Value("${spring.datasource.master.password}")
    private String masterPassword;
    
    @Value("${spring.datasource.master.driver-class-name}")
    private String masterDriverClassName;
    
    @Value("${spring.datasource.tenant.default.url}")
    private String defaultTenantUrl;
    
    @Value("${spring.datasource.tenant.default.username}")
    private String defaultTenantUsername;
    
    @Value("${spring.datasource.tenant.default.password}")
    private String defaultTenantPassword;
    
    @Value("${spring.datasource.tenant.default.driver-class-name}")
    private String defaultTenantDriverClassName;
    
    @Value("${sqoolbus.multitenancy.default-tenant}")
    private String defaultTenantId;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Starting database initialization...");
        
        try {
            // First, create databases if they don't exist
            createDatabasesIfNotExist();
            
            // Run master database migration
            logger.info("Running master database migration...");
            liquibaseConfig.runMasterDatabaseMigration(masterDataSource);
            
            // Set default tenant context and run tenant migration
            logger.info("Running default tenant database migration...");
            TenantContext.setTenantId(defaultTenantId);
            
            // Create default tenant datasource from configuration
            liquibaseConfig.runTenantDatabaseMigration(createDefaultTenantDataSource(), defaultTenantId);
            
            TenantContext.clear();
            
            logger.info("Database initialization completed successfully");
            
        } catch (Exception e) {
            logger.error("Database initialization failed", e);
            throw e;
        }
    }
    
    private DataSource createDefaultTenantDataSource() {
        com.zaxxer.hikari.HikariConfig config = new com.zaxxer.hikari.HikariConfig();
        config.setJdbcUrl(defaultTenantUrl);
        config.setUsername(defaultTenantUsername);
        config.setPassword(defaultTenantPassword);
        config.setDriverClassName(defaultTenantDriverClassName);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        return new com.zaxxer.hikari.HikariDataSource(config);
    }
    
    private void createDatabasesIfNotExist() throws Exception {
        logger.info("Creating databases if they don't exist...");
        
        // Extract connection details from master URL
        String serverUrl = extractServerUrl(masterUrl);
        
        // Create a connection to MySQL server without specifying a database
        com.zaxxer.hikari.HikariConfig config = new com.zaxxer.hikari.HikariConfig();
        config.setJdbcUrl(serverUrl);
        config.setUsername(masterUsername);
        config.setPassword(masterPassword);
        config.setDriverClassName(masterDriverClassName);
        config.setMaximumPoolSize(1);
        
        try (com.zaxxer.hikari.HikariDataSource dataSource = new com.zaxxer.hikari.HikariDataSource(config);
             java.sql.Connection connection = dataSource.getConnection();
             java.sql.Statement statement = connection.createStatement()) {
            
            // Extract database names from URLs
            String masterDbName = extractDatabaseName(masterUrl);
            String tenantDbName = extractDatabaseName(defaultTenantUrl);
            
            // Create master database
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + masterDbName);
            logger.info("Master database ({}) created or already exists", masterDbName);
            
            // Create default tenant database
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + tenantDbName);
            logger.info("Default tenant database ({}) created or already exists", tenantDbName);
            
        } catch (Exception e) {
            logger.error("Failed to create databases", e);
            throw e;
        }
    }
    
    private String extractServerUrl(String jdbcUrl) {
        // Extract server URL from JDBC URL (e.g., "jdbc:mysql://localhost:3306/dbname" -> "jdbc:mysql://localhost:3306")
        int lastSlashIndex = jdbcUrl.lastIndexOf('/');
        if (lastSlashIndex > 0) {
            return jdbcUrl.substring(0, lastSlashIndex);
        }
        return jdbcUrl;
    }
    
    private String extractDatabaseName(String jdbcUrl) {
        // Extract database name from JDBC URL
        int lastSlashIndex = jdbcUrl.lastIndexOf('/');
        if (lastSlashIndex > 0 && lastSlashIndex < jdbcUrl.length() - 1) {
            String dbPart = jdbcUrl.substring(lastSlashIndex + 1);
            // Remove query parameters if present
            int questionMarkIndex = dbPart.indexOf('?');
            if (questionMarkIndex > 0) {
                dbPart = dbPart.substring(0, questionMarkIndex);
            }
            return dbPart;
        }
        throw new IllegalArgumentException("Could not extract database name from URL: " + jdbcUrl);
    }
}
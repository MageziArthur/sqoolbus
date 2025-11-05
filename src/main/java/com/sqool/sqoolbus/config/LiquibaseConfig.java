package com.sqool.sqoolbus.config;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class LiquibaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(LiquibaseConfig.class);
    
    @Value("${spring.datasource.master.url}")
    private String masterUrl;
    
    @Value("${spring.datasource.master.username}")
    private String masterUsername;
    
    @Value("${spring.datasource.master.password}")
    private String masterPassword;
    
    @Value("${spring.datasource.tenant.default.url}")
    private String defaultTenantUrl;
    
    @Value("${spring.datasource.tenant.default.username}")
    private String defaultTenantUsername;
    
    @Value("${spring.datasource.tenant.default.password}")
    private String defaultTenantPassword;
    
    public void runMasterDatabaseMigration(DataSource masterDataSource) {
        try (Connection connection = masterDataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            
            ClassPathResource changelogResource = new ClassPathResource("db/changelog/master-changelog.yml");
            if (changelogResource.exists()) {
                Liquibase liquibase = new Liquibase("db/changelog/master-changelog.yml", 
                                                   new ClassLoaderResourceAccessor(), 
                                                   database);
                liquibase.update(new Contexts(), new LabelExpression());
                logger.info("Master database migration completed successfully");
            } else {
                logger.warn("Master changelog file not found");
            }
        } catch (Exception e) {
            logger.error("Error running master database migration", e);
            throw new RuntimeException("Failed to migrate master database", e);
        }
    }
    
    public void runTenantDatabaseMigration(DataSource tenantDataSource, String tenantId) {
        try (Connection connection = tenantDataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            
            ClassPathResource changelogResource = new ClassPathResource("db/changelog/tenant-changelog.yml");
            if (changelogResource.exists()) {
                Liquibase liquibase = new Liquibase("db/changelog/tenant-changelog.yml", 
                                                   new ClassLoaderResourceAccessor(), 
                                                   database);
                liquibase.update(new Contexts(), new LabelExpression());
                logger.info("Tenant database migration completed successfully for tenant: {}", tenantId);
            } else {
                logger.warn("Tenant changelog file not found");
            }
        } catch (Exception e) {
            logger.error("Error running tenant database migration for tenant: {}", tenantId, e);
            throw new RuntimeException("Failed to migrate tenant database: " + tenantId, e);
        }
    }
}
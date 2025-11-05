package com.sqool.sqoolbus.config.multitenancy;

import com.sqool.sqoolbus.service.TenantDataSourceService;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class MultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String> {
    
    private static final Logger logger = LoggerFactory.getLogger(MultiTenantConnectionProvider.class);
    
    @Autowired
    private TenantDataSourceService tenantDataSourceService;
    
    @Value("${sqoolbus.multitenancy.default-tenant}")
    private String defaultTenant;
    
    @Value("${spring.datasource.tenant.default.url}")
    private String defaultTenantUrl;
    
    @Value("${spring.datasource.tenant.default.username}")
    private String defaultTenantUsername;
    
    @Value("${spring.datasource.tenant.default.password}")
    private String defaultTenantPassword;
    
    @Value("${spring.datasource.tenant.default.driver-class-name}")
    private String defaultTenantDriverClassName;
    
    @Override
    protected DataSource selectAnyDataSource() {
        // During application startup, the tenantDataSourceService might not be fully initialized
        // Return a default datasource for the default tenant
        if (tenantDataSourceService == null) {
            return createDefaultTenantDataSource();
        }
        return tenantDataSourceService.getDataSourceForTenant(defaultTenant);
    }
    
    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        logger.debug("Selecting datasource for tenant: {}", tenantIdentifier);
        if (tenantDataSourceService == null) {
            return createDefaultTenantDataSource();
        }
        return tenantDataSourceService.getDataSourceForTenant(tenantIdentifier);
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
}
package com.sqool.sqoolbus.config;

import com.sqool.sqoolbus.config.multitenancy.MultiTenantConnectionProvider;
import com.sqool.sqoolbus.config.multitenancy.TenantIdentifierResolver;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.sqool.sqoolbus.tenant.repository",
    entityManagerFactoryRef = "tenantEntityManagerFactory",
    transactionManagerRef = "tenantTransactionManager"
)
public class TenantDatabaseConfig {
    
    @Bean
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            MultiTenantConnectionProvider multiTenantConnectionProvider,
            TenantIdentifierResolver tenantIdentifierResolver) {
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", false);
        properties.put("hibernate.format_sql", true);
        
        // Multi-tenancy configuration
        properties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        properties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
        properties.put("hibernate.multiTenancy", "DATABASE");
        
        // Create a dummy datasource - the actual datasource will be provided by MultiTenantConnectionProvider
        javax.sql.DataSource dummyDataSource = DataSourceBuilder.create()
                .url("jdbc:h2:mem:dummy")
                .username("sa")
                .password("")
                .driverClassName("org.h2.Driver")
                .build();
        
        return builder
                .dataSource(dummyDataSource)
                .packages("com.sqool.sqoolbus.tenant.entity")
                .persistenceUnit("tenant")
                .properties(properties)
                .build();
    }
    
    @Bean
    public PlatformTransactionManager tenantTransactionManager(
            @Qualifier("tenantEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
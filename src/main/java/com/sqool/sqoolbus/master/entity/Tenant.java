package com.sqool.sqoolbus.master.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenants", schema = "public")
public class Tenant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "tenant_id", unique = true, nullable = false)
    private String tenantId;
    
    @NotBlank
    @Column(name = "tenant_name", nullable = false)
    private String tenantName;
    
    @Column(name = "description")
    private String description;
    
    @NotBlank
    @Column(name = "database_url", nullable = false)
    private String databaseUrl;
    
    @NotBlank
    @Column(name = "database_username", nullable = false)
    private String databaseUsername;
    
    @NotBlank
    @Column(name = "database_password", nullable = false)
    private String databasePassword;
    
    @NotBlank
    @Column(name = "database_driver", nullable = false)
    private String databaseDriver;
    
    @Column(name = "max_pool_size")
    private Integer maxPoolSize = 20;
    
    @Column(name = "min_idle_size")
    private Integer minIdleSize = 5;
    
    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Tenant() {}
    
    public Tenant(String tenantId, String tenantName, String databaseUrl, 
                  String databaseUsername, String databasePassword, String databaseDriver) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.databaseUrl = databaseUrl;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
        this.databaseDriver = databaseDriver;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getTenantName() {
        return tenantName;
    }
    
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDatabaseUrl() {
        return databaseUrl;
    }
    
    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }
    
    public String getDatabaseUsername() {
        return databaseUsername;
    }
    
    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }
    
    public String getDatabasePassword() {
        return databasePassword;
    }
    
    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }
    
    public String getDatabaseDriver() {
        return databaseDriver;
    }
    
    public void setDatabaseDriver(String databaseDriver) {
        this.databaseDriver = databaseDriver;
    }
    
    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }
    
    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }
    
    public Integer getMinIdleSize() {
        return minIdleSize;
    }
    
    public void setMinIdleSize(Integer minIdleSize) {
        this.minIdleSize = minIdleSize;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Tenant{" +
                "id=" + id +
                ", tenantId='" + tenantId + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
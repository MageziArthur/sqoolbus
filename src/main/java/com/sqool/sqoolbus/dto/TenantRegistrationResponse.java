package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tenant registration response")
public class TenantRegistrationResponse {
    
    @Schema(description = "Tenant identifier", example = "company_abc")
    private String tenantId;
    
    @Schema(description = "Tenant display name", example = "Company ABC Ltd")
    private String tenantName;
    
    @Schema(description = "Database name created for the tenant", example = "company_abc_db")
    private String databaseName;
    
    @Schema(description = "Database URL for the tenant", example = "jdbc:mysql://localhost:3306/company_abc_db")
    private String databaseUrl;
    
    @Schema(description = "Whether the tenant is active", example = "true")
    private boolean isActive;
    
    @Schema(description = "Setup status message", example = "Tenant registered successfully. Run setup to initialize database.")
    private String setupStatus;
    
    public TenantRegistrationResponse() {}
    
    public TenantRegistrationResponse(String tenantId, String tenantName, String databaseName, 
                                    String databaseUrl, boolean isActive, String setupStatus) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.databaseName = databaseName;
        this.databaseUrl = databaseUrl;
        this.isActive = isActive;
        this.setupStatus = setupStatus;
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
    
    public String getDatabaseName() {
        return databaseName;
    }
    
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
    
    public String getDatabaseUrl() {
        return databaseUrl;
    }
    
    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public String getSetupStatus() {
        return setupStatus;
    }
    
    public void setSetupStatus(String setupStatus) {
        this.setupStatus = setupStatus;
    }
}
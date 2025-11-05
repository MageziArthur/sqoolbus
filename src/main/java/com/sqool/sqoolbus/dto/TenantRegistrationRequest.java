package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Tenant registration request")
public class TenantRegistrationRequest {
    
    @Schema(description = "Unique tenant identifier", example = "company_abc", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Tenant ID is required")
    @Pattern(regexp = "^[a-z0-9_]+$", message = "Tenant ID must contain only lowercase letters, numbers, and underscores")
    @Size(min = 3, max = 50, message = "Tenant ID must be between 3 and 50 characters")
    private String tenantId;
    
    @Schema(description = "Tenant display name", example = "Company ABC Ltd", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Tenant name is required")
    @Size(min = 2, max = 100, message = "Tenant name must be between 2 and 100 characters")
    private String tenantName;
    
    @Schema(description = "Description of the tenant", example = "ABC Company tenant for learning management")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    
    @Schema(description = "Database name for the tenant", example = "company_abc_db")
    @Pattern(regexp = "^[a-z0-9_]+$", message = "Database name must contain only lowercase letters, numbers, and underscores")
    @Size(min = 3, max = 63, message = "Database name must be between 3 and 63 characters")
    private String databaseName;
    
    public TenantRegistrationRequest() {}
    
    public TenantRegistrationRequest(String tenantId, String tenantName, String description, String databaseName) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.description = description;
        this.databaseName = databaseName;
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
    
    public String getDatabaseName() {
        return databaseName;
    }
    
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
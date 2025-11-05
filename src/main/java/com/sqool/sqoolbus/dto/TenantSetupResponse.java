package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tenant setup response")
public class TenantSetupResponse {
    
    @Schema(description = "Tenant identifier", example = "company_abc")
    private String tenantId;
    
    @Schema(description = "Database migration status", example = "SUCCESS")
    private String migrationStatus;
    
    @Schema(description = "Number of changesets executed", example = "13")
    private int changesetsExecuted;
    
    @Schema(description = "Setup completion message", example = "Tenant setup completed successfully. Admin user created with credentials: admin/admin123")
    private String message;
    
    @Schema(description = "Whether the setup was successful", example = "true")
    private boolean success;
    
    @Schema(description = "Admin login credentials", example = "Username: admin, Password: admin123")
    private String adminCredentials;
    
    public TenantSetupResponse() {}
    
    public TenantSetupResponse(String tenantId, String migrationStatus, int changesetsExecuted, 
                              String message, boolean success, String adminCredentials) {
        this.tenantId = tenantId;
        this.migrationStatus = migrationStatus;
        this.changesetsExecuted = changesetsExecuted;
        this.message = message;
        this.success = success;
        this.adminCredentials = adminCredentials;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getMigrationStatus() {
        return migrationStatus;
    }
    
    public void setMigrationStatus(String migrationStatus) {
        this.migrationStatus = migrationStatus;
    }
    
    public int getChangesetsExecuted() {
        return changesetsExecuted;
    }
    
    public void setChangesetsExecuted(int changesetsExecuted) {
        this.changesetsExecuted = changesetsExecuted;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getAdminCredentials() {
        return adminCredentials;
    }
    
    public void setAdminCredentials(String adminCredentials) {
        this.adminCredentials = adminCredentials;
    }
}
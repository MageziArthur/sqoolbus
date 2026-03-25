package com.sqool.sqoolbus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to assign an existing driver to a school")
public class AddDriverToSchoolRequest {
    
    @Schema(description = "ID of the driver (user) from the master database to assign to this school", example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Driver ID is required")
    private Long driverId;
    
    // Constructors
    public AddDriverToSchoolRequest() {}
    
    public AddDriverToSchoolRequest(Long driverId) {
        this.driverId = driverId;
    }
    
    // Getters and Setters
    public Long getDriverId() {
        return driverId;
    }
    
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
}


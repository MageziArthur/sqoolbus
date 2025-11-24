package com.sqool.sqoolbus.dto;

import jakarta.validation.constraints.NotNull;

public class AssignDriverToBusRequest {
    
    @NotNull(message = "Bus ID is required")
    private Long busId;
    
    @NotNull(message = "Driver ID is required")
    private Long driverId;
    
    // Getters and Setters
    public Long getBusId() {
        return busId;
    }
    
    public void setBusId(Long busId) {
        this.busId = busId;
    }
    
    public Long getDriverId() {
        return driverId;
    }
    
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
}

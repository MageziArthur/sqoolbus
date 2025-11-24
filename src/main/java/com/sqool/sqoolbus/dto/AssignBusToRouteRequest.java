package com.sqool.sqoolbus.dto;

import jakarta.validation.constraints.NotNull;

public class AssignBusToRouteRequest {
    
    @NotNull(message = "Bus ID is required")
    private Long busId;
    
    @NotNull(message = "Route ID is required")
    private Long routeId;
    
    // Getters and Setters
    public Long getBusId() {
        return busId;
    }
    
    public void setBusId(Long busId) {
        this.busId = busId;
    }
    
    public Long getRouteId() {
        return routeId;
    }
    
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }
}

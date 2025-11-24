package com.sqool.sqoolbus.tenant.entity.hail;

/**
 * Enum representing the various statuses a bus can have
 */
public enum BusStatus {
    AVAILABLE,      // Bus is available for assignment
    IN_SERVICE,     // Bus is currently in service/on route
    MAINTENANCE,    // Bus is under maintenance
    OUT_OF_SERVICE, // Bus is out of service (broken down, etc.)
    RETIRED         // Bus has been retired from service
}

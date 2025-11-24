package com.sqool.sqoolbus.controller;

import com.sqool.sqoolbus.dto.*;
import com.sqool.sqoolbus.service.BusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
@Tag(name = "Bus Management", description = "APIs for managing school buses")
public class BusController {
    
    @Autowired
    private BusService busService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Create a new bus", description = "Creates a new bus for a school")
    public ResponseEntity<ApiResponse<BusResponse>> createBus(
            @Valid @RequestBody BusRequest request,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId) {
        
        BusResponse response = busService.createBus(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Bus created successfully", response));
    }
    
    @GetMapping("/{busId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'RIDER')")
    @Operation(summary = "Get bus by ID", description = "Retrieves bus details by bus ID")
    public ResponseEntity<ApiResponse<BusResponse>> getBusById(
            @PathVariable Long busId,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId) {
        
        BusResponse response = busService.getBusById(busId, tenantId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bus retrieved successfully", response));
    }
    
    @GetMapping("/school/{schoolId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'RIDER')")
    @Operation(summary = "Get all buses by school", description = "Retrieves all buses for a specific school")
    public ResponseEntity<ApiResponse<List<BusResponse>>> getAllBusesBySchool(
            @PathVariable Long schoolId,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId) {
        
        List<BusResponse> responses = busService.getAllBusesBySchool(schoolId, tenantId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Buses retrieved successfully", responses));
    }
    
    @GetMapping("/school/{schoolId}/available-without-route")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get available buses without route", description = "Retrieves buses that are available and not assigned to any route")
    public ResponseEntity<ApiResponse<List<BusResponse>>> getAvailableBusesWithoutRoute(
            @PathVariable Long schoolId,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId) {
        
        List<BusResponse> responses = busService.getAvailableBusesWithoutRoute(schoolId, tenantId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Available buses retrieved successfully", responses));
    }
    
    @GetMapping("/school/{schoolId}/available-without-driver")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get available buses without driver", description = "Retrieves buses that are available and have no assigned driver")
    public ResponseEntity<ApiResponse<List<BusResponse>>> getAvailableBusesWithoutDriver(
            @PathVariable Long schoolId,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId) {
        
        List<BusResponse> responses = busService.getAvailableBusesWithoutDriver(schoolId, tenantId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Available buses retrieved successfully", responses));
    }
    
    @PutMapping("/{busId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Update bus", description = "Updates bus details")
    public ResponseEntity<ApiResponse<BusResponse>> updateBus(
            @PathVariable Long busId,
            @Valid @RequestBody BusRequest request,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId) {
        
        BusResponse response = busService.updateBus(busId, request, tenantId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bus updated successfully", response));
    }
    
    @PostMapping("/{busId}/assign-route/{routeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Assign bus to route", description = "Assigns a bus to a specific route")
    public ResponseEntity<ApiResponse<BusResponse>> assignBusToRoute(
            @PathVariable Long busId,
            @PathVariable Long routeId,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId) {
        
        BusResponse response = busService.assignBusToRoute(busId, routeId, tenantId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bus assigned to route successfully", response));
    }
    
    @PostMapping("/{busId}/unassign-route")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Unassign bus from route", description = "Removes bus assignment from its current route")
    public ResponseEntity<ApiResponse<BusResponse>> unassignBusFromRoute(
            @PathVariable Long busId,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId) {
        
        BusResponse response = busService.unassignBusFromRoute(busId, tenantId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bus unassigned from route successfully", response));
    }
    
    @PostMapping("/{busId}/assign-driver/{driverId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Assign driver to bus", description = "Assigns a driver to a specific bus")
    public ResponseEntity<ApiResponse<BusResponse>> assignDriverToBus(
            @PathVariable Long busId,
            @PathVariable Long driverId,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId) {
        
        BusResponse response = busService.assignDriverToBus(busId, driverId, tenantId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver assigned to bus successfully", response));
    }
    
    @PostMapping("/{busId}/unassign-driver")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Unassign driver from bus", description = "Removes driver assignment from bus")
    public ResponseEntity<ApiResponse<BusResponse>> unassignDriverFromBus(
            @PathVariable Long busId,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId) {
        
        BusResponse response = busService.unassignDriverFromBus(busId, tenantId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver unassigned from bus successfully", response));
    }
    
    @DeleteMapping("/{busId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Delete bus", description = "Soft deletes a bus")
    public ResponseEntity<ApiResponse<Void>> deleteBus(
            @PathVariable Long busId,
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId) {
        
        busService.deleteBus(busId, tenantId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bus deleted successfully", null));
    }
}

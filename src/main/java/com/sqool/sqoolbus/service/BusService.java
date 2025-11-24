package com.sqool.sqoolbus.service;

import com.sqool.sqoolbus.config.multitenancy.TenantContext;
import com.sqool.sqoolbus.dto.*;
import com.sqool.sqoolbus.exception.DuplicateResourceException;
import com.sqool.sqoolbus.exception.ResourceNotFoundException;
import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.entity.hail.*;
import com.sqool.sqoolbus.tenant.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusService {
    
    private static final Logger logger = LoggerFactory.getLogger(BusService.class);
    
    @Autowired
    private BusRepository busRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private RouteRepository routeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public BusResponse createBus(BusRequest request, String tenantId) {
        try {
            TenantContext.setTenantId(tenantId);
            
            // Check for duplicate bus number
            if (busRepository.findByBusNumber(request.getBusNumber()).isPresent()) {
                throw new DuplicateResourceException("Bus", "busNumber", request.getBusNumber());
            }
            
            // Check for duplicate license plate
            if (busRepository.findByLicensePlate(request.getLicensePlate()).isPresent()) {
                throw new DuplicateResourceException("Bus", "licensePlate", request.getLicensePlate());
            }
            
            // Check for duplicate VIN if provided
            if (request.getVin() != null && busRepository.findByVin(request.getVin()).isPresent()) {
                throw new DuplicateResourceException("Bus", "vin", request.getVin());
            }
            
            // Find school
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School", "id", String.valueOf(request.getSchoolId())));
            
            // Create bus entity
            Bus bus = new Bus();
            bus.setBusNumber(request.getBusNumber());
            bus.setLicensePlate(request.getLicensePlate());
            bus.setMake(request.getMake());
            bus.setModel(request.getModel());
            bus.setYear(request.getYear());
            bus.setColor(request.getColor());
            bus.setVin(request.getVin());
            bus.setCapacity(request.getCapacity());
            bus.setFuelType(request.getFuelType());
            bus.setTransmissionType(request.getTransmissionType());
            bus.setStatus(request.getStatus() != null ? request.getStatus() : BusStatus.AVAILABLE);
            bus.setPurchaseDate(request.getPurchaseDate());
            bus.setPurchasePrice(request.getPurchasePrice());
            bus.setCurrentMileage(request.getCurrentMileage());
            bus.setLastServiceDate(request.getLastServiceDate());
            bus.setNextServiceDate(request.getNextServiceDate());
            bus.setInsurancePolicyNumber(request.getInsurancePolicyNumber());
            bus.setInsuranceExpiryDate(request.getInsuranceExpiryDate());
            bus.setRegistrationNumber(request.getRegistrationNumber());
            bus.setRegistrationExpiryDate(request.getRegistrationExpiryDate());
            bus.setInspectionExpiryDate(request.getInspectionExpiryDate());
            bus.setGpsDeviceId(request.getGpsDeviceId());
            bus.setWifiEnabled(request.getWifiEnabled());
            bus.setAcAvailable(request.getAcAvailable());
            bus.setWheelchairAccessible(request.getWheelchairAccessible());
            bus.setCctvInstalled(request.getCctvInstalled());
            bus.setFirstAidKit(request.getFirstAidKit());
            bus.setFireExtinguisher(request.getFireExtinguisher());
            bus.setEmergencyExits(request.getEmergencyExits());
            bus.setNotes(request.getNotes());
            bus.setSchool(school);
            
            bus = busRepository.save(bus);
            
            logger.info("Created bus: {} for school: {}", bus.getBusNumber(), school.getName());
            
            return mapToResponse(bus);
            
        } finally {
            TenantContext.clear();
        }
    }
    
    @Transactional(readOnly = true)
    public BusResponse getBusById(Long busId, String tenantId) {
        try {
            TenantContext.setTenantId(tenantId);
            
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", String.valueOf(busId)));
            
            return mapToResponse(bus);
            
        } finally {
            TenantContext.clear();
        }
    }
    
    @Transactional(readOnly = true)
    public List<BusResponse> getAllBusesBySchool(Long schoolId, String tenantId) {
        try {
            TenantContext.setTenantId(tenantId);
            
            List<Bus> buses = busRepository.findBySchoolId(schoolId);
            
            return buses.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            
        } finally {
            TenantContext.clear();
        }
    }
    
    @Transactional(readOnly = true)
    public List<BusResponse> getAvailableBusesWithoutRoute(Long schoolId, String tenantId) {
        try {
            TenantContext.setTenantId(tenantId);
            
            List<Bus> buses = busRepository.findAvailableBusesWithoutRoute(schoolId);
            
            return buses.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            
        } finally {
            TenantContext.clear();
        }
    }
    
    @Transactional(readOnly = true)
    public List<BusResponse> getAvailableBusesWithoutDriver(Long schoolId, String tenantId) {
        try {
            TenantContext.setTenantId(tenantId);
            
            List<Bus> buses = busRepository.findAvailableBusesWithoutDriver(schoolId);
            
            return buses.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            
        } finally {
            TenantContext.clear();
        }
    }
    
    @Transactional
    public BusResponse updateBus(Long busId, BusRequest request, String tenantId) {
        try {
            TenantContext.setTenantId(tenantId);
            
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", String.valueOf(busId)));
            
            // Check for duplicate bus number if changed
            if (!bus.getBusNumber().equals(request.getBusNumber())) {
                if (busRepository.findByBusNumber(request.getBusNumber()).isPresent()) {
                    throw new DuplicateResourceException("Bus", "busNumber", request.getBusNumber());
                }
            }
            
            // Check for duplicate license plate if changed
            if (!bus.getLicensePlate().equals(request.getLicensePlate())) {
                if (busRepository.findByLicensePlate(request.getLicensePlate()).isPresent()) {
                    throw new DuplicateResourceException("Bus", "licensePlate", request.getLicensePlate());
                }
            }
            
            // Update bus fields
            bus.setBusNumber(request.getBusNumber());
            bus.setLicensePlate(request.getLicensePlate());
            bus.setMake(request.getMake());
            bus.setModel(request.getModel());
            bus.setYear(request.getYear());
            bus.setColor(request.getColor());
            bus.setVin(request.getVin());
            bus.setCapacity(request.getCapacity());
            bus.setFuelType(request.getFuelType());
            bus.setTransmissionType(request.getTransmissionType());
            bus.setStatus(request.getStatus());
            bus.setPurchaseDate(request.getPurchaseDate());
            bus.setPurchasePrice(request.getPurchasePrice());
            bus.setCurrentMileage(request.getCurrentMileage());
            bus.setLastServiceDate(request.getLastServiceDate());
            bus.setNextServiceDate(request.getNextServiceDate());
            bus.setInsurancePolicyNumber(request.getInsurancePolicyNumber());
            bus.setInsuranceExpiryDate(request.getInsuranceExpiryDate());
            bus.setRegistrationNumber(request.getRegistrationNumber());
            bus.setRegistrationExpiryDate(request.getRegistrationExpiryDate());
            bus.setInspectionExpiryDate(request.getInspectionExpiryDate());
            bus.setGpsDeviceId(request.getGpsDeviceId());
            bus.setWifiEnabled(request.getWifiEnabled());
            bus.setAcAvailable(request.getAcAvailable());
            bus.setWheelchairAccessible(request.getWheelchairAccessible());
            bus.setCctvInstalled(request.getCctvInstalled());
            bus.setFirstAidKit(request.getFirstAidKit());
            bus.setFireExtinguisher(request.getFireExtinguisher());
            bus.setEmergencyExits(request.getEmergencyExits());
            bus.setNotes(request.getNotes());
            
            bus = busRepository.save(bus);
            
            logger.info("Updated bus: {}", bus.getBusNumber());
            
            return mapToResponse(bus);
            
        } finally {
            TenantContext.clear();
        }
    }
    
    @Transactional
    public BusResponse assignBusToRoute(Long busId, Long routeId, String tenantId) {
        try {
            TenantContext.setTenantId(tenantId);
            
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", String.valueOf(busId)));
            
            Route route = routeRepository.findById(routeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Route", "id", String.valueOf(routeId)));
            
            // Check if bus belongs to the same school as the route
            if (!bus.getSchool().getId().equals(route.getSchool().getId())) {
                throw new IllegalArgumentException("Bus and route must belong to the same school");
            }
            
            // Check if bus is available
            if (bus.getStatus() != BusStatus.AVAILABLE && bus.getStatus() != BusStatus.IN_SERVICE) {
                throw new IllegalStateException("Bus is not available for assignment. Current status: " + bus.getStatus());
            }
            
            bus.setAssignedRoute(route);
            bus.setStatus(BusStatus.IN_SERVICE);
            
            bus = busRepository.save(bus);
            
            logger.info("Assigned bus: {} to route: {}", bus.getBusNumber(), route.getRouteName());
            
            return mapToResponse(bus);
            
        } finally {
            TenantContext.clear();
        }
    }
    
    @Transactional
    public BusResponse unassignBusFromRoute(Long busId, String tenantId) {
        try {
            TenantContext.setTenantId(tenantId);
            
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", String.valueOf(busId)));
            
            if (bus.getAssignedRoute() == null) {
                throw new IllegalStateException("Bus is not assigned to any route");
            }
            
            logger.info("Unassigning bus: {} from route: {}", bus.getBusNumber(), bus.getAssignedRoute().getRouteName());
            
            bus.setAssignedRoute(null);
            bus.setStatus(BusStatus.AVAILABLE);
            
            bus = busRepository.save(bus);
            
            return mapToResponse(bus);
            
        } finally {
            TenantContext.clear();
        }
    }
    
    @Transactional
    public BusResponse assignDriverToBus(Long busId, Long driverId, String tenantId) {
        try {
            TenantContext.setTenantId(tenantId);
            
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", String.valueOf(busId)));
            
            User driver = userRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", String.valueOf(driverId)));
            
            // Verify driver has RIDER role
            boolean isRider = driver.getRoles().stream()
                    .anyMatch(role -> "RIDER".equals(role.getName()));
            
            if (!isRider) {
                throw new IllegalArgumentException("User is not a driver (RIDER role required)");
            }
            
            // Check if driver is already assigned to another bus
            busRepository.findByAssignedDriverId(driverId).ifPresent(existingBus -> {
                if (!existingBus.getId().equals(busId)) {
                    throw new IllegalStateException("Driver is already assigned to bus: " + existingBus.getBusNumber());
                }
            });
            
            bus.setAssignedDriver(driver);
            
            bus = busRepository.save(bus);
            
            logger.info("Assigned driver: {} to bus: {}", driver.getUsername(), bus.getBusNumber());
            
            return mapToResponse(bus);
            
        } finally {
            TenantContext.clear();
        }
    }
    
    @Transactional
    public BusResponse unassignDriverFromBus(Long busId, String tenantId) {
        try {
            TenantContext.setTenantId(tenantId);
            
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", String.valueOf(busId)));
            
            if (bus.getAssignedDriver() == null) {
                throw new IllegalStateException("Bus has no assigned driver");
            }
            
            logger.info("Unassigning driver: {} from bus: {}", 
                    bus.getAssignedDriver().getUsername(), bus.getBusNumber());
            
            bus.setAssignedDriver(null);
            
            bus = busRepository.save(bus);
            
            return mapToResponse(bus);
            
        } finally {
            TenantContext.clear();
        }
    }
    
    @Transactional
    public void deleteBus(Long busId, String tenantId) {
        try {
            TenantContext.setTenantId(tenantId);
            
            Bus bus = busRepository.findById(busId)
                    .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", String.valueOf(busId)));
            
            // Soft delete
            bus.setIsActive(false);
            busRepository.save(bus);
            
            logger.info("Deleted bus: {}", bus.getBusNumber());
            
        } finally {
            TenantContext.clear();
        }
    }
    
    private BusResponse mapToResponse(Bus bus) {
        BusResponse response = new BusResponse();
        response.setId(bus.getId());
        response.setBusNumber(bus.getBusNumber());
        response.setLicensePlate(bus.getLicensePlate());
        response.setMake(bus.getMake());
        response.setModel(bus.getModel());
        response.setYear(bus.getYear());
        response.setColor(bus.getColor());
        response.setVin(bus.getVin());
        response.setCapacity(bus.getCapacity());
        response.setFuelType(bus.getFuelType());
        response.setTransmissionType(bus.getTransmissionType());
        response.setStatus(bus.getStatus());
        response.setPurchaseDate(bus.getPurchaseDate());
        response.setPurchasePrice(bus.getPurchasePrice());
        response.setCurrentMileage(bus.getCurrentMileage());
        response.setLastServiceDate(bus.getLastServiceDate());
        response.setNextServiceDate(bus.getNextServiceDate());
        response.setInsurancePolicyNumber(bus.getInsurancePolicyNumber());
        response.setInsuranceExpiryDate(bus.getInsuranceExpiryDate());
        response.setRegistrationNumber(bus.getRegistrationNumber());
        response.setRegistrationExpiryDate(bus.getRegistrationExpiryDate());
        response.setInspectionExpiryDate(bus.getInspectionExpiryDate());
        response.setGpsDeviceId(bus.getGpsDeviceId());
        response.setWifiEnabled(bus.getWifiEnabled());
        response.setAcAvailable(bus.getAcAvailable());
        response.setWheelchairAccessible(bus.getWheelchairAccessible());
        response.setCctvInstalled(bus.getCctvInstalled());
        response.setFirstAidKit(bus.getFirstAidKit());
        response.setFireExtinguisher(bus.getFireExtinguisher());
        response.setEmergencyExits(bus.getEmergencyExits());
        response.setNotes(bus.getNotes());
        response.setIsActive(bus.getIsActive());
        response.setLastLocationLatitude(bus.getLastLocationLatitude());
        response.setLastLocationLongitude(bus.getLastLocationLongitude());
        response.setLastLocationUpdate(bus.getLastLocationUpdate());
        response.setCreatedAt(bus.getCreatedAt());
        response.setUpdatedAt(bus.getUpdatedAt());
        
        // Set school details
        if (bus.getSchool() != null) {
            response.setSchoolId(bus.getSchool().getId());
            response.setSchoolName(bus.getSchool().getName());
        }
        
        // Set assigned route details
        if (bus.getAssignedRoute() != null) {
            response.setAssignedRouteId(bus.getAssignedRoute().getId());
            response.setAssignedRouteName(bus.getAssignedRoute().getRouteName());
        }
        
        // Set assigned driver details
        if (bus.getAssignedDriver() != null) {
            response.setAssignedDriverId(bus.getAssignedDriver().getId());
            response.setAssignedDriverName(
                    bus.getAssignedDriver().getFirstName() + " " + bus.getAssignedDriver().getLastName()
            );
        }
        
        return response;
    }
}

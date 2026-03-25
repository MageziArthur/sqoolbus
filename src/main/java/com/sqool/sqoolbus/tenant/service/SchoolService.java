package com.sqool.sqoolbus.tenant.service;

import com.sqool.sqoolbus.config.multitenancy.TenantContext;
import com.sqool.sqoolbus.dto.CreateSchoolRequest;
import com.sqool.sqoolbus.dto.DriverDetailsResponse;
import com.sqool.sqoolbus.exception.ResourceNotFoundException;
import com.sqool.sqoolbus.master.entity.SchoolDriverMapping;
import com.sqool.sqoolbus.master.entity.Tenant;
import com.sqool.sqoolbus.master.entity.User;
import com.sqool.sqoolbus.master.repository.MasterUserRepository;
import com.sqool.sqoolbus.master.repository.SchoolDriverMappingRepository;
import com.sqool.sqoolbus.master.repository.TenantRepository;
import com.sqool.sqoolbus.security.SecurityUtils;
import com.sqool.sqoolbus.tenant.entity.hail.Bus;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import com.sqool.sqoolbus.tenant.repository.BusRepository;
import com.sqool.sqoolbus.tenant.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing schools
 */
@Service
@Transactional
public class SchoolService {
    
    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private BusRepository busRepository;
    
    @Autowired
    private SchoolDriverMappingRepository schoolDriverMappingRepository;
    
    @Autowired
    private MasterUserRepository masterUserRepository;
    
    @Autowired
    private TenantRepository tenantRepository;
    
    public List<School> findAll() {
        // If user is SYSTEM_ADMIN, return all schools
        if (SecurityUtils.isAnyAdmin()) {
            return schoolRepository.findAll();
        }
        
        // For school users, return only their school
        Long schoolId = SecurityUtils.getCurrentUserSchoolId();
        if (schoolId != null) {
            Optional<School> school = findById(schoolId);
            return school.map(List::of).orElse(List.of());
        }
        
        // If no school context, return empty list
        return List.of();
    }
    
    public Optional<School> findById(Long id) {
        return schoolRepository.findById(id);
    }
    
    public Optional<School> findBySchoolCode(String schoolCode) {
        // Assuming the school entity has a 'code' field, not 'schoolCode'
        return schoolRepository.findAll().stream()
                .filter(school -> schoolCode.equals(school.getCode()))
                .findFirst();
    }
    
    public List<School> findActiveSchools() {
        return schoolRepository.findByIsActiveTrue();
    }
    
    public List<School> findByType(String schoolType) {
        return schoolRepository.findBySchoolType(schoolType);
    }
    
    public School save(School school) {
        if (school.getId() == null) {
            throw new UnsupportedOperationException("School creation is not supported in tenant context. Only editing is allowed");
        }
        return schoolRepository.save(school);
    }
    
    public School createSchool(CreateSchoolRequest request) {
        throw new UnsupportedOperationException("School creation is not supported in tenant context. Only editing is allowed");
    }
    
    public School update(Long id, School schoolDetails) {
        School existingSchool = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", id.toString()));
        
        // Update fields that exist in the entity
        existingSchool.setName(schoolDetails.getName());
        existingSchool.setCode(schoolDetails.getCode());
        existingSchool.setAddress(schoolDetails.getAddress());
        existingSchool.setPhoneNumber(schoolDetails.getPhoneNumber());
        existingSchool.setEmail(schoolDetails.getEmail());
        existingSchool.setCity(schoolDetails.getCity());
        existingSchool.setState(schoolDetails.getState());
        existingSchool.setZipCode(schoolDetails.getZipCode());
        existingSchool.setLatitude(schoolDetails.getLatitude());
        existingSchool.setLongitude(schoolDetails.getLongitude());
        existingSchool.setTimezone(schoolDetails.getTimezone());
        existingSchool.setSchoolType(schoolDetails.getSchoolType());
        existingSchool.setWebsite(schoolDetails.getWebsite());
        existingSchool.setPrincipalContact(schoolDetails.getPrincipalContact());
        existingSchool.setStartTime(schoolDetails.getStartTime());
        existingSchool.setEndTime(schoolDetails.getEndTime());
        existingSchool.setEstablishedDate(schoolDetails.getEstablishedDate());
        existingSchool.setIsActive(schoolDetails.getIsActive());
        
        return schoolRepository.save(existingSchool);
    }
    
    public void deleteById(Long id) {
        throw new UnsupportedOperationException("School deletion is not supported. Only editing is allowed");
    }
    
    /**
     * Add a driver to a school by creating a mapping in the master database
     * 
     * @param schoolId The ID of the school to add the driver to
     * @param driverId The ID of the driver (user) from the master database
     * @return The SchoolDriverMapping entity
     * @throws ResourceNotFoundException if the school or driver is not found
     */
    @Transactional("masterTransactionManager")
    public SchoolDriverMapping addDriverToSchool(Long schoolId, Long driverId) {
        Long effectiveSchoolId = SecurityUtils.requireEffectiveSchoolId(schoolId);

        // Get current tenant ID
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("No tenant context available");
        }
        
        // Validate that the school exists in the tenant database
        School school = schoolRepository.findById(effectiveSchoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", effectiveSchoolId.toString()));
        
        // Get tenant from master database
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "tenantId", tenantId));
        
        // Validate that the driver exists in master database
        User driver = masterUserRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId.toString()));

        boolean hasRiderRole = driver.getUserRoles() != null && driver.getUserRoles().stream()
            .anyMatch(userRole -> userRole.getRole() != null && "RIDER".equalsIgnoreCase(userRole.getRole().getName()));

        if (!hasRiderRole) {
            throw new IllegalArgumentException("Selected user is not a driver. RIDER role is required");
        }
        
        // Check if a mapping already exists (active or inactive)
        Optional<SchoolDriverMapping> existingMapping = schoolDriverMappingRepository
            .findByTenantIdAndSchoolIdAndDriverId(tenantId, effectiveSchoolId, driverId);

        if (existingMapping.isPresent()) {
            SchoolDriverMapping mapping = existingMapping.get();

            if (Boolean.TRUE.equals(mapping.getIsActive())) {
                throw new IllegalArgumentException("Driver is already assigned to this school");
            }

            // Reactivate existing mapping instead of creating duplicate rows
            mapping.setIsActive(true);
            mapping.setAssignmentDate(LocalDateTime.now());
            mapping.setSchoolName(school.getName());
            return schoolDriverMappingRepository.save(mapping);
        }
        
        // Create the mapping
        SchoolDriverMapping mapping = new SchoolDriverMapping(driver, effectiveSchoolId, tenant, school.getName());
        return schoolDriverMappingRepository.save(mapping);
    }
    
    /**
     * List all drivers (users with RIDER role) assigned to a school
     * 
     * @param schoolId The ID of the school
     * @return List of driver users assigned to the school
     * @throws ResourceNotFoundException if the school is not found
     */
    @Transactional(readOnly = true)
    public List<DriverDetailsResponse> listDriversForSchool(Long schoolId) {
        // Get current tenant ID
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("No tenant context available");
        }
        
        // Validate that the school exists
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId.toString()));
        
        // Get all active driver mappings for this school from master database
        List<SchoolDriverMapping> mappings = schoolDriverMappingRepository
                .findAllActiveByTenantIdAndSchoolId(tenantId, schoolId);
        
        // Extract and return the driver users
        return mappings.stream()
                .map(SchoolDriverMapping::getDriver)
            .map(DriverDetailsResponse::fromUser)
                .collect(Collectors.toList());
    }
    
    /**
     * Remove a driver from a school by deactivating the mapping
     * 
     * @param schoolId The ID of the school
     * @param driverId The ID of the driver to remove
     * @throws ResourceNotFoundException if the school, driver, or mapping is not found
     */
    @Transactional("masterTransactionManager")
    public void removeDriverFromSchool(Long schoolId, Long driverId) {
        // Get current tenant ID
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("No tenant context available");
        }
        
        // Validate that the school exists
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId.toString()));

        // Prevent removing a driver that is currently assigned to a route
        List<Bus> busesWithAssignedRoute = busRepository.findAllByAssignedDriverIdAndAssignedRouteIsNotNull(driverId);
        if (!busesWithAssignedRoute.isEmpty()) {
            String routeNumber = busesWithAssignedRoute.get(0).getAssignedRoute() != null
                    ? busesWithAssignedRoute.get(0).getAssignedRoute().getRouteNumber()
                    : null;
            if (routeNumber != null) {
                throw new IllegalArgumentException("Cannot remove driver from school. Driver is assigned to route '" + routeNumber + "'. Please unassign the driver from the assigned route first.");
            }
            throw new IllegalArgumentException("Cannot remove driver from school. Driver is assigned to a route. Please unassign the driver from the assigned route first.");
        }
        
        // Find the active mapping
        SchoolDriverMapping mapping = schoolDriverMappingRepository
                .findActiveByTenantIdAndSchoolIdAndDriverId(tenantId, schoolId, driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver mapping", "driverId", driverId.toString()));
        
        // Deactivate the mapping
        mapping.setIsActive(false);
        schoolDriverMappingRepository.save(mapping);
    }
    
    /**
     * Get a specific driver assigned to a school
     * 
     * @param schoolId The ID of the school
     * @param driverId The ID of the driver
     * @return The driver user
     * @throws ResourceNotFoundException if the school or driver is not found
     */
    @Transactional(readOnly = true)
    public User getDriverForSchool(Long schoolId, Long driverId) {
        // Get current tenant ID
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("No tenant context available");
        }
        
        // Validate that the school exists
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId.toString()));
        
        // Find the active mapping
        SchoolDriverMapping mapping = schoolDriverMappingRepository
                .findActiveByTenantIdAndSchoolIdAndDriverId(tenantId, schoolId, driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId.toString()));
        
        return mapping.getDriver();
    }
}
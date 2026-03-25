package com.sqool.sqoolbus.master.service;

import com.sqool.sqoolbus.master.entity.ParentPupilMapping;
import com.sqool.sqoolbus.master.entity.SchoolDriverMapping;
import com.sqool.sqoolbus.master.entity.User;
import com.sqool.sqoolbus.master.repository.MasterUserRepository;
import com.sqool.sqoolbus.master.repository.ParentPupilMappingRepository;
import com.sqool.sqoolbus.master.repository.SchoolDriverMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for cross-tenant operations that retrieves pupil data from the master database mapping table.
 * This avoids the need to query across multiple tenant databases.
 */
@Service
@Transactional("masterTransactionManager")
public class CrossTenantService {
    
    private static final Logger logger = LoggerFactory.getLogger(CrossTenantService.class);
    
    @Autowired
    private ParentPupilMappingRepository parentPupilMappingRepository;
    
    @Autowired
    private MasterUserRepository masterUserRepository;

    @Autowired
    private SchoolDriverMappingRepository schoolDriverMappingRepository;

    /**
     * Get all pupils (kids) owned by a parent across all tenant databases
     * @param parentUsername The username of the parent
     * @return Map of tenant ID to list of pupils
     */
    public Map<String, List<PupilDTO>> getAllKidsForParent(String parentUsername) {
        logger.info("Fetching all kids for parent: {}", parentUsername);
        
        // Get parent user from master database
        User parentUser = masterUserRepository.findByUsername(parentUsername)
                .orElseThrow(() -> new RuntimeException("Parent user not found: " + parentUsername));
        
        return getAllKidsForParentById(parentUser.getId());
    }

    /**
     * Get all pupils (kids) owned by a parent from the master database mapping table
     * @param parentUserId The ID of the parent user
     * @return Map of tenant ID to list of pupils
     */
    public Map<String, List<PupilDTO>> getAllKidsForParentById(Long parentUserId) {
        logger.info("Fetching all kids for parent ID: {} from mapping table", parentUserId);
        
        // Verify parent exists in master database
        User parentUser = masterUserRepository.findById(parentUserId)
                .orElseThrow(() -> new RuntimeException("Parent user not found with ID: " + parentUserId));
        
        // Get all active pupil mappings for this parent
        List<ParentPupilMapping> mappings = parentPupilMappingRepository.findAllActiveByParentId(parentUserId);
        logger.info("Found {} active pupil mappings for parent ID: {}", mappings.size(), parentUserId);
        
        // Group mappings by tenant ID and convert to DTOs
        Map<String, List<PupilDTO>> allKids = mappings.stream()
            .collect(Collectors.groupingBy(
                mapping -> mapping.getTenant().getTenantId(),
                Collectors.mapping(this::mapMappingToPupilDTO, Collectors.toList())
            ));
        
        logger.info("Found total of {} kids across {} tenants for parent ID: {}", 
                    mappings.size(), allKids.size(), parentUserId);
        
        return allKids;
    }

        /**
         * Get all active students for a parent within a specific tenant.
         */
        public List<PupilDTO> getStudentsForParentInTenant(Long parentUserId, String tenantId) {
        logger.info("Fetching students for parent ID: {} in tenant: {}", parentUserId, tenantId);

        masterUserRepository.findById(parentUserId)
            .orElseThrow(() -> new RuntimeException("Parent user not found with ID: " + parentUserId));

        List<ParentPupilMapping> mappings = parentPupilMappingRepository
            .findAllActiveByParentIdAndTenantId(parentUserId, tenantId);

        return mappings.stream()
            .map(this::mapMappingToPupilDTO)
            .collect(Collectors.toList());
        }

        /**
         * Get all active students for a parent within a specific tenant and school.
         */
        public List<PupilDTO> getStudentsForParentInTenantBySchool(Long parentUserId, String tenantId, Long schoolId) {
        logger.info("Fetching students for parent ID: {} in tenant: {} and school: {}", parentUserId, tenantId, schoolId);

        masterUserRepository.findById(parentUserId)
            .orElseThrow(() -> new RuntimeException("Parent user not found with ID: " + parentUserId));

        List<ParentPupilMapping> mappings = parentPupilMappingRepository
            .findAllActiveByParentIdAndTenantIdAndSchoolId(parentUserId, tenantId, schoolId);

        return mappings.stream()
            .map(this::mapMappingToPupilDTO)
            .collect(Collectors.toList());
        }

        /**
         * Get all active students attached to a school within a specific tenant.
         */
        public List<PupilDTO> getStudentsForSchoolInTenant(String tenantId, Long schoolId) {
        logger.info("Fetching students for tenant: {} and school: {}", tenantId, schoolId);

        List<ParentPupilMapping> mappings = parentPupilMappingRepository
            .findAllActiveByTenantIdAndSchoolId(tenantId, schoolId);

        return mappings.stream()
            .map(this::mapMappingToPupilDTO)
            .collect(Collectors.toList());
        }

        /**
         * Get all schools assigned to a driver across all tenants.
         */
        public Map<String, List<DriverSchoolDTO>> getAllSchoolsForDriverById(Long driverUserId) {
        logger.info("Fetching all schools for driver ID: {} from mapping table", driverUserId);

        User driverUser = masterUserRepository.findById(driverUserId)
            .orElseThrow(() -> new RuntimeException("Driver user not found with ID: " + driverUserId));

        boolean hasRiderRole = driverUser.getUserRoles() != null && driverUser.getUserRoles().stream()
            .anyMatch(userRole -> userRole.getRole() != null &&
                "RIDER".equalsIgnoreCase(userRole.getRole().getName()));

        if (!hasRiderRole) {
            throw new RuntimeException("User is not a driver. RIDER role is required");
        }

        List<SchoolDriverMapping> mappings = schoolDriverMappingRepository.findAllActiveByDriverId(driverUserId);

        return mappings.stream()
            .collect(Collectors.groupingBy(
                mapping -> mapping.getTenant().getTenantId(),
                Collectors.mapping(this::mapSchoolDriverMappingToDTO, Collectors.toList())
            ));
        }

        /**
         * Get all schools assigned to a driver within a specific tenant.
         */
        public List<DriverSchoolDTO> getDriverSchoolsInTenant(Long driverUserId, String tenantId) {
        logger.info("Fetching schools for driver ID: {} in tenant: {}", driverUserId, tenantId);

        User driverUser = masterUserRepository.findById(driverUserId)
            .orElseThrow(() -> new RuntimeException("Driver user not found with ID: " + driverUserId));

        boolean hasRiderRole = driverUser.getUserRoles() != null && driverUser.getUserRoles().stream()
            .anyMatch(userRole -> userRole.getRole() != null &&
                "RIDER".equalsIgnoreCase(userRole.getRole().getName()));

        if (!hasRiderRole) {
            throw new RuntimeException("User is not a driver. RIDER role is required");
        }

        List<SchoolDriverMapping> mappings = schoolDriverMappingRepository
            .findAllActiveByDriverIdAndTenantId(driverUserId, tenantId);

        return mappings.stream()
            .map(this::mapSchoolDriverMappingToDTO)
            .collect(Collectors.toList());
        }

        private DriverSchoolDTO mapSchoolDriverMappingToDTO(SchoolDriverMapping mapping) {
        DriverSchoolDTO dto = new DriverSchoolDTO();
        dto.setMappingId(mapping.getId());
        dto.setDriverId(mapping.getDriver() != null ? mapping.getDriver().getId() : null);
        dto.setDriverUsername(mapping.getDriver() != null ? mapping.getDriver().getUsername() : null);
        dto.setDriverFullName(mapping.getDriver() != null ? mapping.getDriver().getFullName() : null);
        dto.setSchoolId(mapping.getSchoolId());
        dto.setSchoolName(mapping.getSchoolName());
        dto.setTenantId(mapping.getTenant() != null ? mapping.getTenant().getTenantId() : null);
        dto.setAssignmentDate(mapping.getAssignmentDate());
        dto.setActive(Boolean.TRUE.equals(mapping.getIsActive()));
        return dto;
        }

    /**
     * Convert a ParentPupilMapping entity to a PupilDTO
     */
    private PupilDTO mapMappingToPupilDTO(ParentPupilMapping mapping) {
        PupilDTO dto = new PupilDTO();
        
        dto.setId(mapping.getPupilId());
        dto.setFirstName(mapping.getPupilFirstName());
        dto.setLastName(mapping.getPupilLastName());
        dto.setStudentId(mapping.getStudentId());
        dto.setGradeLevel(mapping.getGradeLevel());
        dto.setSchoolId(mapping.getSchoolId());
        dto.setSchoolName(mapping.getSchoolName());
        dto.setRouteId(mapping.getRouteId());
        dto.setRouteName(mapping.getRouteName());
        dto.setActive(mapping.getIsActive());
        dto.setTenantId(mapping.getTenant().getTenantId());
        dto.setParentId(mapping.getParent() != null ? mapping.getParent().getId() : null);
        dto.setParentFirstName(mapping.getParent() != null ? mapping.getParent().getFirstName() : null);
        dto.setParentLastName(mapping.getParent() != null ? mapping.getParent().getLastName() : null);
        if (mapping.getParent() != null) {
            dto.setParentFullName(mapping.getParent().getFirstName() + " " + mapping.getParent().getLastName());
        }
        
        return dto;
    }

    /**
     * DTO for Pupil summary information stored in the mapping table
     */
    public static class PupilDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String studentId;
        private String gradeLevel;
        private boolean isActive;
        private String tenantId;
        private Long schoolId;
        private String schoolName;
        private Long routeId;
        private String routeName;
        private Long parentId;
        private String parentFirstName;
        private String parentLastName;
        private String parentFullName;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getGradeLevel() {
            return gradeLevel;
        }

        public void setGradeLevel(String gradeLevel) {
            this.gradeLevel = gradeLevel;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public Long getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(Long schoolId) {
            this.schoolId = schoolId;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public Long getRouteId() {
            return routeId;
        }

        public void setRouteId(Long routeId) {
            this.routeId = routeId;
        }

        public String getRouteName() {
            return routeName;
        }

        public void setRouteName(String routeName) {
            this.routeName = routeName;
        }

        public Long getParentId() {
            return parentId;
        }

        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }

        public String getParentFirstName() {
            return parentFirstName;
        }

        public void setParentFirstName(String parentFirstName) {
            this.parentFirstName = parentFirstName;
        }

        public String getParentLastName() {
            return parentLastName;
        }

        public void setParentLastName(String parentLastName) {
            this.parentLastName = parentLastName;
        }

        public String getParentFullName() {
            return parentFullName;
        }

        public void setParentFullName(String parentFullName) {
            this.parentFullName = parentFullName;
        }
    }

    public static class DriverSchoolDTO {
        private Long mappingId;
        private Long driverId;
        private String driverUsername;
        private String driverFullName;
        private Long schoolId;
        private String schoolName;
        private String tenantId;
        private java.time.LocalDateTime assignmentDate;
        private boolean isActive;

        public Long getMappingId() {
            return mappingId;
        }

        public void setMappingId(Long mappingId) {
            this.mappingId = mappingId;
        }

        public Long getDriverId() {
            return driverId;
        }

        public void setDriverId(Long driverId) {
            this.driverId = driverId;
        }

        public String getDriverUsername() {
            return driverUsername;
        }

        public void setDriverUsername(String driverUsername) {
            this.driverUsername = driverUsername;
        }

        public String getDriverFullName() {
            return driverFullName;
        }

        public void setDriverFullName(String driverFullName) {
            this.driverFullName = driverFullName;
        }

        public Long getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(Long schoolId) {
            this.schoolId = schoolId;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public java.time.LocalDateTime getAssignmentDate() {
            return assignmentDate;
        }

        public void setAssignmentDate(java.time.LocalDateTime assignmentDate) {
            this.assignmentDate = assignmentDate;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }
    }
}

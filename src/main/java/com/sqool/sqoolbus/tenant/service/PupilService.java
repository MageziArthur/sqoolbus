package com.sqool.sqoolbus.tenant.service;

import com.sqool.sqoolbus.config.multitenancy.TenantContext;
import com.sqool.sqoolbus.exception.ResourceNotFoundException;
import com.sqool.sqoolbus.master.entity.ParentPupilMapping;
import com.sqool.sqoolbus.master.entity.Tenant;
import com.sqool.sqoolbus.master.repository.MasterUserRepository;
import com.sqool.sqoolbus.master.repository.ParentPupilMappingRepository;
import com.sqool.sqoolbus.master.repository.TenantRepository;
import com.sqool.sqoolbus.security.SecurityUtils;
import com.sqool.sqoolbus.tenant.entity.hail.Pupil;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.repository.PupilRepository;
import com.sqool.sqoolbus.tenant.repository.UserRepository;
import com.sqool.sqoolbus.tenant.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing pupils
 */
@Service
@Transactional
public class PupilService {
    
    @Autowired
    private PupilRepository pupilRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private MasterUserRepository masterUserRepository;

    @Autowired
    private ParentPupilMappingRepository parentPupilMappingRepository;

    @Autowired
    private TenantRepository tenantRepository;
    
    public List<Pupil> findAll() {
        // Return all pupils for current tenant (tenant isolation handled at DB level)
        return pupilRepository.findAllWithRelationships();
    }
    
    public Optional<Pupil> findById(Long id) {
        return pupilRepository.findById(id);
    }
    
    public List<Pupil> findByParentId(Long parentId) {
        return pupilRepository.findByParentId(parentId);
    }
    
    public List<Pupil> findBySchoolId(Long schoolId) {
        return pupilRepository.findBySchoolId(schoolId);
    }
    
    public List<Pupil> findByGradeLevel(String grade) {
        return pupilRepository.findByGradeLevel(grade);
    }

    public boolean existsByStudentId(String studentId) {
        return pupilRepository.existsByStudentId(studentId);
    }
    
    public List<Pupil> findActiveStudents() {
        // Return all active pupils for current tenant (tenant isolation handled at DB level)
        return pupilRepository.findByIsActiveTrue();
    }
    
    public Pupil save(Pupil pupil) {
        return pupilRepository.save(pupil);
    }
    
    public Pupil update(Long id, Pupil pupilDetails) {
        Pupil existingPupil = pupilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pupil", "id", id.toString()));
        
        // Update basic fields that exist
        existingPupil.setFirstName(pupilDetails.getFirstName());
        existingPupil.setLastName(pupilDetails.getLastName());
        existingPupil.setMiddleName(pupilDetails.getMiddleName());
        existingPupil.setDateOfBirth(pupilDetails.getDateOfBirth());
        existingPupil.setGradeLevel(pupilDetails.getGradeLevel());
        existingPupil.setStudentId(pupilDetails.getStudentId());
        existingPupil.setGender(pupilDetails.getGender());
        existingPupil.setHomeAddress(pupilDetails.getHomeAddress());
        existingPupil.setCity(pupilDetails.getCity());
        existingPupil.setState(pupilDetails.getState());
        existingPupil.setZipCode(pupilDetails.getZipCode());
        existingPupil.setIsActive(pupilDetails.getIsActive());
        
        return pupilRepository.save(existingPupil);
    }
    
    public void deleteById(Long id) {
        if (!pupilRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pupil", "id", id.toString());
        }
        pupilRepository.deleteById(id);
    }
    
    public Pupil assignToParent(Long pupilId, Long parentId) {
        Pupil pupil = pupilRepository.findById(pupilId)
                .orElseThrow(() -> new ResourceNotFoundException("Pupil", "id", pupilId.toString()));
        
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent", "id", parentId.toString()));
        
        pupil.setParent(parent);
        return pupilRepository.save(pupil);
    }

    @Transactional("masterTransactionManager")
    public void assignParentFromMaster(Long parentId, Pupil pupil) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("No tenant context available");
        }

        Tenant tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "tenantId", tenantId));

        com.sqool.sqoolbus.master.entity.User parent = masterUserRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent", "id", parentId.toString()));

        boolean hasParentRole = parent.getUserRoles() != null && parent.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRole() != null &&
                        "PARENT".equalsIgnoreCase(userRole.getRole().getName()));

        if (!hasParentRole) {
            throw new IllegalArgumentException("Selected user is not a parent. PARENT role is required");
        }

        Long schoolId = pupil.getSchool() != null ? pupil.getSchool().getId() : null;
        if (schoolId == null) {
            throw new IllegalArgumentException("Student must be assigned to a school before assigning a parent");
        }

        String studentId = pupil.getStudentId();
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("Student ID is required before assigning a parent");
        }

        ParentPupilMapping existingMapping = parentPupilMappingRepository
                .findByParentIdAndTenantIdAndSchoolIdAndStudentId(parentId, tenantId, schoolId, studentId);

        if (existingMapping != null) {
            if (Boolean.TRUE.equals(existingMapping.getIsActive())) {
                throw new IllegalArgumentException("Parent is already assigned to this student");
            }

            existingMapping.setIsActive(true);
            existingMapping.setPupilFirstName(pupil.getFirstName());
            existingMapping.setPupilLastName(pupil.getLastName());
            existingMapping.setStudentId(pupil.getStudentId());
            existingMapping.setGradeLevel(pupil.getGradeLevel());
            existingMapping.setSchoolName(pupil.getSchool() != null ? pupil.getSchool().getName() : null);
            existingMapping.setSchoolId(pupil.getSchool() != null ? pupil.getSchool().getId() : null);
            existingMapping.setRouteName(pupil.getRoute() != null ? pupil.getRoute().getRouteName() : null);
            existingMapping.setRouteId(pupil.getRoute() != null ? pupil.getRoute().getId() : null);
            parentPupilMappingRepository.save(existingMapping);
            return;
        }

        ParentPupilMapping mapping = new ParentPupilMapping();
        mapping.setParent(parent);
        mapping.setPupilId(pupil.getId());
        mapping.setTenant(tenant);
        mapping.setPupilFirstName(pupil.getFirstName());
        mapping.setPupilLastName(pupil.getLastName());
        mapping.setStudentId(pupil.getStudentId());
        mapping.setGradeLevel(pupil.getGradeLevel());
        mapping.setSchoolName(pupil.getSchool() != null ? pupil.getSchool().getName() : null);
        mapping.setSchoolId(pupil.getSchool() != null ? pupil.getSchool().getId() : null);
        mapping.setRouteName(pupil.getRoute() != null ? pupil.getRoute().getRouteName() : null);
        mapping.setRouteId(pupil.getRoute() != null ? pupil.getRoute().getId() : null);
        mapping.setIsActive(true);

        parentPupilMappingRepository.save(mapping);
    }
    
    public long countActiveStudentsBySchool(Long schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId.toString()));
        return pupilRepository.countActiveBySchool(school);
    }
}
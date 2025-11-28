package com.sqool.sqoolbus.tenant.service;

import com.sqool.sqoolbus.exception.ResourceNotFoundException;
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
    
    public long countActiveStudentsBySchool(Long schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", schoolId.toString()));
        return pupilRepository.countActiveBySchool(school);
    }
}
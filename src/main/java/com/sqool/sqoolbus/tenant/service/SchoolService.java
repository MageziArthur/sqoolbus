package com.sqool.sqoolbus.tenant.service;

import com.sqool.sqoolbus.dto.CreateSchoolRequest;
import com.sqool.sqoolbus.exception.ResourceNotFoundException;
import com.sqool.sqoolbus.security.SecurityUtils;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import com.sqool.sqoolbus.tenant.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing schools
 */
@Service
@Transactional
public class SchoolService {
    
    @Autowired
    private SchoolRepository schoolRepository;
    
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
        return schoolRepository.save(school);
    }
    
    public School createSchool(CreateSchoolRequest request) {
        School school = new School();
        school.setName(request.getName());
        school.setCode(request.getCode());
        school.setAddress(request.getAddress());
        school.setSchoolType(request.getSchoolType());
        school.setPhoneNumber(request.getPhoneNumber());
        school.setEmail(request.getEmail());
        school.setWebsite(request.getWebsite());
        school.setPrincipalName(request.getPrincipalName());
        school.setPrincipalContact(request.getPrincipalContact());
        school.setCity(request.getCity());
        school.setState(request.getState());
        school.setZipCode(request.getZipCode());
        school.setCountry(request.getCountry() != null ? request.getCountry() : "USA");
        school.setLatitude(request.getLatitude());
        school.setLongitude(request.getLongitude());
        school.setTimezone(request.getTimezone() != null ? request.getTimezone() : "America/New_York");
        school.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        school.setStartTime(request.getStartTime());
        school.setEndTime(request.getEndTime());
        school.setEstablishedDate(request.getEstablishedDate());
        
        return schoolRepository.save(school);
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
        if (!schoolRepository.existsById(id)) {
            throw new ResourceNotFoundException("School", "id", id.toString());
        }
        schoolRepository.deleteById(id);
    }
}
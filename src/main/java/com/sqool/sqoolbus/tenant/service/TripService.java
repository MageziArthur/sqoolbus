package com.sqool.sqoolbus.tenant.service;

import com.sqool.sqoolbus.exception.BusinessValidationException;
import com.sqool.sqoolbus.exception.ResourceNotFoundException;
import com.sqool.sqoolbus.security.SecurityUtils;
import com.sqool.sqoolbus.tenant.entity.hail.Trip;
import com.sqool.sqoolbus.tenant.entity.hail.Route;
import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.repository.TripRepository;
import com.sqool.sqoolbus.tenant.repository.RouteRepository;
import com.sqool.sqoolbus.tenant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing trips
 */
@Service
@Transactional
public class TripService {
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private RouteRepository routeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Trip> findAll() {
        // If user is any admin, return all trips
        if (SecurityUtils.isAnyAdmin()) {
            return tripRepository.findAll();
        }
        
        // For school users, filter by their school
        Long schoolId = SecurityUtils.getCurrentUserSchoolId();
        if (schoolId != null) {
            return tripRepository.findBySchoolId(schoolId);
        }
        
        // If no school context, return empty list
        return List.of();
    }
    
    public Optional<Trip> findById(Long id) {
        return tripRepository.findById(id);
    }
    
    public List<Trip> findByRouteId(Long routeId) {
        return tripRepository.findByRouteId(routeId);
    }
    
    public List<Trip> findByDriverId(Long driverId) {
        return tripRepository.findByRiderId(driverId);
    }
    
    public List<Trip> findByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        
        return tripRepository.findByScheduledStartTimeBetween(startOfDay, endOfDay);
    }
    
    public List<Trip> findByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        return tripRepository.findByScheduledStartTimeBetween(start, end);
    }
    
    public List<Trip> findByStatus(String status) {
        return tripRepository.findByStatus(status);
    }
    
    public List<Trip> findActiveTrips() {
        return tripRepository.findActiveTrips();
    }
    
    public List<Trip> findScheduledTrips() {
        return tripRepository.findByStatus("SCHEDULED");
    }
    
    public List<Trip> findTodaysTrips() {
        return tripRepository.findTodaysTrips();
    }
    
    public List<Trip> findUpcomingTrips(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);
        
        return tripRepository.findByScheduledStartTimeBetween(now, future);
    }
    
    public Trip save(Trip trip) {
        return tripRepository.save(trip);
    }
    
    public Trip update(Long id, Trip tripDetails) {
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", id.toString()));
        
        // Update basic fields that exist in the entity
        existingTrip.setStatus(tripDetails.getStatus());
        existingTrip.setPassengerCount(tripDetails.getPassengerCount());
        
        return tripRepository.save(existingTrip);
    }
    
    public void deleteById(Long id) {
        if (!tripRepository.existsById(id)) {
            throw new ResourceNotFoundException("Trip", "id", id.toString());
        }
        tripRepository.deleteById(id);
    }
    
    public Trip startTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));
        
        trip.setStatus(Trip.TripStatus.IN_PROGRESS);
        return tripRepository.save(trip);
    }
    
    public Trip completeTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));
        
        trip.setStatus(Trip.TripStatus.COMPLETED);
        return tripRepository.save(trip);
    }
    
    public Trip cancelTrip(Long tripId, String reason) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));
        
        trip.setStatus(Trip.TripStatus.CANCELLED);
        return tripRepository.save(trip);
    }
    
    public Trip updatePassengerCount(Long tripId, Integer count) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));
        
        if (count < 0) {
            throw new BusinessValidationException("Passenger count cannot be negative");
        }
        
        trip.setPassengerCount(count);
        return tripRepository.save(trip);
    }
    
    public long countTripsByRoute(Long routeId) {
        return tripRepository.findByRouteId(routeId).size();
    }
    
    public long countTripsByDriver(Long driverId) {
        return tripRepository.findByRiderId(driverId).size();
    }
    
    public long countTripsByStatus(String status) {
        return tripRepository.countByStatus(status);
    }
}
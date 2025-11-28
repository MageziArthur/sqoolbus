package com.sqool.sqoolbus.tenant.service;

import com.sqool.sqoolbus.exception.ResourceNotFoundException;
import com.sqool.sqoolbus.security.SecurityUtils;
import com.sqool.sqoolbus.tenant.entity.hail.Route;
import com.sqool.sqoolbus.tenant.entity.hail.Pupil;
import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.repository.RouteRepository;
import com.sqool.sqoolbus.tenant.repository.PupilRepository;
import com.sqool.sqoolbus.tenant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing routes
 */
@Service
@Transactional
public class RouteService {
    
    @Autowired
    private RouteRepository routeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PupilRepository pupilRepository;
    
    public List<Route> findAll() {
        // Return all routes for current tenant (tenant isolation handled at DB level)
        return routeRepository.findAll();
    }
    
    public Optional<Route> findById(Long id) {
        return routeRepository.findById(id);
    }
    
    public Optional<Route> findByRouteNumber(String routeNumber) {
        // Assuming route has a 'name' field that serves as route number
        return routeRepository.findByRouteName(routeNumber);
    }
    
    public List<Route> findBySchoolId(Long schoolId) {
        return routeRepository.findBySchoolId(schoolId);
    }
    
    public List<Route> findActiveRoutes() {
        return routeRepository.findByStatus(Route.RouteStatus.ACTIVE);
    }
    
    public List<Route> findByType(String routeType) {
        return routeRepository.findByRouteType(routeType);
    }
    
    public Route save(Route route) {
        return routeRepository.save(route);
    }
    
    public Route update(Long id, Route routeDetails) {
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id.toString()));
        
        // Update basic fields that exist
        existingRoute.setRouteName(routeDetails.getRouteName());
        existingRoute.setDescription(routeDetails.getDescription());
        existingRoute.setRouteType(routeDetails.getRouteType());
        existingRoute.setStartTime(routeDetails.getStartTime());
        existingRoute.setEndTime(routeDetails.getEndTime());
        existingRoute.setEstimatedDurationMinutes(routeDetails.getEstimatedDurationMinutes());
        existingRoute.setDistanceKm(routeDetails.getDistanceKm());
        existingRoute.setMaxCapacity(routeDetails.getMaxCapacity());
        existingRoute.setStatus(routeDetails.getStatus());
        
        if (routeDetails.getStartLatitude() != null && routeDetails.getStartLongitude() != null) {
            existingRoute.setStartLatitude(routeDetails.getStartLatitude());
            existingRoute.setStartLongitude(routeDetails.getStartLongitude());
        }
        
        if (routeDetails.getEndLatitude() != null && routeDetails.getEndLongitude() != null) {
            existingRoute.setEndLatitude(routeDetails.getEndLatitude());
            existingRoute.setEndLongitude(routeDetails.getEndLongitude());
        }
        
        return routeRepository.save(existingRoute);
    }
    
    public void deleteById(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Route", "id", id.toString());
        }
        routeRepository.deleteById(id);
    }
    
    public Route assignRider(Long routeId, Long riderId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId.toString()));
        
        User rider = userRepository.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", riderId.toString()));
        
        // Set the driver ID on the route
        // Note: Driver assignment would need a proper driver-route relationship entity
        // route.setDriverId(riderId); // Removed - no direct driver relationship in Route entity
        return routeRepository.save(route);
    }
    
    public Route removeRider(Long routeId, Long riderId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId.toString()));
        
        // Remove the driver assignment
        // Note: Driver assignment would need a proper driver-route relationship entity
        // route.setDriverId(null); // Removed - no direct driver relationship in Route entity
        return routeRepository.save(route);
    }
    
    public Route assignMultiplePupils(Long routeId, List<Long> pupilIds) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId.toString()));
        
        for (Long pupilId : pupilIds) {
            Pupil pupil = pupilRepository.findById(pupilId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pupil", "id", pupilId.toString()));
            
            pupil.setRoute(route);
            pupil.setUsesBusService(true);
            pupilRepository.save(pupil);
        }
        
        // Refresh route to get updated pupils list
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId.toString()));
    }
    
    public Route removeMultiplePupils(Long routeId, List<Long> pupilIds) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId.toString()));
        
        for (Long pupilId : pupilIds) {
            Pupil pupil = pupilRepository.findById(pupilId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pupil", "id", pupilId.toString()));
            
            // Only remove if the pupil is actually assigned to this route
            if (pupil.getRoute() != null && pupil.getRoute().getId().equals(routeId)) {
                pupil.setRoute(null);
                pupil.setUsesBusService(false);
                pupilRepository.save(pupil);
            }
        }
        
        // Refresh route to get updated pupils list
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId.toString()));
    }
}
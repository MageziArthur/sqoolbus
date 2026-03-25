package com.sqool.sqoolbus.tenant.service;

import com.sqool.sqoolbus.dto.TripDetailResponse;
import com.sqool.sqoolbus.exception.BusinessValidationException;
import com.sqool.sqoolbus.exception.ResourceNotFoundException;
import com.sqool.sqoolbus.security.SecurityUtils;
import com.sqool.sqoolbus.tenant.entity.hail.Bus;
import com.sqool.sqoolbus.tenant.entity.hail.Pupil;
import com.sqool.sqoolbus.tenant.entity.hail.TripStudentCheckout;
import com.sqool.sqoolbus.tenant.entity.hail.TripStudentCheckin;
import com.sqool.sqoolbus.tenant.entity.hail.TripTrailPoint;
import com.sqool.sqoolbus.tenant.entity.hail.Trip;
import com.sqool.sqoolbus.tenant.entity.hail.Route;
import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.repository.BusRepository;
import com.sqool.sqoolbus.tenant.repository.PupilRepository;
import com.sqool.sqoolbus.tenant.repository.TripStudentCheckoutRepository;
import com.sqool.sqoolbus.tenant.repository.TripStudentCheckinRepository;
import com.sqool.sqoolbus.tenant.repository.TripTrailPointRepository;
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
import java.util.stream.Collectors;

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

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private PupilRepository pupilRepository;

    @Autowired
    private TripTrailPointRepository tripTrailPointRepository;

    @Autowired
    private TripStudentCheckoutRepository tripStudentCheckoutRepository;

    @Autowired
    private TripStudentCheckinRepository tripStudentCheckinRepository;
    
    public List<Trip> findAll() {
        // Return all trips for current tenant (tenant isolation handled at DB level)
        return tripRepository.findAll();
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
        
        if (tripDetails.getStatus() != null) {
            existingTrip.setStatus(tripDetails.getStatus());
        }

        if (tripDetails.getTripType() != null) {
            existingTrip.setTripType(tripDetails.getTripType());
        }

        if (tripDetails.getPlannedStartTime() != null) {
            existingTrip.setPlannedStartTime(tripDetails.getPlannedStartTime());
        }

        if (tripDetails.getPlannedEndTime() != null) {
            existingTrip.setPlannedEndTime(tripDetails.getPlannedEndTime());
        }

        if (tripDetails.getPassengerCount() != null) {
            existingTrip.setPassengerCount(tripDetails.getPassengerCount());
        }

        if (tripDetails.getPickedUpCount() != null) {
            existingTrip.setPickedUpCount(tripDetails.getPickedUpCount());
        }

        if (tripDetails.getDroppedOffCount() != null) {
            existingTrip.setDroppedOffCount(tripDetails.getDroppedOffCount());
        }

        if (tripDetails.getStartLocation() != null) {
            existingTrip.setStartLocation(tripDetails.getStartLocation());
        }

        if (tripDetails.getEndLocation() != null) {
            existingTrip.setEndLocation(tripDetails.getEndLocation());
        }

        if (tripDetails.getNotes() != null) {
            existingTrip.setNotes(tripDetails.getNotes());
        }

        if (tripDetails.getDelayReason() != null) {
            existingTrip.setDelayReason(tripDetails.getDelayReason());
        }
        
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

    public Trip startDriverTrip(Long routeId,
                                Long busId,
                                Trip.TripType tripType,
                                LocalDateTime plannedStartTime,
                                LocalDateTime plannedEndTime,
                                List<Long> checkedInPupilIds) {
        User rider = getCurrentAuthenticatedRider();

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId.toString()));

        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", busId.toString()));

        if (bus.getAssignedDriver() == null || !bus.getAssignedDriver().getId().equals(rider.getId())) {
            throw new BusinessValidationException("Selected bus is not assigned to the current driver");
        }

        if (bus.getAssignedRoute() == null || !bus.getAssignedRoute().getId().equals(routeId)) {
            throw new BusinessValidationException("Selected bus is not assigned to the selected route");
        }

        Trip trip = new Trip();
        trip.setRoute(route);
        trip.setRider(rider);
        trip.setTripType(tripType != null ? tripType : Trip.TripType.PICKUP);

        if (plannedStartTime != null) {
            trip.setPlannedStartTime(plannedStartTime);
        }
        if (plannedEndTime != null) {
            trip.setPlannedEndTime(plannedEndTime);
        }

        trip.generateTripNumber();
        trip.startTrip();

        if (checkedInPupilIds != null && !checkedInPupilIds.isEmpty()) {
            int checkedInCount = validatePupilsBelongToRoute(routeId, checkedInPupilIds);
            trip.setPassengerCount(checkedInCount);
            trip.setPickedUpCount(checkedInCount);
        }

        trip.setNotes(buildTripNote("Started with bus " + bus.getBusNumber(), null, trip.getNotes()));

        Trip savedTrip = tripRepository.save(trip);

        if (checkedInPupilIds != null && !checkedInPupilIds.isEmpty()) {
            recordCheckins(savedTrip, checkedInPupilIds);
        }

        return savedTrip;
    }

    public Trip checkInPupil(Long tripId, Long pupilId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        ensureTripBelongsToCurrentRider(trip);
        ensureTripCanAcceptCheckIn(trip);

        Pupil pupil = pupilRepository.findById(pupilId)
                .orElseThrow(() -> new ResourceNotFoundException("Pupil", "id", pupilId.toString()));

        if (pupil.getRoute() == null || !pupil.getRoute().getId().equals(trip.getRoute().getId())) {
            throw new BusinessValidationException("Pupil is not assigned to this trip route");
        }

        if (tripStudentCheckinRepository.existsByTripIdAndPupilId(tripId, pupilId)) {
            throw new BusinessValidationException("Pupil already checked in for this trip");
        }

        int currentPassenger = trip.getPassengerCount() != null ? trip.getPassengerCount() : 0;
        int currentPickedUp = trip.getPickedUpCount() != null ? trip.getPickedUpCount() : 0;

        trip.setPassengerCount(currentPassenger + 1);
        trip.setPickedUpCount(currentPickedUp + 1);

        tripStudentCheckinRepository.save(new TripStudentCheckin(trip, pupil, LocalDateTime.now()));

        return tripRepository.save(trip);
    }

    public Trip checkInPupilsBulk(Long tripId, List<Long> pupilIds) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        ensureTripBelongsToCurrentRider(trip);
        ensureTripCanAcceptCheckIn(trip);

        List<Long> uniquePupilIds = pupilIds.stream().distinct().collect(Collectors.toList());
        int checkedInCount = validatePupilsBelongToRoute(trip.getRoute().getId(), uniquePupilIds);

        boolean hasDuplicate = uniquePupilIds.stream()
            .anyMatch(pupilId -> tripStudentCheckinRepository.existsByTripIdAndPupilId(tripId, pupilId));
        if (hasDuplicate) {
            throw new BusinessValidationException("One or more pupils are already checked in for this trip");
        }

        int currentPassenger = trip.getPassengerCount() != null ? trip.getPassengerCount() : 0;
        int currentPickedUp = trip.getPickedUpCount() != null ? trip.getPickedUpCount() : 0;

        trip.setPassengerCount(currentPassenger + checkedInCount);
        trip.setPickedUpCount(currentPickedUp + checkedInCount);

        recordCheckins(trip, uniquePupilIds);

        return tripRepository.save(trip);
    }

    public Trip pauseTrip(Long tripId, String reason) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        ensureTripBelongsToCurrentRider(trip);

        if (trip.getStatus() != Trip.TripStatus.IN_PROGRESS) {
            throw new BusinessValidationException("Only IN_PROGRESS trips can be paused");
        }

        trip.setStatus(Trip.TripStatus.SUSPENDED);
        trip.setDelayReason(reason);
        trip.setNotes(buildTripNote("Trip paused", reason, trip.getNotes()));

        return tripRepository.save(trip);
    }

    public Trip resumeTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        ensureTripBelongsToCurrentRider(trip);

        if (trip.getStatus() != Trip.TripStatus.SUSPENDED) {
            throw new BusinessValidationException("Only SUSPENDED trips can be resumed");
        }

        trip.setStatus(Trip.TripStatus.IN_PROGRESS);
        trip.setNotes(buildTripNote("Trip resumed", null, trip.getNotes()));

        return tripRepository.save(trip);
    }

    public Trip stopTrip(Long tripId, String reason) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        ensureTripBelongsToCurrentRider(trip);

        if (trip.getStatus() != Trip.TripStatus.IN_PROGRESS && trip.getStatus() != Trip.TripStatus.SUSPENDED) {
            throw new BusinessValidationException("Only IN_PROGRESS or SUSPENDED trips can be stopped");
        }

        trip.endTrip();
        trip.setNotes(buildTripNote("Trip stopped", reason, trip.getNotes()));

        return tripRepository.save(trip);
    }

    private User getCurrentAuthenticatedRider() {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null || username.isBlank()) {
            throw new BusinessValidationException("No authenticated driver found");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "username", username));

        boolean isRider = user.getRoles().stream().anyMatch(role -> "RIDER".equals(role.getName()));
        if (!isRider) {
            throw new BusinessValidationException("Authenticated user is not a driver");
        }

        return user;
    }

    private void ensureTripBelongsToCurrentRider(Trip trip) {
        User rider = getCurrentAuthenticatedRider();
        if (trip.getRider() == null || !trip.getRider().getId().equals(rider.getId())) {
            throw new BusinessValidationException("Trip does not belong to current driver");
        }
    }

    private void ensureTripCanAcceptCheckIn(Trip trip) {
        if (trip.getStatus() != Trip.TripStatus.IN_PROGRESS && trip.getStatus() != Trip.TripStatus.SUSPENDED) {
            throw new BusinessValidationException("Trip is not active for check-in");
        }
    }

    private int validatePupilsBelongToRoute(Long routeId, List<Long> pupilIds) {
        if (pupilIds == null || pupilIds.isEmpty()) {
            return 0;
        }

        List<Pupil> pupils = pupilIds.stream()
                .distinct()
                .map(pupilId -> pupilRepository.findById(pupilId)
                        .orElseThrow(() -> new ResourceNotFoundException("Pupil", "id", pupilId.toString())))
                .collect(Collectors.toList());

        boolean invalidRouteAssignment = pupils.stream()
                .anyMatch(pupil -> pupil.getRoute() == null || !pupil.getRoute().getId().equals(routeId));

        if (invalidRouteAssignment) {
            throw new BusinessValidationException("One or more pupils are not assigned to the selected route");
        }

        return pupils.size();
    }

    private String buildTripNote(String action, String reason, String currentNotes) {
        StringBuilder builder = new StringBuilder();
        if (currentNotes != null && !currentNotes.isBlank()) {
            builder.append(currentNotes).append("\n");
        }

        builder.append(LocalDateTime.now()).append(" - ").append(action);
        if (reason != null && !reason.isBlank()) {
            builder.append(". Reason: ").append(reason);
        }

        return builder.toString();
    }

    public TripTrailPoint recordTripTrailPoint(Long tripId, Double latitude, Double longitude, LocalDateTime recordedAt) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        ensureTripBelongsToCurrentRider(trip);
        ensureTripCanAcceptCheckIn(trip);

        if (latitude == null || longitude == null) {
            throw new BusinessValidationException("Latitude and longitude are required");
        }

        LocalDateTime pointTime = recordedAt != null ? recordedAt : LocalDateTime.now();

        TripTrailPoint point = new TripTrailPoint(trip, latitude, longitude, pointTime);

        trip.setCurrentLatitude(latitude);
        trip.setCurrentLongitude(longitude);

        return tripTrailPointRepository.save(point);
    }

    @Transactional(readOnly = true)
    public List<TripTrailPoint> getTripTrailHistory(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        ensureTripBelongsToCurrentRider(trip);

        return tripTrailPointRepository.findByTripIdOrderByRecordedAtAsc(tripId);
    }

    public TripStudentCheckout checkOutStudent(Long tripId,
                                               Long pupilId,
                                               Double latitude,
                                               Double longitude,
                                               String notes,
                                               LocalDateTime checkoutTime) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        ensureTripBelongsToCurrentRider(trip);
        ensureTripCanAcceptCheckIn(trip);

        Pupil pupil = pupilRepository.findById(pupilId)
                .orElseThrow(() -> new ResourceNotFoundException("Pupil", "id", pupilId.toString()));

        if (pupil.getRoute() == null || !pupil.getRoute().getId().equals(trip.getRoute().getId())) {
            throw new BusinessValidationException("Pupil is not assigned to this trip route");
        }

        if (tripStudentCheckoutRepository.existsByTripIdAndPupilId(tripId, pupilId)) {
            throw new BusinessValidationException("Pupil already checked out from this trip");
        }

        LocalDateTime eventTime = checkoutTime != null ? checkoutTime : LocalDateTime.now();
        TripStudentCheckout checkout = new TripStudentCheckout(trip, pupil, eventTime, latitude, longitude, notes);

        int currentDropped = trip.getDroppedOffCount() != null ? trip.getDroppedOffCount() : 0;
        int currentPassenger = trip.getPassengerCount() != null ? trip.getPassengerCount() : 0;

        trip.setDroppedOffCount(currentDropped + 1);
        trip.setPassengerCount(Math.max(0, currentPassenger - 1));

        if (latitude != null && longitude != null) {
            trip.setCurrentLatitude(latitude);
            trip.setCurrentLongitude(longitude);
        }

        tripRepository.save(trip);
        return tripStudentCheckoutRepository.save(checkout);
    }

    public List<TripStudentCheckout> checkOutStudentsBulk(Long tripId,
                                                          List<Long> pupilIds,
                                                          Double latitude,
                                                          Double longitude,
                                                          String notes,
                                                          LocalDateTime checkoutTime) {
        if (pupilIds == null || pupilIds.isEmpty()) {
            throw new BusinessValidationException("At least one pupil ID is required");
        }

        return pupilIds.stream()
                .distinct()
                .map(pupilId -> checkOutStudent(tripId, pupilId, latitude, longitude, notes, checkoutTime))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TripStudentCheckout> getTripStudentCheckouts(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        ensureTripBelongsToCurrentRider(trip);

        return tripStudentCheckoutRepository.findByTripIdOrderByCheckoutTimeAsc(tripId);
    }

    @Transactional(readOnly = true)
    public TripDetailResponse getTripDetail(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId.toString()));

        List<TripTrailPoint> realtimeLogs = tripTrailPointRepository.findByTripIdOrderByRecordedAtAsc(tripId);
        List<TripStudentCheckin> checkins = tripStudentCheckinRepository.findByTripIdOrderByCheckinTimeAsc(tripId);
        List<TripStudentCheckout> checkouts = tripStudentCheckoutRepository.findByTripIdOrderByCheckoutTimeAsc(tripId);

        return new TripDetailResponse(trip, realtimeLogs, checkins, checkouts);
    }

    private void recordCheckins(Trip trip, List<Long> pupilIds) {
        List<TripStudentCheckin> checkins = pupilIds.stream()
                .map(pupilId -> {
                    Pupil pupil = pupilRepository.findById(pupilId)
                            .orElseThrow(() -> new ResourceNotFoundException("Pupil", "id", pupilId.toString()));
                    return new TripStudentCheckin(trip, pupil, LocalDateTime.now());
                })
                .collect(Collectors.toList());

        tripStudentCheckinRepository.saveAll(checkins);
    }
}
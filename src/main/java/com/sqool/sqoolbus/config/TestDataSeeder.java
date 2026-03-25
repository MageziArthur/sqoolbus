package com.sqool.sqoolbus.config;

import com.sqool.sqoolbus.config.multitenancy.TenantContext;
import com.sqool.sqoolbus.master.entity.SchoolDriverMapping;
import com.sqool.sqoolbus.master.entity.Tenant;
import com.sqool.sqoolbus.master.entity.User;
import com.sqool.sqoolbus.master.entity.UserRole;
import com.sqool.sqoolbus.master.repository.MasterRoleRepository;
import com.sqool.sqoolbus.master.repository.MasterUserRepository;
import com.sqool.sqoolbus.master.repository.MasterUserRoleRepository;
import com.sqool.sqoolbus.master.repository.ParentPupilMappingRepository;
import com.sqool.sqoolbus.master.repository.SchoolDriverMappingRepository;
import com.sqool.sqoolbus.master.repository.TenantRepository;
import com.sqool.sqoolbus.tenant.entity.hail.Bus;
import com.sqool.sqoolbus.tenant.entity.hail.BusStatus;
import com.sqool.sqoolbus.tenant.entity.hail.Pupil;
import com.sqool.sqoolbus.tenant.entity.hail.Route;
import com.sqool.sqoolbus.tenant.entity.hail.School;
import com.sqool.sqoolbus.tenant.entity.hail.Trip;
import com.sqool.sqoolbus.tenant.entity.hail.TripTrailPoint;
import com.sqool.sqoolbus.tenant.entity.hail.TripStudentCheckin;
import com.sqool.sqoolbus.tenant.entity.hail.TripStudentCheckout;
import com.sqool.sqoolbus.tenant.repository.BusRepository;
import com.sqool.sqoolbus.tenant.repository.PupilRepository;
import com.sqool.sqoolbus.tenant.repository.RouteRepository;
import com.sqool.sqoolbus.tenant.repository.SchoolRepository;
import com.sqool.sqoolbus.tenant.repository.TripRepository;
import com.sqool.sqoolbus.tenant.repository.TripTrailPointRepository;
import com.sqool.sqoolbus.tenant.repository.TripStudentCheckinRepository;
import com.sqool.sqoolbus.tenant.repository.TripStudentCheckoutRepository;
import com.sqool.sqoolbus.tenant.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@DependsOn("databaseInitializer")
@ConditionalOnProperty(name = "sqoolbus.see_test_data", havingValue = "true")
public class TestDataSeeder implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(TestDataSeeder.class);

    private final MasterRoleRepository masterRoleRepository;
    private final MasterUserRepository masterUserRepository;
    private final MasterUserRoleRepository masterUserRoleRepository;
    private final SchoolDriverMappingRepository schoolDriverMappingRepository;
    private final ParentPupilMappingRepository parentPupilMappingRepository;
    private final TenantRepository tenantRepository;

    private final SchoolRepository schoolRepository;
    private final BusRepository busRepository;
    private final PupilRepository pupilRepository;
    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;
    private final TripTrailPointRepository tripTrailPointRepository;
    private final TripStudentCheckinRepository tripStudentCheckinRepository;
    private final TripStudentCheckoutRepository tripStudentCheckoutRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${sqoolbus.multitenancy.default-tenant}")
    private String defaultTenantId;

    public TestDataSeeder(
            MasterRoleRepository masterRoleRepository,
            MasterUserRepository masterUserRepository,
            MasterUserRoleRepository masterUserRoleRepository,
            SchoolDriverMappingRepository schoolDriverMappingRepository,
            ParentPupilMappingRepository parentPupilMappingRepository,
            TenantRepository tenantRepository,
            SchoolRepository schoolRepository,
            BusRepository busRepository,
            PupilRepository pupilRepository,
            RouteRepository routeRepository,
            TripRepository tripRepository,
            TripTrailPointRepository tripTrailPointRepository,
            TripStudentCheckinRepository tripStudentCheckinRepository,
            TripStudentCheckoutRepository tripStudentCheckoutRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.masterRoleRepository = masterRoleRepository;
        this.masterUserRepository = masterUserRepository;
        this.masterUserRoleRepository = masterUserRoleRepository;
        this.schoolDriverMappingRepository = schoolDriverMappingRepository;
        this.parentPupilMappingRepository = parentPupilMappingRepository;
        this.tenantRepository = tenantRepository;
        this.schoolRepository = schoolRepository;
        this.busRepository = busRepository;
        this.pupilRepository = pupilRepository;
        this.routeRepository = routeRepository;
        this.tripRepository = tripRepository;
        this.tripTrailPointRepository = tripTrailPointRepository;
        this.tripStudentCheckinRepository = tripStudentCheckinRepository;
        this.tripStudentCheckoutRepository = tripStudentCheckoutRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("see_test_data is enabled - starting test data seeding");

        String originalTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId(defaultTenantId);

            School school = getOrCreateSchool();
            seedMasterDriversAndMappings(school);

            seedBuses(school);
            seedPupils(school);
            seedTripsAndMetadata(school);
            seedMasterParentsAndMappings(school);

            logger.info("Test data seeding completed successfully");
        } catch (Exception exception) {
            logger.error("Failed to seed test data", exception);
        } finally {
            if (originalTenant == null) {
                TenantContext.clear();
            } else {
                TenantContext.setTenantId(originalTenant);
            }
        }
    }

    private School getOrCreateSchool() {
        Optional<School> defaultSchool = schoolRepository.findByName("Default School");
        if (defaultSchool.isPresent()) {
            return defaultSchool.get();
        }

        return schoolRepository.findByIsActiveTrue().stream().findFirst().orElseGet(() -> {
            School school = new School();
            school.setName("Default School");
            school.setCode("DEFAULT_SCHOOL");
            school.setSchoolType("MIXED");
            school.setAddress("123 Default Street");
            school.setCity("Default City");
            school.setState("DC");
            school.setZipCode("00000");
            school.setPhoneNumber("+10000000000");
            school.setEmail("admin@default.school");
            school.setPrincipalName("Default Principal");
            school.setPrincipalContact("+10000000000");
            school.setIsActive(true);
            return schoolRepository.save(school);
        });
    }

    private void seedMasterDriversAndMappings(School school) {
        com.sqool.sqoolbus.master.entity.Role riderRole = masterRoleRepository.findByName("RIDER")
                .orElseThrow(() -> new IllegalStateException("RIDER role not found in master database"));

        Tenant tenant = tenantRepository.findByTenantId(defaultTenantId)
                .orElseThrow(() -> new IllegalStateException("Tenant not found in master database: " + defaultTenantId));

        List<User> drivers = new ArrayList<>();

        for (int index = 1; index <= 3; index++) {
            int driverIndex = index;
            String username = "master_test_driver_" + driverIndex;
            String email = "master_test_driver_" + driverIndex + "@sqool.local";

            User driver = masterUserRepository.findByUsername(username)
                    .or(() -> masterUserRepository.findByEmail(email))
                    .orElseGet(() -> {
                        User newDriver = new User();
                        newDriver.setUsername(username);
                        newDriver.setEmail(email);
                        newDriver.setPasswordHash(passwordEncoder.encode("Test@123"));
                        newDriver.setFirstName("TestDriver" + driverIndex);
                        newDriver.setLastName("Seed");
                        newDriver.setIsActive(true);
                        newDriver.setIsEmailVerified(true);
                        return newDriver;
                    });

            driver = masterUserRepository.save(driver);

            boolean hasRiderRole = masterUserRoleRepository
                    .existsByUserIdAndRoleIdAndTenantIsNull(driver.getId(), riderRole.getId());
            if (!hasRiderRole) {
                UserRole userRole = new UserRole();
                userRole.setUser(driver);
                userRole.setRole(riderRole);
                userRole.setTenant(null);
                masterUserRoleRepository.save(userRole);
            }

            drivers.add(driver);
        }

        for (User driver : drivers) {
            boolean mappingExists = schoolDriverMappingRepository
                    .existsByTenantIdAndSchoolIdAndDriverId(defaultTenantId, school.getId(), driver.getId());

            if (!mappingExists) {
                SchoolDriverMapping mapping = new SchoolDriverMapping();
                mapping.setDriver(driver);
                mapping.setTenant(tenant);
                mapping.setSchoolId(school.getId());
                mapping.setSchoolName(school.getName());
                mapping.setIsActive(true);
                schoolDriverMappingRepository.save(mapping);
            }
        }
    }

    private void seedBuses(School school) {
        for (int index = 1; index <= 3; index++) {
            String busNumber = String.format("TEST-BUS-%03d", index);

            if (busRepository.findByBusNumber(busNumber).isPresent()) {
                continue;
            }

            Bus bus = new Bus();
            bus.setBusNumber(busNumber);
            bus.setLicensePlate(String.format("TST-%03d", index));
            bus.setVin(String.format("TESTVIN%012d", index));
            bus.setMake("Blue Bird");
            bus.setModel("Vision");
            bus.setYear(2022 + index);
            bus.setColor("Yellow");
            bus.setCapacity(40);
            bus.setFuelType("DIESEL");
            bus.setTransmissionType("AUTOMATIC");
            bus.setStatus(BusStatus.AVAILABLE);
            bus.setPurchaseDate(LocalDate.now().minusYears(1));
            bus.setCurrentMileage(10000.0 + (index * 1000));
            bus.setIsActive(true);
            bus.setSchool(school);

            busRepository.save(bus);
        }
    }

    private void seedPupils(School school) {
        for (int index = 1; index <= 8; index++) {
            String studentId = String.format("TEST-STU-%03d", index);

            if (pupilRepository.existsByStudentId(studentId)) {
                continue;
            }

            Pupil pupil = new Pupil();
            pupil.setFirstName("Student" + index);
            pupil.setLastName("Demo");
            pupil.setStudentId(studentId);
            pupil.setGradeLevel(String.valueOf((index % 6) + 1));
            pupil.setClassSection("A");
            pupil.setDateOfBirth(LocalDate.now().minusYears(8 + index));
            pupil.setGender(index % 2 == 0 ? "FEMALE" : "MALE");
            pupil.setHomeAddress(index + " Demo Street");
            pupil.setCity("Default City");
            pupil.setState("DC");
            pupil.setZipCode("00000");
            pupil.setParentContact("+1000000000" + index);
            pupil.setEmergencyContact("+1000000010" + index);
            pupil.setParentEmail("parent" + index + "@sqool.local");
            pupil.setEnrollmentDate(LocalDate.now().minusMonths(3));
            pupil.setUsesBusService(true);
            pupil.setIsActive(true);
            pupil.setSchool(school);

            pupilRepository.save(pupil);
        }
    }

    private void seedMasterParentsAndMappings(School school) {
        com.sqool.sqoolbus.master.entity.Role parentRole = masterRoleRepository.findByName("PARENT")
                .orElseThrow(() -> new IllegalStateException("PARENT role not found in master database"));

        Tenant tenant = tenantRepository.findByTenantId(defaultTenantId)
                .orElseThrow(() -> new IllegalStateException("Tenant not found in master database: " + defaultTenantId));

        List<User> parents = new ArrayList<>();

        for (int index = 1; index <= 3; index++) {
            int parentIndex = index;
            String username = "master_test_parent_" + parentIndex;
            String email = "master_test_parent_" + parentIndex + "@sqool.local";

            User parent = masterUserRepository.findByUsername(username)
                    .or(() -> masterUserRepository.findByEmail(email))
                    .orElseGet(() -> {
                        User newParent = new User();
                        newParent.setUsername(username);
                        newParent.setEmail(email);
                        newParent.setPasswordHash(passwordEncoder.encode("Test@123"));
                        newParent.setFirstName("TestParent" + parentIndex);
                        newParent.setLastName("Seed");
                        newParent.setIsActive(true);
                        newParent.setIsEmailVerified(true);
                        return newParent;
                    });

            parent = masterUserRepository.save(parent);

            boolean hasParentRole = masterUserRoleRepository
                    .existsByUserIdAndRoleIdAndTenantIsNull(parent.getId(), parentRole.getId());
            if (!hasParentRole) {
                UserRole userRole = new UserRole();
                userRole.setUser(parent);
                userRole.setRole(parentRole);
                userRole.setTenant(null);
                masterUserRoleRepository.save(userRole);
            }

            parents.add(parent);
        }

        List<Pupil> testPupils = pupilRepository.findBySchoolId(school.getId()).stream()
                .filter(pupil -> pupil.getStudentId() != null && pupil.getStudentId().startsWith("TEST-STU-"))
                .toList();

        for (int index = 0; index < testPupils.size(); index++) {
            Pupil pupil = testPupils.get(index);
            User parent = parents.get(index % parents.size());

            if (parentPupilMappingRepository.findByParentIdAndTenantIdAndPupilId(
                    parent.getId(), defaultTenantId, pupil.getId()) != null) {
                continue;
            }

            com.sqool.sqoolbus.master.entity.ParentPupilMapping mapping =
                    new com.sqool.sqoolbus.master.entity.ParentPupilMapping();
            mapping.setParent(parent);
            mapping.setTenant(tenant);
            mapping.setPupilId(pupil.getId());
            mapping.setPupilFirstName(pupil.getFirstName());
            mapping.setPupilLastName(pupil.getLastName());
            mapping.setStudentId(pupil.getStudentId());
            mapping.setGradeLevel(pupil.getGradeLevel());
            mapping.setSchoolId(school.getId());
            mapping.setSchoolName(school.getName());
            mapping.setRouteId(pupil.getRoute() != null ? pupil.getRoute().getId() : null);
            mapping.setRouteName(pupil.getRoute() != null ? pupil.getRoute().getRouteName() : null);
            mapping.setIsActive(true);

            parentPupilMappingRepository.save(mapping);
        }
    }

    private void seedTripsAndMetadata(School school) {
        Route route = getOrCreateRoute(school);
        assignTestPupilsToRoute(route);

        com.sqool.sqoolbus.tenant.entity.User rider = userRepository.findByUsername("admin")
                .orElseGet(() -> userRepository.findAllActiveUsers().stream().findFirst()
                        .orElseThrow(() -> new IllegalStateException("No active tenant user found for trip seeding")));

        Trip completedTrip = getOrCreateTrip(
                "TEST-TRIP-001",
                route,
                rider,
                school,
                Trip.TripType.PICKUP,
                Trip.TripStatus.COMPLETED,
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().minusHours(2)
        );

        Trip inProgressTrip = getOrCreateTrip(
                "TEST-TRIP-002",
                route,
                rider,
                school,
                Trip.TripType.DROPOFF,
                Trip.TripStatus.IN_PROGRESS,
                LocalDateTime.now().minusMinutes(35),
                null
        );

        List<Pupil> routePupils = pupilRepository.findByRouteId(route.getId());
        seedTripMetadata(completedTrip, routePupils, true);
        seedTripMetadata(inProgressTrip, routePupils, false);
    }

    private Route getOrCreateRoute(School school) {
        return routeRepository.findActiveBySchoolId(school.getId()).stream().findFirst().orElseGet(() -> {
            Route route = new Route();
            route.setRouteNumber("TEST-ROUTE-001");
            route.setRouteName("Test Route 001");
            route.setDescription("Seeded route for trip test data");
            route.setRouteType(Route.RouteType.BOTH);
            route.setStartTime(LocalTime.of(7, 30));
            route.setEndTime(LocalTime.of(8, 15));
            route.setEstimatedDurationMinutes(45);
            route.setDistanceKm(12.0);
            route.setMaxCapacity(40);
            route.setStatus(Route.RouteStatus.ACTIVE);
            route.setSchool(school);
            route.setStartLocation("Default School");
            route.setEndLocation("Default School");
            route.setStartLatitude(39.7817);
            route.setStartLongitude(-89.6501);
            route.setEndLatitude(39.7817);
            route.setEndLongitude(-89.6501);
            return routeRepository.save(route);
        });
    }

    private void assignTestPupilsToRoute(Route route) {
        List<Pupil> pupils = pupilRepository.findBySchoolId(route.getSchool().getId()).stream()
                .filter(pupil -> pupil.getStudentId() != null && pupil.getStudentId().startsWith("TEST-STU-"))
                .limit(5)
                .toList();

        for (Pupil pupil : pupils) {
            if (pupil.getRoute() == null || !route.getId().equals(pupil.getRoute().getId())) {
                pupil.setRoute(route);
                pupil.setUsesBusService(true);
                pupilRepository.save(pupil);
            }
        }
    }

    private Trip getOrCreateTrip(
            String tripNumber,
            Route route,
            com.sqool.sqoolbus.tenant.entity.User rider,
            School school,
            Trip.TripType tripType,
            Trip.TripStatus status,
            LocalDateTime start,
            LocalDateTime end) {

        Optional<Trip> existing = tripRepository.findByTripNumber(tripNumber);
        if (existing.isPresent()) {
            return existing.get();
        }

        Trip trip = new Trip();
        trip.setTripNumber(tripNumber);
        trip.setTripDate(start);
        trip.setPlannedStartTime(start.minusMinutes(10));
        trip.setPlannedEndTime(end != null ? end.plusMinutes(5) : start.plusMinutes(50));
        trip.setActualStartTime(start);
        trip.setActualEndTime(end);
        trip.setTripType(tripType);
        trip.setStatus(status);
        trip.setRoute(route);
        trip.setRider(rider);
        trip.setSchool(school);
        trip.setStartLocation("Default School");
        trip.setEndLocation("Default School");
        trip.setStartLatitude(39.7817);
        trip.setStartLongitude(-89.6501);
        trip.setEndLatitude(39.7817);
        trip.setEndLongitude(-89.6501);
        trip.setCurrentLatitude(39.7820);
        trip.setCurrentLongitude(-89.6498);
        trip.setEstimatedDurationMinutes(45);
        trip.setActualDurationMinutes(end != null ? (int) java.time.Duration.between(start, end).toMinutes() : 0);
        trip.setDistanceTraveledKm(end != null ? 11.8 : 4.1);
        trip.setPassengerCount(5);
        trip.setPickedUpCount(end != null ? 5 : 3);
        trip.setDroppedOffCount(end != null ? 5 : 0);
        trip.setWeatherConditions("CLEAR");
        trip.setTrafficConditions("LIGHT");
        trip.setIncidentReported(false);
        trip.setEmergencyStops(0);

        return tripRepository.save(trip);
    }

    private void seedTripMetadata(Trip trip, List<Pupil> pupils, boolean completed) {
        if (tripTrailPointRepository.findByTripIdOrderByRecordedAtAsc(trip.getId()).isEmpty()) {
            LocalDateTime base = trip.getActualStartTime() != null ? trip.getActualStartTime() : LocalDateTime.now().minusMinutes(20);
            tripTrailPointRepository.save(new TripTrailPoint(trip, 39.7817, -89.6501, base.plusMinutes(2)));
            tripTrailPointRepository.save(new TripTrailPoint(trip, 39.7821, -89.6492, base.plusMinutes(10)));
            tripTrailPointRepository.save(new TripTrailPoint(trip, 39.7830, -89.6480, base.plusMinutes(18)));
        }

        List<Pupil> seedPupils = pupils.stream().limit(3).toList();
        for (int index = 0; index < seedPupils.size(); index++) {
            Pupil pupil = seedPupils.get(index);

            if (!tripStudentCheckinRepository.existsByTripIdAndPupilId(trip.getId(), pupil.getId())) {
                LocalDateTime checkinTime = (trip.getActualStartTime() != null ? trip.getActualStartTime() : LocalDateTime.now())
                        .plusMinutes(5 + index * 3L);
                tripStudentCheckinRepository.save(new TripStudentCheckin(trip, pupil, checkinTime));
            }

            if (completed && !tripStudentCheckoutRepository.existsByTripIdAndPupilId(trip.getId(), pupil.getId())) {
                LocalDateTime checkoutTime = (trip.getActualEndTime() != null ? trip.getActualEndTime() : LocalDateTime.now())
                        .minusMinutes(Math.max(0, 8 - (index * 2L)));
                tripStudentCheckoutRepository.save(new TripStudentCheckout(
                        trip,
                        pupil,
                        checkoutTime,
                        39.7835 + (index * 0.0004),
                        -89.6470 - (index * 0.0004),
                        "Seeded checkout"
                ));
            }
        }
    }
}

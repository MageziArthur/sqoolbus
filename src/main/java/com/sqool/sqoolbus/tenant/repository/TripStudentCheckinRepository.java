package com.sqool.sqoolbus.tenant.repository;

import com.sqool.sqoolbus.tenant.entity.hail.TripStudentCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripStudentCheckinRepository extends JpaRepository<TripStudentCheckin, Long> {
    List<TripStudentCheckin> findByTripIdOrderByCheckinTimeAsc(Long tripId);
    boolean existsByTripIdAndPupilId(Long tripId, Long pupilId);
}

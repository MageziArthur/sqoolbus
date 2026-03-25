package com.sqool.sqoolbus.tenant.repository;

import com.sqool.sqoolbus.tenant.entity.hail.TripTrailPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripTrailPointRepository extends JpaRepository<TripTrailPoint, Long> {

    List<TripTrailPoint> findByTripIdOrderByRecordedAtAsc(Long tripId);
}
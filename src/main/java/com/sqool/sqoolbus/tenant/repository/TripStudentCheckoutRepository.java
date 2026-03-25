package com.sqool.sqoolbus.tenant.repository;

import com.sqool.sqoolbus.tenant.entity.hail.TripStudentCheckout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripStudentCheckoutRepository extends JpaRepository<TripStudentCheckout, Long> {

    List<TripStudentCheckout> findByTripIdOrderByCheckoutTimeAsc(Long tripId);

    boolean existsByTripIdAndPupilId(Long tripId, Long pupilId);
}
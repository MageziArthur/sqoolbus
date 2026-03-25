package com.sqool.sqoolbus.dto;

import com.sqool.sqoolbus.tenant.entity.hail.Trip;
import com.sqool.sqoolbus.tenant.entity.hail.TripStudentCheckin;
import com.sqool.sqoolbus.tenant.entity.hail.TripStudentCheckout;
import com.sqool.sqoolbus.tenant.entity.hail.TripTrailPoint;

import java.util.List;

public class TripDetailResponse {
    private Trip trip;
    private List<TripTrailPoint> realtimeLogs;
    private List<TripStudentCheckin> checkedInStudents;
    private List<TripStudentCheckout> checkouts;

    public TripDetailResponse() {
    }

    public TripDetailResponse(Trip trip,
                              List<TripTrailPoint> realtimeLogs,
                              List<TripStudentCheckin> checkedInStudents,
                              List<TripStudentCheckout> checkouts) {
        this.trip = trip;
        this.realtimeLogs = realtimeLogs;
        this.checkedInStudents = checkedInStudents;
        this.checkouts = checkouts;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public List<TripTrailPoint> getRealtimeLogs() {
        return realtimeLogs;
    }

    public void setRealtimeLogs(List<TripTrailPoint> realtimeLogs) {
        this.realtimeLogs = realtimeLogs;
    }

    public List<TripStudentCheckin> getCheckedInStudents() {
        return checkedInStudents;
    }

    public void setCheckedInStudents(List<TripStudentCheckin> checkedInStudents) {
        this.checkedInStudents = checkedInStudents;
    }

    public List<TripStudentCheckout> getCheckouts() {
        return checkouts;
    }

    public void setCheckouts(List<TripStudentCheckout> checkouts) {
        this.checkouts = checkouts;
    }
}

package com.sqool.sqoolbus.dto;

import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.entity.hail.Bus;
import com.sqool.sqoolbus.tenant.entity.hail.Pupil;
import com.sqool.sqoolbus.tenant.entity.hail.Route;

import java.util.List;

public class RouteDetailsResponse {
    private Route route;
    private Bus assignedBus;
    private User assignedDriver;
    private List<Pupil> students;

    public RouteDetailsResponse() {
    }

    public RouteDetailsResponse(Route route, Bus assignedBus, User assignedDriver, List<Pupil> students) {
        this.route = route;
        this.assignedBus = assignedBus;
        this.assignedDriver = assignedDriver;
        this.students = students;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Bus getAssignedBus() {
        return assignedBus;
    }

    public void setAssignedBus(Bus assignedBus) {
        this.assignedBus = assignedBus;
    }

    public User getAssignedDriver() {
        return assignedDriver;
    }

    public void setAssignedDriver(User assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

    public List<Pupil> getStudents() {
        return students;
    }

    public void setStudents(List<Pupil> students) {
        this.students = students;
    }
}

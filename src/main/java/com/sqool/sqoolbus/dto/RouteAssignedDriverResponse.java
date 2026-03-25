package com.sqool.sqoolbus.dto;

import com.sqool.sqoolbus.tenant.entity.User;

public class RouteAssignedDriverResponse {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private Boolean isActive;
    private Long assignedBusId;
    private String assignedBusNumber;

    public RouteAssignedDriverResponse() {
    }

    public RouteAssignedDriverResponse(Long id, String username, String email, String firstName, String lastName,
                                       String fullName, Boolean isActive, Long assignedBusId, String assignedBusNumber) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.isActive = isActive;
        this.assignedBusId = assignedBusId;
        this.assignedBusNumber = assignedBusNumber;
    }

    public static RouteAssignedDriverResponse from(User user, Long assignedBusId, String assignedBusNumber) {
        String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " " +
                (user.getLastName() != null ? user.getLastName() : "")).trim();

        return new RouteAssignedDriverResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                fullName,
                user.getIsActive(),
                assignedBusId,
                assignedBusNumber
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Long getAssignedBusId() {
        return assignedBusId;
    }

    public void setAssignedBusId(Long assignedBusId) {
        this.assignedBusId = assignedBusId;
    }

    public String getAssignedBusNumber() {
        return assignedBusNumber;
    }

    public void setAssignedBusNumber(String assignedBusNumber) {
        this.assignedBusNumber = assignedBusNumber;
    }
}
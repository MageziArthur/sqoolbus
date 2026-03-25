package com.sqool.sqoolbus.master.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing the mapping between schools and drivers (users with RIDER role) 
 * in the master database. This allows drivers from the master database to be assigned 
 * to schools in different tenants.
 */
@Entity
@Table(name = "school_driver_mappings", indexes = {
    @Index(name = "idx_school_driver_mapping_driver_id", columnList = "driver_id"),
    @Index(name = "idx_school_driver_mapping_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_school_driver_mapping_school_id", columnList = "tenant_id, school_id")
})
public class SchoolDriverMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "assignment_date")
    private LocalDateTime assignmentDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (assignmentDate == null) {
            assignmentDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public SchoolDriverMapping() {
    }

    public SchoolDriverMapping(User driver, Long schoolId, Tenant tenant, String schoolName) {
        this.driver = driver;
        this.schoolId = schoolId;
        this.tenant = tenant;
        this.schoolName = schoolName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public LocalDateTime getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDateTime assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "SchoolDriverMapping{" +
                "id=" + id +
                ", driverId=" + (driver != null ? driver.getId() : null) +
                ", schoolId=" + schoolId +
                ", tenantId=" + (tenant != null ? tenant.getTenantId() : null) +
                ", schoolName='" + schoolName + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}

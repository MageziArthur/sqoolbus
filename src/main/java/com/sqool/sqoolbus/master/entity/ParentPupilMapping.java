package com.sqool.sqoolbus.master.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing the mapping between parents and their pupils across tenant databases.
 * This table in the master database stores a summary of pupil information to avoid
 * cross-tenant queries.
 */
@Entity
@Table(name = "parent_pupil_mappings", indexes = {
    @Index(name = "idx_parent_pupil_mapping_parent_id", columnList = "parent_id"),
    @Index(name = "idx_parent_pupil_mapping_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_parent_pupil_mapping_pupil_id", columnList = "tenant_id, pupil_id")
})
public class ParentPupilMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private User parent;

    @Column(name = "pupil_id", nullable = false)
    private Long pupilId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "pupil_first_name", nullable = false)
    private String pupilFirstName;

    @Column(name = "pupil_last_name", nullable = false)
    private String pupilLastName;

    @Column(name = "student_id", length = 100)
    private String studentId;

    @Column(name = "grade_level", length = 50)
    private String gradeLevel;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "school_id")
    private Long schoolId;

    @Column(name = "route_name")
    private String routeName;

    @Column(name = "route_id")
    private Long routeId;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public ParentPupilMapping() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public Long getPupilId() {
        return pupilId;
    }

    public void setPupilId(Long pupilId) {
        this.pupilId = pupilId;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getPupilFirstName() {
        return pupilFirstName;
    }

    public void setPupilFirstName(String pupilFirstName) {
        this.pupilFirstName = pupilFirstName;
    }

    public String getPupilLastName() {
        return pupilLastName;
    }

    public void setPupilLastName(String pupilLastName) {
        this.pupilLastName = pupilLastName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
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
}

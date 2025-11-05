package com.sqool.sqoolbus.master.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_roles")
public class UserRole {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    
    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by")
    private User grantedBy;
    
    @PrePersist
    protected void onCreate() {
        if (grantedAt == null) {
            grantedAt = LocalDateTime.now();
        }
    }
    
    // Constructors
    public UserRole() {}
    
    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }
    
    public UserRole(User user, Role role, Tenant tenant) {
        this.user = user;
        this.role = role;
        this.tenant = tenant;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Tenant getTenant() {
        return tenant;
    }
    
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
    
    public LocalDateTime getGrantedAt() {
        return grantedAt;
    }
    
    public void setGrantedAt(LocalDateTime grantedAt) {
        this.grantedAt = grantedAt;
    }
    
    public User getGrantedBy() {
        return grantedBy;
    }
    
    public void setGrantedBy(User grantedBy) {
        this.grantedBy = grantedBy;
    }
    
    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : null) +
                ", role=" + (role != null ? role.getName() : null) +
                ", tenant=" + (tenant != null ? tenant.getTenantId() : null) +
                ", grantedAt=" + grantedAt +
                '}';
    }
}
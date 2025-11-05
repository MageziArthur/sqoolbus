package com.sqool.sqoolbus.master.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "role_permissions")
public class RolePermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public RolePermission() {}
    
    public RolePermission(Role role, Permission permission) {
        this.role = role;
        this.permission = permission;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Permission getPermission() {
        return permission;
    }
    
    public void setPermission(Permission permission) {
        this.permission = permission;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "RolePermission{" +
                "id=" + id +
                ", role=" + (role != null ? role.getName() : null) +
                ", permission=" + (permission != null ? permission.getName() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}
package com.sqool.sqoolbus.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Custom Authentication implementation that includes school context
 */
public class SchoolAwareAuthentication extends UsernamePasswordAuthenticationToken {
    
    private final Long schoolId;
    private final String tenantId;
    
    public SchoolAwareAuthentication(Object principal, Object credentials, 
                                   Collection<? extends GrantedAuthority> authorities,
                                   Long schoolId, String tenantId) {
        super(principal, credentials, authorities);
        this.schoolId = schoolId;
        this.tenantId = tenantId;
    }
    
    public Long getSchoolId() {
        return schoolId;
    }
    
    public String getTenantId() {
        return tenantId;
    }
}
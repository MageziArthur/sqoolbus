package com.sqool.sqoolbus.config.multitenancy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(2)
public class TenantFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);
    
    // Paths that should be excluded from tenant filtering
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/swagger-ui",
        "/v3/api-docs",
        "/webjars",
        "/h2-console",
        "/error",
        "/favicon.ico",
        "/actuator"
    );
    
    @Value("${sqoolbus.multitenancy.tenant-header:X-Tenant-ID}")
    private String tenantHeader;
    
    @Value("${sqoolbus.multitenancy.default-tenant:default-sqool}")
    private String defaultTenant;
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        boolean shouldExclude = EXCLUDED_PATHS.stream().anyMatch(requestPath::startsWith);
        if (shouldExclude) {
            logger.debug("Excluding path from tenant filtering: {}", requestPath);
        }
        return shouldExclude;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Check if tenant context is already set by TenantDataSourceFilter
            String existingTenantId = TenantContext.getTenantId();
            
            if (StringUtils.isBlank(existingTenantId)) {
                // Only set tenant context if not already set
                String tenantId = extractTenantId(request);
                TenantContext.setTenantId(tenantId);
                logger.debug("Set tenant context to: {}", tenantId);
            } else {
                logger.debug("Tenant context already set to: {}", existingTenantId);
            }
            
            filterChain.doFilter(request, response);
        } finally {
            // Don't clear context here since TenantDataSourceFilter handles it
            // TenantContext.clear();
        }
    }
    
    private String extractTenantId(HttpServletRequest request) {
        // 1. Try to get tenant from header
        String tenantId = request.getHeader(tenantHeader);
        
        // 2. Try to get tenant from request parameter
        if (StringUtils.isBlank(tenantId)) {
            tenantId = request.getParameter("tenantId");
        }
        
        // 3. Try to extract tenant from subdomain
        if (StringUtils.isBlank(tenantId)) {
            tenantId = extractTenantFromSubdomain(request);
        }
        
        // 4. Try to extract tenant from path
        if (StringUtils.isBlank(tenantId)) {
            tenantId = extractTenantFromPath(request);
        }
        
        // 5. Use default tenant if none found
        if (StringUtils.isBlank(tenantId)) {
            tenantId = defaultTenant;
        }
        
        return tenantId;
    }
    
    private String extractTenantFromSubdomain(HttpServletRequest request) {
        String serverName = request.getServerName();
        if (serverName != null && serverName.contains(".")) {
            String[] parts = serverName.split("\\.");
            if (parts.length > 2) {
                return parts[0]; // Return subdomain as tenant
            }
        }
        return null;
    }
    
    private String extractTenantFromPath(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        if (requestPath != null && requestPath.startsWith("/tenant/")) {
            String[] pathParts = requestPath.split("/");
            if (pathParts.length > 2) {
                return pathParts[2]; // Extract tenant from /tenant/{tenantId}/...
            }
        }
        return null;
    }
}
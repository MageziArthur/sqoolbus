package com.sqool.sqoolbus.config.multitenancy;

import com.sqool.sqoolbus.service.TenantDataSourceService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(1)
public class TenantDataSourceFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantDataSourceFilter.class);
    
    private static final String TENANT_HEADER = "X-Tenant-ID";
    
    @Value("${sqoolbus.multitenancy.default-tenant}")
    private String defaultTenant;
    
    // Paths that don't require tenant validation (master database and public endpoints)
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/api/master",
        "/h2-console",
        "/error",
        "/favicon.ico",
        "/actuator",
        "/swagger-ui",
        "/v3/api-docs",
        "/webjars"
    );
    
    @Autowired
    private TenantDataSourceService tenantDataSourceService;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestPath = httpRequest.getRequestURI();
        logger.debug("Processing request for path: {}", requestPath);
        
        // Skip tenant validation for excluded paths
        if (isExcludedPath(requestPath)) {
            logger.debug("Skipping tenant validation for excluded path: {}", requestPath);
            chain.doFilter(request, response);
            return;
        }
        
        try {
            // Extract tenant ID from header
            String tenantId = httpRequest.getHeader(TENANT_HEADER);
            
            // Require tenant header for tenant APIs
            if (tenantId == null || tenantId.trim().isEmpty()) {
                logger.error("Missing required tenant header: {}", TENANT_HEADER);
                sendErrorResponse(httpResponse, HttpStatus.BAD_REQUEST, 
                    "Missing required header: " + TENANT_HEADER);
                return;
            }
            
            logger.debug("Tenant ID from header: {}", tenantId);
            
            // Validate tenant and get datasource
            if (!tenantDataSourceService.isTenantValid(tenantId)) {
                logger.error("Tenant not found or inactive: {}", tenantId);
                sendErrorResponse(httpResponse, HttpStatus.NOT_FOUND, 
                    "Tenant not found: " + tenantId);
                return;
            }
            
            // Get datasource for tenant
            DataSource dataSource = tenantDataSourceService.getDataSourceForTenant(tenantId);
            if (dataSource == null) {
                logger.error("Failed to initialize database connection for tenant: {}", tenantId);
                sendErrorResponse(httpResponse, HttpStatus.SERVICE_UNAVAILABLE, 
                    "Database service temporarily unavailable for tenant: " + tenantId);
                return;
            }
            
            // Set tenant context
            TenantContext.setTenantId(tenantId);
            logger.debug("Set tenant context: {}", tenantId);
            
            // Store datasource in request attributes for potential use
            httpRequest.setAttribute("tenantDataSource", dataSource);
            httpRequest.setAttribute("tenantId", tenantId);
            
            // Continue with the request
            chain.doFilter(request, response);
            
        } catch (Exception e) {
            logger.error("Error in tenant datasource filter", e);
            sendErrorResponse(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR, 
                "Internal server error during tenant resolution");
        } finally {
            // Always clear tenant context after request
            TenantContext.clear();
            logger.debug("Cleared tenant context after request");
        }
    }
    
    /**
     * Check if the request path should be excluded from tenant validation
     */
    private boolean isExcludedPath(String requestPath) {
        return EXCLUDED_PATHS.stream().anyMatch(requestPath::startsWith);
    }
    
    /**
     * Send error response to client
     */
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) 
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = String.format(
            "{\"success\": false, \"message\": \"%s\", \"timestamp\": \"%s\", \"status\": %d}",
            message,
            java.time.LocalDateTime.now().toString(),
            status.value()
        );
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("TenantDataSourceFilter initialized");
    }
    
    @Override
    public void destroy() {
        logger.info("TenantDataSourceFilter destroyed");
    }
}
package com.sqool.sqoolbus.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filter to check user permissions against API endpoint annotations
 */
@Component
public class PermissionAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(PermissionAuthorizationFilter.class);
    
    private final RequestMappingHandlerMapping handlerMapping;

    public PermissionAuthorizationFilter(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Get the handler method for this request
            HandlerExecutionChain handlerChain = handlerMapping.getHandler(request);
            
            if (handlerChain == null || !(handlerChain.getHandler() instanceof HandlerMethod)) {
                // No handler method found, continue with filter chain
                filterChain.doFilter(request, response);
                return;
            }

            HandlerMethod handlerMethod = (HandlerMethod) handlerChain.getHandler();
            Method method = handlerMethod.getMethod();
            Class<?> controllerClass = handlerMethod.getBeanType();

            // Check for RequirePermissions annotation on method first, then class
            RequirePermissions methodAnnotation = method.getAnnotation(RequirePermissions.class);
            RequirePermissions classAnnotation = controllerClass.getAnnotation(RequirePermissions.class);
            
            RequirePermissions activeAnnotation = methodAnnotation != null ? methodAnnotation : classAnnotation;
            
            if (activeAnnotation == null) {
                // No permission annotation found, continue with filter chain
                filterChain.doFilter(request, response);
                return;
            }

            // Check if user has required permissions
            if (!hasRequiredPermissions(activeAnnotation)) {
                log.warn("Access denied for user {} to endpoint {}: insufficient permissions", 
                        getCurrentUsername(), request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("""
                    {
                        "errorCode": "INSUFFICIENT_PERMISSIONS",
                        "message": "You do not have the required permissions to access this resource",
                        "status": 403,
                        "path": "%s"
                    }
                    """.formatted(request.getRequestURI()));
                return;
            }

            // User has required permissions, continue with filter chain
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("Error in permission authorization filter", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "errorCode": "AUTHORIZATION_ERROR",
                    "message": "An error occurred while checking permissions",
                    "status": 500
                }
                """);
        }
    }

    /**
     * Check if the current user has the required permissions
     */
    private boolean hasRequiredPermissions(RequirePermissions annotation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Check if user is SYSTEM_ADMIN or ADMIN (has all permissions)
        Set<String> userAuthorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        
        if (userAuthorities.contains("ROLE_ADMIN")) {
            log.debug("User {} has ADMIN role, granting access", getCurrentUsername());
            return true;
        }

        // Get required permissions from annotation
        Permission[] requiredPermissions = annotation.value();
        boolean requireAll = annotation.requireAll();

        if (requireAll) {
            // User must have ALL required permissions (AND logic)
            return Arrays.stream(requiredPermissions)
                    .allMatch(permission -> userAuthorities.contains(permission.getAuthority()));
        } else {
            // User must have at least ONE required permission (OR logic)
            return Arrays.stream(requiredPermissions)
                    .anyMatch(permission -> userAuthorities.contains(permission.getAuthority()));
        }
    }

    /**
     * Get the current authenticated username
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "anonymous";
    }

    /**
     * Only apply this filter to API endpoints
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip filter for static resources, actuator endpoints, and non-API paths
        return path.startsWith("/static/") || 
               path.startsWith("/actuator/") || 
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/swagger-ui.html") ||
               path.equals("/") ||
               (!path.startsWith("/api/"));
    }
}
package com.sqool.sqoolbus.security;

import com.sqool.sqoolbus.service.AuthService;
import com.sqool.sqoolbus.tenant.entity.User;
import com.sqool.sqoolbus.tenant.repository.UserRepository;
import com.sqool.sqoolbus.config.multitenancy.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter to validate tokens and set authentication context
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String token = getTokenFromRequest(request);
            
            if (token != null && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
                Long schoolId = jwtTokenProvider.getSchoolIdFromToken(token);
                Set<String> roles = jwtTokenProvider.getRolesFromToken(token);
                Set<String> permissions = jwtTokenProvider.getPermissionsFromToken(token);
                
                logger.debug("Extracted from token - Username: {}, TenantId: {}, SchoolId: {}, Roles: {}, Permissions: {}", 
                            username, tenantId, schoolId, roles, permissions);
                
                if (username == null || username.trim().isEmpty()) {
                    logger.warn("Token validation failed: username is null or empty");
                    filterChain.doFilter(request, response);
                    return;
                }
                
                // Set tenant context if available
                if (tenantId != null) {
                    TenantContext.setTenantId(tenantId);
                }
                
                // Create authorities from roles and permissions
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());
                        
                // Add permissions as authorities
                authorities.addAll(permissions.stream()
                        .map(permission -> new SimpleGrantedAuthority("PERM_" + permission))
                        .collect(Collectors.toList()));
                
                // Create custom authentication token with school context
                SchoolAwareAuthentication authentication = 
                        new SchoolAwareAuthentication(username, null, authorities, schoolId, tenantId);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                logger.debug("Set authentication for user: {} with roles: {} and permissions: {}", 
                            username, roles, permissions);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
package com.sqool.sqoolbus.config;

import com.sqool.sqoolbus.security.JwtAuthenticationFilter;
import com.sqool.sqoolbus.security.PermissionAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private PermissionAuthorizationFilter permissionAuthorizationFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - Master tenant authentication (for getting tokens)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/master/auth/**").permitAll()
                .requestMatchers("/api/otp/**").permitAll()
                .requestMatchers("/api/tenants/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                
                // Swagger UI endpoints (for development)
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                
                // All tenant API endpoints require authentication (Bearer token)
                // Specific permissions are handled by @RequirePermissions annotations on controller methods
                .requestMatchers("/api/schools/**").authenticated()
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/pupils/**").authenticated()
                .requestMatchers("/api/routes/**").authenticated()
                .requestMatchers("/api/trips/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions().disable()) // For H2 console
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(permissionAuthorizationFilter, JwtAuthenticationFilter.class);
        
        return http.build();
    }
}
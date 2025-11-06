package com.sqool.sqoolbus.controller;

import com.sqool.sqoolbus.dto.*;
import com.sqool.sqoolbus.tenant.entity.UserOtp;
import com.sqool.sqoolbus.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for OTP (One-Time Password) operations
 */
@Tag(name = "OTP Management", description = "Operations for generating and verifying OTPs for two-factor authentication")
@RestController
@RequestMapping("/api/otp")
public class OtpController {
    
    private static final Logger logger = LoggerFactory.getLogger(OtpController.class);
    
    @Autowired
    private OtpService otpService;
    
    /**
     * Generate OTP for user
     */
    @Operation(summary = "Generate OTP", description = "Generate OTP for two-factor authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OTP generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/generate")
    public ResponseEntity<com.sqool.sqoolbus.dto.ApiResponse<OtpResponse>> generateOtp(
            @Valid @RequestBody OtpGenerationRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {
        
        logger.info("OTP generation request for user: {} with type: {}", 
                   request.getUsername(), request.getOtpType());
        
        // Validate request
        if (bindingResult.hasErrors()) {
            logger.warn("OTP generation validation failed for user: {}", request.getUsername());
            return ResponseEntity.badRequest().body(
                com.sqool.sqoolbus.dto.ApiResponse.<OtpResponse>error("Validation failed")
            );
        }
        
        try {
            // Parse enums
            UserOtp.OtpType otpType;
            UserOtp.DeliveryMethod deliveryMethod;
            
            try {
                otpType = UserOtp.OtpType.valueOf(request.getOtpType().toUpperCase());
                deliveryMethod = UserOtp.DeliveryMethod.valueOf(request.getDeliveryMethod().toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid OTP type or delivery method: {} / {}", 
                           request.getOtpType(), request.getDeliveryMethod());
                return ResponseEntity.badRequest().body(
                    com.sqool.sqoolbus.dto.ApiResponse.error("Invalid OTP type or delivery method")
                );
            }
            
            // Get client information
            String clientIp = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            
            // Generate OTP
            OtpResponse otpResponse = otpService.generateOtp(
                request.getUsername(),
                otpType,
                deliveryMethod,
                request.getDeliveryDestination(),
                clientIp,
                userAgent
            );
            
            if (otpResponse.isSuccess()) {
                logger.info("OTP generated successfully for user: {}", request.getUsername());
                return ResponseEntity.ok(
                    com.sqool.sqoolbus.dto.ApiResponse.success("OTP generated successfully", otpResponse)
                );
            } else {
                logger.warn("OTP generation failed for user: {}, reason: {}", 
                           request.getUsername(), otpResponse.getMessage());
                return ResponseEntity.badRequest().body(
                    com.sqool.sqoolbus.dto.ApiResponse.error(otpResponse.getMessage(), otpResponse)
                );
            }
            
        } catch (Exception e) {
            logger.error("Unexpected error during OTP generation for user {}: {}", 
                        request.getUsername(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                com.sqool.sqoolbus.dto.ApiResponse.error("Internal server error occurred")
            );
        }
    }
    
    /**
     * Verify OTP
     */
    @Operation(summary = "Verify OTP", description = "Verify OTP for two-factor authentication")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or OTP"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired OTP"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/verify")
    public ResponseEntity<com.sqool.sqoolbus.dto.ApiResponse<OtpResponse>> verifyOtp(
            @Valid @RequestBody OtpVerificationRequest request,
            BindingResult bindingResult) {
        
        logger.info("OTP verification request for user: {} with type: {}", 
                   request.getUsername(), request.getOtpType());
        
        // Validate request
        if (bindingResult.hasErrors()) {
            logger.warn("OTP verification validation failed for user: {}", request.getUsername());
            return ResponseEntity.badRequest().body(
                com.sqool.sqoolbus.dto.ApiResponse.<OtpResponse>error("Validation failed")
            );
        }
        
        try {
            // Parse OTP type
            UserOtp.OtpType otpType;
            try {
                otpType = UserOtp.OtpType.valueOf(request.getOtpType().toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid OTP type: {}", request.getOtpType());
                return ResponseEntity.badRequest().body(
                    com.sqool.sqoolbus.dto.ApiResponse.error("Invalid OTP type")
                );
            }
            
            // Verify OTP
            OtpResponse otpResponse = otpService.verifyOtp(
                request.getUsername(),
                request.getOtpCode(),
                otpType
            );
            
            if (otpResponse.isSuccess()) {
                logger.info("OTP verified successfully for user: {}", request.getUsername());
                return ResponseEntity.ok(
                    com.sqool.sqoolbus.dto.ApiResponse.success("OTP verified successfully", otpResponse)
                );
            } else {
                logger.warn("OTP verification failed for user: {}, reason: {}", 
                           request.getUsername(), otpResponse.getMessage());
                
                // Return 401 for authentication-related failures
                if (otpResponse.getMessage().contains("Invalid") || 
                    otpResponse.getMessage().contains("expired") ||
                    otpResponse.getMessage().contains("exceeded")) {
                    
                    return ResponseEntity.status(401).body(
                        com.sqool.sqoolbus.dto.ApiResponse.error(otpResponse.getMessage(), otpResponse)
                    );
                } else {
                    return ResponseEntity.badRequest().body(
                        com.sqool.sqoolbus.dto.ApiResponse.error(otpResponse.getMessage(), otpResponse)
                    );
                }
            }
            
        } catch (Exception e) {
            logger.error("Unexpected error during OTP verification for user {}: {}", 
                        request.getUsername(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                com.sqool.sqoolbus.dto.ApiResponse.error("Internal server error occurred")
            );
        }
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
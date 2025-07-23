package com.pms.controller;

import com.pms.dto.request.LoginRequest;
import com.pms.dto.request.RegisterRequest;
import com.pms.dto.request.RefreshTokenRequest;
import com.pms.dto.request.ForgotPasswordRequest;
import com.pms.dto.request.ResetPasswordRequest;
import com.pms.dto.response.ApiResponse;
import com.pms.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Map<String, Object> authResponse = authService.authenticateUser(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
            );
            
            return ResponseEntity.ok(ApiResponse.success(authResponse));
            
        } catch (Exception e) {
            logger.error("Login failed for email: {}", loginRequest.getEmail(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("LOGIN_FAILED", e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            Map<String, Object> registerResponse = authService.registerUser(
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    registerRequest.getName(),
                    registerRequest.getNickname()
            );
            
            return ResponseEntity.ok(ApiResponse.success(registerResponse));
            
        } catch (Exception e) {
            logger.error("Registration failed for email: {}", registerRequest.getEmail(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("REGISTRATION_FAILED", e.getMessage()));
        }
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        try {
            Map<String, Object> tokenResponse = authService.refreshToken(refreshRequest.getRefreshToken());
            
            return ResponseEntity.ok(ApiResponse.success(tokenResponse));
            
        } catch (Exception e) {
            logger.error("Token refresh failed", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("TOKEN_REFRESH_FAILED", e.getMessage()));
        }
    }
    
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and invalidate tokens")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            authService.logout(token);
            
            return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
            
        } catch (Exception e) {
            logger.error("Logout failed", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("LOGOUT_FAILED", e.getMessage()));
        }
    }
    
    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Initiate password reset process")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotRequest) {
        try {
            boolean result = authService.forgotPassword(forgotRequest.getEmail());
            
            if (result) {
                return ResponseEntity.ok(ApiResponse.success(null, 
                        "If the email exists, a password reset link has been sent."));
            } else {
                return ResponseEntity.ok(ApiResponse.success(null, 
                        "If the email exists, a password reset link has been sent."));
            }
            
        } catch (Exception e) {
            logger.error("Forgot password failed for email: {}", forgotRequest.getEmail(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("FORGOT_PASSWORD_FAILED", e.getMessage()));
        }
    }
    
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using reset token")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetRequest) {
        try {
            boolean result = authService.resetPassword(resetRequest.getToken(), resetRequest.getNewPassword());
            
            if (result) {
                return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("INVALID_TOKEN", "Invalid or expired reset token"));
            }
            
        } catch (Exception e) {
            logger.error("Password reset failed", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("PASSWORD_RESET_FAILED", e.getMessage()));
        }
    }
    
    @GetMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verify user email using verification token")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        try {
            boolean result = authService.verifyEmail(token);
            
            if (result) {
                return ResponseEntity.ok(ApiResponse.success(null, "Email verified successfully"));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("INVALID_TOKEN", "Invalid or expired verification token"));
            }
            
        } catch (Exception e) {
            logger.error("Email verification failed for token: {}", token, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("EMAIL_VERIFICATION_FAILED", e.getMessage()));
        }
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 
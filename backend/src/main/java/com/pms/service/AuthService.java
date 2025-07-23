package com.pms.service;

import com.pms.entity.User;
import com.pms.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public Map<String, Object> authenticateUser(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = (User) authentication.getPrincipal();
        
        if (!user.getEmailVerified()) {
            throw new RuntimeException("Email not verified");
        }
        
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }
        
        // Generate tokens
        List<String> roles = new ArrayList<>();
        user.getRoles().forEach(role -> roles.add(role.getName()));
        
        String accessToken = tokenProvider.createAccessToken(user.getId(), user.getEmail(), roles);
        String refreshToken = tokenProvider.createRefreshToken(user.getId());
        
        // Store refresh token in Redis
        redisTemplate.opsForValue().set(
                "refresh_token:" + user.getId(),
                refreshToken,
                Duration.ofDays(7)
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("tokenType", "Bearer");
        response.put("expiresIn", 900); // 15 minutes
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("email", user.getEmail());
        userInfo.put("name", user.getName());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("avatarUrl", user.getAvatarUrl());
        userInfo.put("roles", roles);
        
        response.put("user", userInfo);
        
        return response;
    }
    
    public Map<String, Object> registerUser(String email, String password, String name, String nickname) {
        if (userService.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = userService.createUser(email, password, name, nickname);
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("email", user.getEmail());
        userInfo.put("name", user.getName());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("emailVerified", user.getEmailVerified());
        userInfo.put("createdAt", user.getCreatedAt());
        
        response.put("user", userInfo);
        response.put("message", "Registration successful. Please check your email for verification.");
        
        return response;
    }
    
    public Map<String, Object> refreshToken(String refreshToken) {
        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        Long userId = tokenProvider.getUserIdFromRefreshToken(refreshToken);
        
        // Check if refresh token exists in Redis
        String storedRefreshToken = redisTemplate.opsForValue().get("refresh_token:" + userId);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new RuntimeException("Refresh token not found or expired");
        }
        
        // Get user details
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Generate new tokens
        List<String> roles = new ArrayList<>();
        user.getRoles().forEach(role -> roles.add(role.getName()));
        
        String newAccessToken = tokenProvider.createAccessToken(user.getId(), user.getEmail(), roles);
        String newRefreshToken = tokenProvider.createRefreshToken(user.getId());
        
        // Update refresh token in Redis
        redisTemplate.opsForValue().set(
                "refresh_token:" + userId,
                newRefreshToken,
                Duration.ofDays(7)
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("refreshToken", newRefreshToken);
        response.put("tokenType", "Bearer");
        response.put("expiresIn", 900);
        
        return response;
    }
    
    public void logout(String accessToken) {
        if (accessToken != null && tokenProvider.validateToken(accessToken)) {
            Long userId = tokenProvider.getUserIdFromToken(accessToken);
            
            // Remove refresh token from Redis
            redisTemplate.delete("refresh_token:" + userId);
            
            // Add access token to blacklist
            long remainingTime = tokenProvider.getRemainingTime(accessToken);
            if (remainingTime > 0) {
                redisTemplate.opsForValue().set(
                        "blacklist:" + accessToken,
                        "true",
                        Duration.ofSeconds(remainingTime)
                );
            }
        }
    }
    
    public boolean forgotPassword(String email) {
        return userService.initiatePasswordReset(email);
    }
    
    public boolean resetPassword(String token, String newPassword) {
        return userService.resetPassword(token, newPassword);
    }
    
    public boolean verifyEmail(String token) {
        return userService.verifyEmail(token);
    }
} 
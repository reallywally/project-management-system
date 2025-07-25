package com.pms.security;

import com.pms.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserService userService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        // Reflection을 사용해서 private 필드에 Mock 주입
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "tokenProvider", jwtTokenProvider);
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "userService", userService);
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "redisTemplate", redisTemplate);
    }

    @Test
    void 유효한_JWT_토큰으로_인증_성공() throws ServletException, IOException {
        // Given
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;
        Long userId = 1L;
        
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(redisTemplate.hasKey("blacklist:" + token)).thenReturn(false);
        when(jwtTokenProvider.getUserIdFromToken(token)).thenReturn(userId);
        when(userService.loadUserByUserId(userId)).thenReturn(userDetails);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider).validateToken(token);
        verify(jwtTokenProvider).getUserIdFromToken(token);
        verify(userService).loadUserByUserId(userId);
    }

    @Test
    void Authorization_헤더가_없으면_인증_스킵() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider, never()).validateToken(any());
    }

    @Test
    void Bearer_접두사가_없으면_인증_스킵() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Basic token");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider, never()).validateToken(any());
    }

    @Test
    void 유효하지_않은_JWT_토큰으로_인증_실패() throws ServletException, IOException {
        // Given
        String token = "invalid.jwt.token";
        String authHeader = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider).validateToken(token);
        verify(jwtTokenProvider, never()).getUserIdFromToken(any());
    }

    @Test
    void 블랙리스트_토큰으로_인증_실패() throws ServletException, IOException {
        // Given
        String token = "blacklisted.jwt.token";
        String authHeader = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(redisTemplate.hasKey("blacklist:" + token)).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider).validateToken(token);
        verify(jwtTokenProvider, never()).getUserIdFromToken(any());
    }

    @Test
    void JWT_검증_중_예외_발생시_인증_실패() throws ServletException, IOException {
        // Given
        String token = "problematic.jwt.token";
        String authHeader = "Bearer " + token;
        
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtTokenProvider.validateToken(token)).thenThrow(new RuntimeException("JWT parsing error"));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
} 
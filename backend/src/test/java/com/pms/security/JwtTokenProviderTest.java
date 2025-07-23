package com.pms.security;

import com.pms.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private RedisTemplate<String, Object> redisTemplate;
    
    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        jwtTokenProvider = new JwtTokenProvider();
        // JWT 설정을 테스트용으로 초기화
        jwtTokenProvider.setSecret("test-secret-key-for-testing-purposes-only-256-bits-long");
        jwtTokenProvider.setRefreshSecret("test-refresh-secret-key-for-testing-purposes-only-256-bits-long");
        jwtTokenProvider.setTokenValidityInMilliseconds(60000L); // 1분
        jwtTokenProvider.setRefreshTokenValidityInMilliseconds(300000L); // 5분
        jwtTokenProvider.setRedisTemplate(redisTemplate);
        jwtTokenProvider.init();
    }

    @Test
    void 액세스_토큰_생성_및_검증_성공() {
        // Given
        String username = "test@example.com";
        Collection<? extends GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );

        // When
        String token = jwtTokenProvider.createToken(username, authorities);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(username, jwtTokenProvider.getUsername(token));
    }

    @Test
    void 리프레시_토큰_생성_및_검증_성공() {
        // Given
        String username = "test@example.com";

        // When
        String refreshToken = jwtTokenProvider.createRefreshToken(username);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        assertTrue(jwtTokenProvider.validateRefreshToken(refreshToken));
        assertEquals(username, jwtTokenProvider.getUsernameFromRefreshToken(refreshToken));
    }

    @Test
    void 권한_정보_추출_성공() {
        // Given
        String username = "test@example.com";
        Collection<? extends GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        // When
        String token = jwtTokenProvider.createToken(username, authorities);
        Collection<? extends GrantedAuthority> extractedAuthorities = jwtTokenProvider.getAuthorities(token);

        // Then
        assertThat(extractedAuthorities).hasSize(2);
        assertThat(extractedAuthorities).extracting("authority")
            .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void 잘못된_토큰_검증_실패() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    void 만료된_토큰_검증_실패() throws InterruptedException {
        // Given - 매우 짧은 유효시간으로 토큰 생성
        jwtTokenProvider.setTokenValidityInMilliseconds(1L); // 1ms
        jwtTokenProvider.init();
        
        String username = "test@example.com";
        Collection<? extends GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        
        String token = jwtTokenProvider.createToken(username, authorities);
        
        // When - 토큰이 만료될 때까지 대기
        Thread.sleep(10);
        
        // Then
        assertFalse(jwtTokenProvider.validateToken(token));
    }

    @Test
    void 토큰_블랙리스트_처리() {
        // Given
        String token = "sample.jwt.token";
        when(redisTemplate.hasKey("blacklist:" + token)).thenReturn(true);

        // When & Then
        assertFalse(jwtTokenProvider.validateToken(token));
    }

    @Test
    void 토큰_블랙리스트_추가() {
        // Given
        String token = "sample.jwt.token";
        long expiration = 3600000L; // 1시간

        // When
        jwtTokenProvider.addToBlacklist(token, expiration);

        // Then
        verify(redisTemplate).opsForValue();
    }
} 
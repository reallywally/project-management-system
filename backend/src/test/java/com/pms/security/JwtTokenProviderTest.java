package com.pms.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    
    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        
        // JWT 설정을 테스트용으로 초기화
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", 
            "test-secret-key-for-testing-purposes-only-must-be-256-bits-long-at-least");
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshSecret", 
            "test-refresh-secret-key-for-testing-purposes-only-must-be-256-bits-long");
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidityInSeconds", 3600); // 1시간
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenValidityInSeconds", 86400); // 24시간
    }

    @Test
    void 액세스_토큰_생성_및_검증_성공() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        List<String> roles = List.of("ROLE_USER");

        // When
        String token = jwtTokenProvider.createAccessToken(userId, email, roles);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
        assertEquals(email, jwtTokenProvider.getEmailFromToken(token));
        assertEquals(roles, jwtTokenProvider.getRolesFromToken(token));
    }

    @Test
    void 리프레시_토큰_생성_및_검증_성공() {
        // Given
        Long userId = 1L;

        // When
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        assertTrue(jwtTokenProvider.validateRefreshToken(refreshToken));
        assertEquals(userId, jwtTokenProvider.getUserIdFromRefreshToken(refreshToken));
    }

    @Test
    void 토큰에서_사용자_정보_추출_성공() {
        // Given
        Long userId = 123L;
        String email = "user@example.com";
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");

        // When
        String token = jwtTokenProvider.createAccessToken(userId, email, roles);

        // Then
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
        assertEquals(email, jwtTokenProvider.getEmailFromToken(token));
        assertEquals(roles, jwtTokenProvider.getRolesFromToken(token));
    }

    @Test
    void 잘못된_토큰_검증_실패() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    void 빈_토큰_검증_실패() {
        // Given
        String emptyToken = "";
        String nullToken = null;

        // When & Then
        assertFalse(jwtTokenProvider.validateToken(emptyToken));
        assertFalse(jwtTokenProvider.validateToken(nullToken));
    }

    @Test
    void 잘못된_리프레시_토큰_검증_실패() {
        // Given
        String invalidRefreshToken = "invalid.refresh.token";

        // When & Then
        assertFalse(jwtTokenProvider.validateRefreshToken(invalidRefreshToken));
    }

    @Test
    void 토큰_만료시간_확인() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        List<String> roles = List.of("ROLE_USER");

        // When
        String token = jwtTokenProvider.createAccessToken(userId, email, roles);
        long remainingTime = jwtTokenProvider.getRemainingTime(token);

        // Then
        assertThat(remainingTime).isGreaterThan(0);
        assertThat(remainingTime).isLessThanOrEqualTo(3600); // 1시간 이하
    }

    @Test
    void 토큰_만료_여부_확인() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        List<String> roles = List.of("ROLE_USER");

        // When
        String token = jwtTokenProvider.createAccessToken(userId, email, roles);

        // Then
        assertFalse(jwtTokenProvider.isTokenExpired(token));
    }

    @Test
    void 토큰_만료일_추출() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        List<String> roles = List.of("ROLE_USER");

        // When
        String token = jwtTokenProvider.createAccessToken(userId, email, roles);
        var expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);

        // Then
        assertThat(expirationDate).isNotNull();
        assertThat(expirationDate).isAfter(new java.util.Date());
    }

    @Test
    void 다른_키로_서명된_토큰_검증_실패() {
        // Given
        JwtTokenProvider anotherProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(anotherProvider, "jwtSecret", 
            "different-secret-key-for-testing-purposes-only-must-be-256-bits-long");
        ReflectionTestUtils.setField(anotherProvider, "refreshSecret", 
            "different-refresh-secret-key-for-testing-purposes-only-must-be-256-bits");
        ReflectionTestUtils.setField(anotherProvider, "accessTokenValidityInSeconds", 3600);
        ReflectionTestUtils.setField(anotherProvider, "refreshTokenValidityInSeconds", 86400);

        Long userId = 1L;
        String email = "test@example.com";
        List<String> roles = List.of("ROLE_USER");

        // When
        String token = anotherProvider.createAccessToken(userId, email, roles);

        // Then
        assertFalse(jwtTokenProvider.validateToken(token)); // 다른 키로 검증하면 실패
    }

    @Test
    void 액세스_토큰으로_리프레시_토큰_검증_실패() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        List<String> roles = List.of("ROLE_USER");

        // When
        String accessToken = jwtTokenProvider.createAccessToken(userId, email, roles);

        // Then
        assertFalse(jwtTokenProvider.validateRefreshToken(accessToken)); // 액세스 토큰을 리프레시 토큰으로 검증하면 실패
    }

    @Test
    void 리프레시_토큰으로_액세스_토큰_검증_실패() {
        // Given
        Long userId = 1L;

        // When
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        // Then
        assertFalse(jwtTokenProvider.validateToken(refreshToken)); // 리프레시 토큰을 액세스 토큰으로 검증하면 실패
    }
} 
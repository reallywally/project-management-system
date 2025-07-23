package com.pms.service;

import com.pms.TestDataFactory;
import com.pms.entity.Role;
import com.pms.entity.User;
import com.pms.repository.RoleRepository;
import com.pms.repository.UserRepository;
import com.pms.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = TestDataFactory.createTestRole("USER");
        testUser = TestDataFactory.createTestUser("test@example.com", "Test User", passwordEncoder);
        testUser.getRoles().add(userRole);
    }

    @Test
    void 로그인_성공() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            email, password, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.createToken(eq(email), any())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(email)).thenReturn("refresh-token");

        // When
        var result = authService.login(email, password);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(result.getUser().getEmail()).isEqualTo(email);
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).createToken(eq(email), any());
        verify(jwtTokenProvider).createRefreshToken(email);
    }

    @Test
    void 로그인_실패_잘못된_자격증명() {
        // Given
        String email = "test@example.com";
        String password = "wrongpassword";
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        assertThatThrownBy(() -> authService.login(email, password))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Invalid email or password");
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).createToken(anyString(), any());
    }

    @Test
    void 회원가입_성공() {
        // Given
        String email = "newuser@example.com";
        String password = "password123";
        String name = "New User";
        String nickname = "NewUser";
        
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(password)).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = authService.register(email, password, name, nickname);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com"); // mocked save returns testUser
        
        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendVerificationEmail(any(User.class), anyString());
    }

    @Test
    void 회원가입_실패_중복_이메일() {
        // Given
        String email = "existing@example.com";
        String password = "password123";
        String name = "User";
        
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(email, password, name, name))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Email already exists");
        
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void 토큰_갱신_성공() {
        // Given
        String refreshToken = "valid-refresh-token";
        String email = "test@example.com";
        
        when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromRefreshToken(refreshToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.createToken(eq(email), any())).thenReturn("new-access-token");
        when(jwtTokenProvider.createRefreshToken(email)).thenReturn("new-refresh-token");

        // When
        var result = authService.refreshToken(refreshToken);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
        
        verify(jwtTokenProvider).validateRefreshToken(refreshToken);
        verify(jwtTokenProvider).getUsernameFromRefreshToken(refreshToken);
        verify(jwtTokenProvider).createToken(eq(email), any());
        verify(jwtTokenProvider).createRefreshToken(email);
    }

    @Test
    void 토큰_갱신_실패_유효하지_않은_토큰() {
        // Given
        String invalidRefreshToken = "invalid-refresh-token";
        
        when(jwtTokenProvider.validateRefreshToken(invalidRefreshToken)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(invalidRefreshToken))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Invalid refresh token");
        
        verify(jwtTokenProvider).validateRefreshToken(invalidRefreshToken);
        verify(jwtTokenProvider, never()).getUsernameFromRefreshToken(anyString());
    }

    @Test
    void 로그아웃_성공() {
        // Given
        String accessToken = "valid-access-token";
        long expiration = 3600000L;
        
        when(jwtTokenProvider.getTokenExpiration(accessToken)).thenReturn(expiration);

        // When
        authService.logout(accessToken);

        // Then
        verify(jwtTokenProvider).getTokenExpiration(accessToken);
        verify(jwtTokenProvider).addToBlacklist(accessToken, expiration);
    }

    @Test
    void 이메일_인증_성공() {
        // Given
        String token = "verification-token";
        testUser.setEmailVerified(false);
        
        when(userRepository.findByEmailVerificationToken(token)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        authService.verifyEmail(token);

        // Then
        assertTrue(testUser.getEmailVerified());
        assertNull(testUser.getEmailVerificationToken());
        verify(userRepository).findByEmailVerificationToken(token);
        verify(userRepository).save(testUser);
    }

    @Test
    void 이메일_인증_실패_유효하지_않은_토큰() {
        // Given
        String invalidToken = "invalid-token";
        
        when(userRepository.findByEmailVerificationToken(invalidToken)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.verifyEmail(invalidToken))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Invalid verification token");
        
        verify(userRepository).findByEmailVerificationToken(invalidToken);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void 비밀번호_재설정_이메일_발송() {
        // Given
        String email = "test@example.com";
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        authService.forgotPassword(email);

        // Then
        assertThat(testUser.getPasswordResetToken()).isNotNull();
        assertThat(testUser.getPasswordResetTokenExpiry()).isNotNull();
        
        verify(userRepository).findByEmail(email);
        verify(userRepository).save(testUser);
        verify(emailService).sendPasswordResetEmail(eq(testUser), anyString());
    }

    @Test
    void 비밀번호_재설정_성공() {
        // Given
        String token = "reset-token";
        String newPassword = "newpassword123";
        testUser.setPasswordResetToken(token);
        testUser.setPasswordResetTokenExpiry(java.time.LocalDateTime.now().plusHours(1));
        
        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded-new-password");
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        authService.resetPassword(token, newPassword);

        // Then
        assertThat(testUser.getPassword()).isEqualTo("encoded-new-password");
        assertNull(testUser.getPasswordResetToken());
        assertNull(testUser.getPasswordResetTokenExpiry());
        
        verify(userRepository).findByPasswordResetToken(token);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testUser);
    }
} 
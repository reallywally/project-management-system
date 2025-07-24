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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    private UserService userService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthService authService;

    private TestDataFactory testDataFactory;

    @BeforeEach
    void setUp() {
        testDataFactory = new TestDataFactory();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void 로그인_성공() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        
        User mockUser = testDataFactory.createUser(email, "Test User", "testUser");
        mockUser.setEmailVerified(true);
        mockUser.setIsActive(true);
        
        Role userRole = testDataFactory.createRole("ROLE_USER");
        mockUser.setRoles(Set.of(userRole));

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(mockUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mockAuth);
        when(jwtTokenProvider.createAccessToken(anyLong(), anyString(), anyList()))
            .thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(anyLong()))
            .thenReturn("refresh-token");

        // When
        Map<String, Object> result = authService.authenticateUser(email, password);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("accessToken")).isEqualTo("access-token");
        assertThat(result.get("refreshToken")).isEqualTo("refresh-token");
        assertThat(result.get("tokenType")).isEqualTo("Bearer");
        
        Map<String, Object> userInfo = (Map<String, Object>) result.get("user");
        assertThat(userInfo.get("email")).isEqualTo(email);
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).createAccessToken(eq(mockUser.getId()), eq(email), any(List.class));
        verify(jwtTokenProvider).createRefreshToken(eq(mockUser.getId()));
        verify(valueOperations).set(eq("refresh_token:" + mockUser.getId()), eq("refresh-token"), any());
    }

    @Test
    void 로그인_실패_잘못된_자격증명() {
        // Given
        String email = "test@example.com";
        String password = "wrong-password";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        assertThatThrownBy(() -> authService.authenticateUser(email, password))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessage("Bad credentials");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void 로그인_실패_이메일_미인증() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        
        User mockUser = testDataFactory.createUser(email, "Test User", "testUser");
        mockUser.setEmailVerified(false); // 이메일 미인증

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(mockUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mockAuth);

        // When & Then
        assertThatThrownBy(() -> authService.authenticateUser(email, password))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Email not verified");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void 로그인_실패_계정_비활성화() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        
        User mockUser = testDataFactory.createUser(email, "Test User", "testUser");
        mockUser.setEmailVerified(true);
        mockUser.setIsActive(false); // 계정 비활성화

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(mockUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mockAuth);

        // When & Then
        assertThatThrownBy(() -> authService.authenticateUser(email, password))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Account is deactivated");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void 회원가입_성공() {
        // Given
        String email = "newuser@example.com";
        String password = "password123";
        String name = "New User";
        String nickname = "newuser";

        User mockUser = testDataFactory.createUser(email, name, nickname);

        when(userService.findByEmail(email)).thenReturn(Optional.empty());
        when(userService.createUser(email, password, name, nickname)).thenReturn(mockUser);

        // When
        Map<String, Object> result = authService.registerUser(email, password, name, nickname);

        // Then
        assertThat(result).isNotNull();
        
        Map<String, Object> userInfo = (Map<String, Object>) result.get("user");
        assertThat(userInfo.get("email")).isEqualTo(email);
        assertThat(userInfo.get("name")).isEqualTo(name);
        assertThat(userInfo.get("nickname")).isEqualTo(nickname);

        verify(userService).findByEmail(email);
        verify(userService).createUser(email, password, name, nickname);
    }

    @Test
    void 회원가입_실패_이메일_중복() {
        // Given
        String email = "existing@example.com";
        String password = "password123";
        String name = "New User";
        String nickname = "newuser";

        User existingUser = testDataFactory.createUser(email, "Existing User", "existing");
        when(userService.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThatThrownBy(() -> authService.registerUser(email, password, name, nickname))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Email already exists");

        verify(userService).findByEmail(email);
        verify(userService, never()).createUser(anyString(), anyString(), anyString(), anyString());
    }
} 
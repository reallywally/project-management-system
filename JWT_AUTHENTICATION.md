# JWT 인증 시스템 설계

## 1. JWT 인증 시스템 개요

본 프로젝트 관리 시스템은 JWT(JSON Web Token) 기반 Stateless 인증 방식을 사용합니다. Access Token과 Refresh Token을 활용한 이중 토큰 전략으로 보안성과 사용자 경험을 모두 고려한 설계입니다.

### 주요 특징
- **Stateless 인증**: 서버에서 세션을 관리하지 않음
- **이중 토큰 전략**: Access Token (단기) + Refresh Token (장기)
- **자동 토큰 갱신**: 사용자가 인지하지 못하는 seamless한 토큰 재발급
- **보안 강화**: Redis를 통한 Refresh Token 관리 및 블랙리스트 기능

## 2. 토큰 구조 및 설정

### 2.1 Access Token
```yaml
# JWT Header
{
  "alg": "HS256",
  "typ": "JWT"
}

# JWT Payload
{
  "sub": "1",                           # 사용자 ID
  "email": "user@example.com",          # 사용자 이메일
  "name": "홍길동",                      # 사용자 이름
  "roles": ["USER", "PROJECT_MANAGER"], # 사용자 역할
  "iat": 1704067200,                    # 발급 시간
  "exp": 1704070800,                    # 만료 시간 (1시간)
  "aud": "project-management-system",   # 대상
  "iss": "pms-auth-server"              # 발급자
}
```

**설정값:**
- **만료 시간**: 15분 (보안성 강화)
- **알고리즘**: HS256
- **Secret Key**: 256bit 이상의 강력한 비밀키

### 2.2 Refresh Token
```yaml
# JWT Payload
{
  "sub": "1",                           # 사용자 ID
  "type": "refresh",                    # 토큰 타입
  "iat": 1704067200,                    # 발급 시간
  "exp": 1704672000,                    # 만료 시간 (7일)
  "jti": "uuid-generated-id",           # 토큰 고유 ID
  "aud": "project-management-system",
  "iss": "pms-auth-server"
}
```

**설정값:**
- **만료 시간**: 7일
- **저장소**: Redis (자동 만료 설정)
- **고유 ID**: JTI를 통한 토큰 추적 및 무효화

## 3. 인증 흐름 상세 설명

### 3.1 로그인 과정 (Authentication)

#### Step 1: 사용자 자격 증명 검증
```java
@PostMapping("/auth/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    // 1. 이메일로 사용자 조회
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new AuthenticationException("Invalid credentials"));
    
    // 2. 비밀번호 검증 (BCrypt)
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new AuthenticationException("Invalid credentials");
    }
    
    // 3. 계정 상태 확인
    if (!user.isActive() || !user.isEmailVerified()) {
        throw new AuthenticationException("Account not active");
    }
    
    // 4. 토큰 생성 및 반환
    return generateTokensAndResponse(user);
}
```

#### Step 2: 토큰 생성
```java
private ResponseEntity<LoginResponse> generateTokensAndResponse(User user) {
    // Access Token 생성 (15분)
    String accessToken = jwtTokenProvider.createAccessToken(
        user.getId(), 
        user.getEmail(), 
        user.getRoles()
    );
    
    // Refresh Token 생성 (7일)
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
    
    // Refresh Token을 Redis에 저장
    redisTemplate.opsForValue().set(
        "refresh_token:" + user.getId(), 
        refreshToken, 
        Duration.ofDays(7)
    );
    
    return ResponseEntity.ok(LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(900) // 15분
        .user(UserDto.from(user))
        .build());
}
```

### 3.2 API 요청 시 토큰 검증 (Authorization)

#### JWT 필터 구현
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String token = extractTokenFromHeader(request);
        
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰에서 사용자 정보 추출
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            List<String> roles = jwtTokenProvider.getRolesFromToken(token);
            
            // Spring Security Context에 인증 정보 설정
            Authentication authentication = new JwtAuthenticationToken(userId, roles);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

#### 토큰 검증 로직
```java
@Component
public class JwtTokenProvider {
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("JWT token is malformed: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("JWT signature does not match: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid: {}", e.getMessage());
            return false;
        }
    }
}
```

### 3.3 토큰 재발급 과정 (Token Refresh)

#### Refresh Token 검증 및 새 토큰 발급
```java
@PostMapping("/auth/refresh")
public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
    String refreshToken = request.getRefreshToken();
    
    // 1. Refresh Token 형식 검증
    if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
        throw new TokenRefreshException("Invalid refresh token");
    }
    
    // 2. 토큰에서 사용자 ID 추출
    Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
    
    // 3. Redis에서 저장된 Refresh Token과 비교
    String storedRefreshToken = redisTemplate.opsForValue()
        .get("refresh_token:" + userId);
    
    if (!refreshToken.equals(storedRefreshToken)) {
        throw new TokenRefreshException("Refresh token not found in database");
    }
    
    // 4. 새로운 토큰 발급
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found"));
    
    String newAccessToken = jwtTokenProvider.createAccessToken(
        user.getId(), user.getEmail(), user.getRoles());
    String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId());
    
    // 5. 새 Refresh Token을 Redis에 저장
    redisTemplate.opsForValue().set(
        "refresh_token:" + userId, 
        newRefreshToken, 
        Duration.ofDays(7)
    );
    
    return ResponseEntity.ok(TokenRefreshResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .tokenType("Bearer")
        .expiresIn(900)
        .build());
}
```

### 3.4 로그아웃 과정

#### 토큰 무효화
```java
@PostMapping("/auth/logout")
public ResponseEntity<LogoutResponse> logout(HttpServletRequest request) {
    String token = extractTokenFromHeader(request);
    
    if (token != null && jwtTokenProvider.validateToken(token)) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        // Redis에서 Refresh Token 삭제
        redisTemplate.delete("refresh_token:" + userId);
        
        // 선택사항: Access Token을 블랙리스트에 추가
        long remainingTime = jwtTokenProvider.getRemainingTime(token);
        if (remainingTime > 0) {
            redisTemplate.opsForValue().set(
                "blacklist_token:" + token, 
                "true", 
                Duration.ofSeconds(remainingTime)
            );
        }
    }
    
    return ResponseEntity.ok(LogoutResponse.builder()
        .message("Successfully logged out")
        .build());
}
```

## 4. React 클라이언트 구현

### 4.1 토큰 관리 유틸리티
```typescript
// utils/tokenManager.ts
export class TokenManager {
  private static readonly ACCESS_TOKEN_KEY = 'access_token';
  private static readonly REFRESH_TOKEN_KEY = 'refresh_token';

  static setTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }

  static getAccessToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  static getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  static clearTokens(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
  }

  static isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 < Date.now();
    } catch {
      return true;
    }
  }
}
```

### 4.2 HTTP 인터셉터 구현
```typescript
// api/httpClient.ts
import axios, { AxiosRequestConfig, AxiosError } from 'axios';
import { TokenManager } from '../utils/tokenManager';
import { authApi } from './authApi';

const httpClient = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL,
});

// Request 인터셉터: 모든 요청에 Access Token 추가
httpClient.interceptors.request.use(
  (config) => {
    const accessToken = TokenManager.getAccessToken();
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response 인터셉터: 토큰 만료 시 자동 갱신
httpClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = TokenManager.getRefreshToken();
        if (!refreshToken) {
          throw new Error('No refresh token available');
        }

        const response = await authApi.refreshToken(refreshToken);
        const { accessToken, refreshToken: newRefreshToken } = response.data;

        TokenManager.setTokens(accessToken, newRefreshToken);

        // 원래 요청을 새 토큰으로 재시도
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        }
        
        return httpClient(originalRequest);
      } catch (refreshError) {
        // Refresh 실패 시 로그인 페이지로 리다이렉트
        TokenManager.clearTokens();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export { httpClient };
```

### 4.3 인증 Context 구현
```typescript
// contexts/AuthContext.tsx
interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  isAuthenticated: boolean;
  isLoading: boolean;
}

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = async () => {
      const accessToken = TokenManager.getAccessToken();
      
      if (accessToken && !TokenManager.isTokenExpired(accessToken)) {
        try {
          const response = await authApi.getCurrentUser();
          setUser(response.data);
        } catch (error) {
          TokenManager.clearTokens();
        }
      }
      
      setIsLoading(false);
    };

    initializeAuth();
  }, []);

  const login = async (email: string, password: string) => {
    const response = await authApi.login(email, password);
    const { accessToken, refreshToken, user: userData } = response.data;

    TokenManager.setTokens(accessToken, refreshToken);
    setUser(userData);
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } catch (error) {
      // 로그아웃 API 실패해도 로컬 토큰은 제거
      console.error('Logout API failed:', error);
    } finally {
      TokenManager.clearTokens();
      setUser(null);
    }
  };

  return (
    <AuthContext.Provider value={{
      user,
      login,
      logout,
      isAuthenticated: !!user,
      isLoading
    }}>
      {children}
    </AuthContext.Provider>
  );
};
```

## 5. 보안 고려사항

### 5.1 토큰 보안
```java
// SecurityConfig.java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler));

        return http.build();
    }
}
```

### 5.2 토큰 블랙리스트 관리
```java
// TokenBlacklistService.java
@Service
public class TokenBlacklistService {
    
    public void blacklistToken(String token) {
        long remainingTime = jwtTokenProvider.getRemainingTime(token);
        if (remainingTime > 0) {
            redisTemplate.opsForValue().set(
                "blacklist:" + token, 
                "true", 
                Duration.ofSeconds(remainingTime)
            );
        }
    }
    
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
    }
}
```

### 5.3 Rate Limiting
```java
// RateLimitingFilter.java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIp(request);
        String key = "rate_limit:" + clientIp;
        
        String currentRequests = redisTemplate.opsForValue().get(key);
        
        if (currentRequests == null) {
            redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(1));
        } else if (Integer.parseInt(currentRequests) >= 100) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        } else {
            redisTemplate.opsForValue().increment(key);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

## 6. 성능 최적화

### 6.1 토큰 캐싱
```java
// UserCacheService.java
@Service
public class UserCacheService {
    
    @Cacheable(value = "users", key = "#userId")
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    
    @CacheEvict(value = "users", key = "#userId")
    public void evictUser(Long userId) {
        // 사용자 정보 변경 시 캐시 무효화
    }
}
```

### 6.2 Redis 설정 최적화
```yaml
# application.yml
spring:
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
    database: 0
```

## 7. 모니터링 및 로깅

### 7.1 인증 이벤트 로깅
```java
@EventListener
public class AuthenticationEventListener {
    
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        log.info("Authentication success for user: {}", username);
        
        // 성공 로그인 통계 업데이트
        metricsService.incrementLoginSuccess();
    }
    
    @EventListener
    public void handleAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        log.warn("Authentication failed for user: {}", username);
        
        // 실패 로그인 통계 업데이트
        metricsService.incrementLoginFailure();
    }
}
```

### 7.2 JWT 토큰 메트릭스
```java
@Component
public class JwtMetricsCollector {
    
    private final Counter tokenValidationCounter;
    private final Counter tokenRefreshCounter;
    
    public JwtMetricsCollector(MeterRegistry meterRegistry) {
        this.tokenValidationCounter = Counter.builder("jwt.validation")
            .tag("result", "success")
            .register(meterRegistry);
        
        this.tokenRefreshCounter = Counter.builder("jwt.refresh")
            .tag("result", "success")
            .register(meterRegistry);
    }
    
    public void recordTokenValidation(boolean success) {
        tokenValidationCounter.increment(
            Tags.of("result", success ? "success" : "failure")
        );
    }
}
```

이러한 JWT 인증 시스템을 통해 안전하고 확장 가능한 인증 메커니즘을 구현할 수 있습니다. 
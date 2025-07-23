package com.pms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.TestDataFactory;
import com.pms.config.TestConfig;
import com.pms.dto.request.LoginRequest;
import com.pms.dto.request.RegisterRequest;
import com.pms.entity.Role;
import com.pms.entity.User;
import com.pms.repository.RoleRepository;
import com.pms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class AuthIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // 기본 역할 생성
        if (!roleRepository.existsByName("USER")) {
            Role userRole = TestDataFactory.createTestRole("USER");
            roleRepository.save(userRole);
        }
    }

    @Test
    void 전체_인증_플로우_테스트() throws Exception {
        // 1. 회원가입
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("integration@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setName("Integration Test User");
        registerRequest.setNickname("IntegrationUser");

        String registerResponse = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 2. 로그인
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("integration@test.com");
        loginRequest.setPassword("password123");

        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.user.email").value("integration@test.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 3. 토큰 갱신
        String refreshToken = objectMapper.readTree(loginResponse)
                .get("data").get("refreshToken").asText();

        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    void 보안_검증_테스트() throws Exception {
        // 1. 잘못된 토큰으로 보호된 리소스 접근 시도
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"invalid.token.here\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        // 2. 잘못된 로그인 시도
        LoginRequest badLoginRequest = new LoginRequest();
        badLoginRequest.setEmail("nonexistent@test.com");
        badLoginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badLoginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        // 3. 중복 이메일로 회원가입 시도
        // 먼저 사용자 생성
        User existingUser = TestDataFactory.createTestUser("existing@test.com", "Existing User", passwordEncoder);
        Role userRole = roleRepository.findByName("USER").orElseThrow();
        existingUser.getRoles().add(userRole);
        userRepository.save(existingUser);

        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setEmail("existing@test.com");
        duplicateRequest.setPassword("password123");
        duplicateRequest.setName("Duplicate User");
        duplicateRequest.setNickname("DuplicateUser");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void 입력_검증_테스트() throws Exception {
        // 1. 빈 이메일로 로그인 시도
        LoginRequest emptyEmailRequest = new LoginRequest();
        emptyEmailRequest.setEmail("");
        emptyEmailRequest.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyEmailRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // 2. 잘못된 이메일 형식으로 회원가입 시도
        RegisterRequest invalidEmailRequest = new RegisterRequest();
        invalidEmailRequest.setEmail("invalid-email-format");
        invalidEmailRequest.setPassword("password123");
        invalidEmailRequest.setName("Test User");
        invalidEmailRequest.setNickname("TestUser");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // 3. 너무 짧은 비밀번호로 회원가입 시도
        RegisterRequest shortPasswordRequest = new RegisterRequest();
        shortPasswordRequest.setEmail("test@example.com");
        shortPasswordRequest.setPassword("123");
        shortPasswordRequest.setName("Test User");
        shortPasswordRequest.setNickname("TestUser");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shortPasswordRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
} 
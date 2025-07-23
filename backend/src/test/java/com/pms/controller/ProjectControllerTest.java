package com.pms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pms.TestDataFactory;
import com.pms.config.TestConfig;
import com.pms.dto.request.CreateProjectRequest;
import com.pms.dto.request.UpdateProjectRequest;
import com.pms.entity.Project;
import com.pms.entity.ProjectMember;
import com.pms.entity.Role;
import com.pms.entity.User;
import com.pms.repository.ProjectRepository;
import com.pms.repository.RoleRepository;
import com.pms.repository.UserRepository;
import com.pms.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
class ProjectControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User testUser;
    private User anotherUser;
    private Project testProject;
    private String authToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // 기본 역할 생성
        Role userRole = TestDataFactory.createTestRole("USER");
        roleRepository.save(userRole);

        // 테스트 사용자들 생성
        testUser = TestDataFactory.createTestUser("owner@example.com", "Project Owner", passwordEncoder);
        testUser.getRoles().add(userRole);
        userRepository.save(testUser);

        anotherUser = TestDataFactory.createTestUser("member@example.com", "Project Member", passwordEncoder);
        anotherUser.getRoles().add(userRole);
        userRepository.save(anotherUser);

        // 테스트 프로젝트 생성
        testProject = TestDataFactory.createTestProject("Test Project", "TP", testUser);
        projectRepository.save(testProject);

        // JWT 토큰 생성
        authToken = jwtTokenProvider.createToken(
                testUser.getEmail(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void 프로젝트_생성_성공() throws Exception {
        // Given
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("New Test Project");
        request.setKey("NTP");
        request.setDescription("This is a new test project");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusMonths(6));
        request.setIsPublic(false);

        // When & Then
        mockMvc.perform(post("/projects")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("New Test Project"))
                .andExpect(jsonPath("$.data.key").value("NTP"))
                .andExpect(jsonPath("$.data.owner.email").value("owner@example.com"));
    }

    @Test
    void 프로젝트_생성_실패_중복_키() throws Exception {
        // Given
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Another Project");
        request.setKey("TP"); // 이미 존재하는 키
        request.setDescription("This project has duplicate key");

        // When & Then
        mockMvc.perform(post("/projects")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void 사용자_프로젝트_목록_조회_성공() throws Exception {
        // When & Then
        mockMvc.perform(get("/projects")
                .header("Authorization", "Bearer " + authToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].name").value("Test Project"))
                .andExpect(jsonPath("$.data.content[0].key").value("TP"));
    }

    @Test
    void 프로젝트_상세_조회_성공() throws Exception {
        // When & Then
        mockMvc.perform(get("/projects/" + testProject.getId())
                .header("Authorization", "Bearer " + authToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testProject.getId()))
                .andExpect(jsonPath("$.data.name").value("Test Project"))
                .andExpect(jsonPath("$.data.key").value("TP"));
    }

    @Test
    void 프로젝트_키로_조회_성공() throws Exception {
        // When & Then
        mockMvc.perform(get("/projects/key/" + testProject.getKey())
                .header("Authorization", "Bearer " + authToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.key").value(testProject.getKey()))
                .andExpect(jsonPath("$.data.name").value("Test Project"));
    }

    @Test
    void 프로젝트_수정_성공() throws Exception {
        // Given
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("Updated Project Name");
        request.setDescription("Updated description");
        request.setIsPublic(true);

        // When & Then
        mockMvc.perform(put("/projects/" + testProject.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Updated Project Name"))
                .andExpect(jsonPath("$.data.description").value("Updated description"))
                .andExpect(jsonPath("$.data.isPublic").value(true));
    }

    @Test
    void 프로젝트_접근_권한_없음() throws Exception {
        // Given - 다른 사용자의 토큰 생성
        String anotherUserToken = jwtTokenProvider.createToken(
                anotherUser.getEmail(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // When & Then
        mockMvc.perform(get("/projects/" + testProject.getId())
                .header("Authorization", "Bearer " + anotherUserToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void 인증되지_않은_요청() throws Exception {
        // When & Then
        mockMvc.perform(get("/projects"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 공개_프로젝트_목록_조회() throws Exception {
        // Given - 공개 프로젝트 생성
        testProject.setIsPublic(true);
        projectRepository.save(testProject);

        // When & Then
        mockMvc.perform(get("/projects/public"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void 프로젝트_검색_성공() throws Exception {
        // When & Then
        mockMvc.perform(get("/projects")
                .header("Authorization", "Bearer " + authToken)
                .param("search", "Test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void 프로젝트_페이징_조회() throws Exception {
        // When & Then
        mockMvc.perform(get("/projects")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "0")
                .param("size", "5")
                .param("sortBy", "name")
                .param("sortDir", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pageable").exists())
                .andExpect(jsonPath("$.data.totalElements").exists());
    }
} 
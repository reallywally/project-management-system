package com.pms.service;

import com.pms.TestDataFactory;
import com.pms.entity.Project;
import com.pms.entity.ProjectMember;
import com.pms.entity.User;
import com.pms.repository.ProjectRepository;
import com.pms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProjectService projectService;

    private User owner;
    private User member;
    private Project testProject;

    @BeforeEach
    void setUp() {
        owner = TestDataFactory.createTestUser("owner@example.com", "Owner", passwordEncoder);
        owner.setId(1L);
        
        member = TestDataFactory.createTestUser("member@example.com", "Member", passwordEncoder);
        member.setId(2L);
        
        testProject = TestDataFactory.createTestProject("Test Project", "TP", owner);
        testProject.setId(1L);
    }

    @Test
    void 프로젝트_생성_성공() {
        // Given
        String name = "New Project";
        String key = "NP";
        String description = "Test project description";
        Long ownerId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(3);
        Boolean isPublic = false;

        when(projectRepository.existsByKey(key)).thenReturn(false);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // When
        Project result = projectService.createProject(name, key, description, ownerId, 
                startDate, endDate, isPublic);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Project"); // mocked return
        assertThat(result.getOwner()).isEqualTo(owner);
        
        verify(projectRepository).existsByKey(key);
        verify(userRepository).findById(ownerId);
        verify(projectRepository, times(2)).save(any(Project.class)); // 한번은 프로젝트, 한번은 멤버 추가 후
    }

    @Test
    void 프로젝트_생성_실패_중복_키() {
        // Given
        String key = "EXISTING";
        
        when(projectRepository.existsByKey(key)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> projectService.createProject("Project", key, "desc", 1L, 
                LocalDate.now(), LocalDate.now().plusMonths(1), false))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Project key already exists");
        
        verify(projectRepository).existsByKey(key);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void 프로젝트_생성_실패_사용자_없음() {
        // Given
        Long nonExistentUserId = 999L;
        
        when(projectRepository.existsByKey(anyString())).thenReturn(false);
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.createProject("Project", "KEY", "desc", 
                nonExistentUserId, LocalDate.now(), LocalDate.now().plusMonths(1), false))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("User not found");
        
        verify(userRepository).findById(nonExistentUserId);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void 사용자_프로젝트_목록_조회() {
        // Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> expectedPage = new PageImpl<>(List.of(testProject));
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(projectRepository.findUserProjectsByStatus(owner, Project.Status.ACTIVE, pageable))
                .thenReturn(expectedPage);

        // When
        Page<Project> result = projectService.findUserProjects(userId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testProject);
        
        verify(userRepository).findById(userId);
        verify(projectRepository).findUserProjectsByStatus(owner, Project.Status.ACTIVE, pageable);
    }

    @Test
    void 프로젝트_업데이트_성공() {
        // Given
        Long projectId = 1L;
        String newName = "Updated Project";
        String newDescription = "Updated description";
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(testProject)).thenReturn(testProject);

        // When
        Project result = projectService.updateProject(projectId, newName, newDescription, 
                null, null, null);

        // Then
        assertThat(result).isNotNull();
        verify(projectRepository).findById(projectId);
        verify(projectRepository).save(testProject);
    }

    @Test
    void 프로젝트_업데이트_실패_프로젝트_없음() {
        // Given
        Long nonExistentProjectId = 999L;
        
        when(projectRepository.findById(nonExistentProjectId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.updateProject(nonExistentProjectId, 
                "name", "desc", null, null, null))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Project not found");
        
        verify(projectRepository).findById(nonExistentProjectId);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void 멤버_추가_성공() {
        // Given
        Long projectId = 1L;
        Long userId = 2L;
        ProjectMember.Role role = ProjectMember.Role.DEVELOPER;
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(userRepository.findById(userId)).thenReturn(Optional.of(member));
        when(projectRepository.save(testProject)).thenReturn(testProject);

        // When
        ProjectMember result = projectService.addMember(projectId, userId, role);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(member);
        assertThat(result.getRole()).isEqualTo(role);
        
        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(userId);
        verify(projectRepository).save(testProject);
    }

    @Test
    void 멤버_추가_실패_중복_멤버() {
        // Given
        Long projectId = 1L;
        Long userId = 1L; // 이미 owner인 사용자
        
        // 프로젝트에 이미 owner가 멤버로 등록되어 있다고 가정
        ProjectMember existingMember = TestDataFactory.createTestProjectMember(testProject, owner, ProjectMember.Role.OWNER);
        testProject.addMember(existingMember);
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        // When & Then
        assertThatThrownBy(() -> projectService.addMember(projectId, userId, ProjectMember.Role.DEVELOPER))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("User is already a member");
        
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void 멤버_제거_성공() {
        // Given
        Long projectId = 1L;
        Long userId = 2L;
        
        ProjectMember memberToRemove = TestDataFactory.createTestProjectMember(testProject, member, ProjectMember.Role.DEVELOPER);
        testProject.addMember(memberToRemove);
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(testProject)).thenReturn(testProject);

        // When
        projectService.removeMember(projectId, userId);

        // Then
        verify(projectRepository).findById(projectId);
        verify(projectRepository).save(testProject);
    }

    @Test
    void 멤버_제거_실패_owner_제거_시도() {
        // Given
        Long projectId = 1L;
        Long ownerId = 1L;
        
        ProjectMember ownerMember = TestDataFactory.createTestProjectMember(testProject, owner, ProjectMember.Role.OWNER);
        testProject.addMember(ownerMember);
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));

        // When & Then
        assertThatThrownBy(() -> projectService.removeMember(projectId, ownerId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Cannot remove project owner");
        
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void 사용자_프로젝트_멤버십_확인() {
        // Given
        Long userId = 1L;
        Long projectId = 1L;
        
        ProjectMember membership = TestDataFactory.createTestProjectMember(testProject, owner, ProjectMember.Role.OWNER);
        testProject.addMember(membership);
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));

        // When
        boolean result = projectService.isUserMemberOfProject(userId, projectId);

        // Then
        assertTrue(result);
        verify(projectRepository).findById(projectId);
    }

    @Test
    void 사용자_프로젝트_권한_조회() {
        // Given
        Long userId = 1L;
        Long projectId = 1L;
        
        ProjectMember membership = TestDataFactory.createTestProjectMember(testProject, owner, ProjectMember.Role.OWNER);
        testProject.addMember(membership);
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));

        // When
        ProjectMember.Role result = projectService.getUserRoleInProject(userId, projectId);

        // Then
        assertThat(result).isEqualTo(ProjectMember.Role.OWNER);
        verify(projectRepository).findById(projectId);
    }

    @Test
    void 프로젝트_보관_처리() {
        // Given
        Long projectId = 1L;
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(testProject)).thenReturn(testProject);

        // When
        projectService.archiveProject(projectId);

        // Then
        assertThat(testProject.getStatus()).isEqualTo(Project.Status.ARCHIVED);
        verify(projectRepository).findById(projectId);
        verify(projectRepository).save(testProject);
    }

    @Test
    void 프로젝트_삭제_처리() {
        // Given
        Long projectId = 1L;
        
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(testProject)).thenReturn(testProject);

        // When
        projectService.deleteProject(projectId);

        // Then
        assertThat(testProject.getStatus()).isEqualTo(Project.Status.DELETED);
        verify(projectRepository).findById(projectId);
        verify(projectRepository).save(testProject);
    }
} 
package com.pms.repository;

import com.pms.TestDataFactory;
import com.pms.config.TestConfig;
import com.pms.entity.Project;
import com.pms.entity.ProjectMember;
import com.pms.entity.Role;
import com.pms.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private User owner;
    private User member;
    private Project testProject;
    private Project publicProject;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = TestDataFactory.createTestRole("USER");
        entityManager.persistAndFlush(userRole);

        owner = TestDataFactory.createTestUser("owner@example.com", "Owner", passwordEncoder);
        owner.getRoles().add(userRole);
        entityManager.persistAndFlush(owner);

        member = TestDataFactory.createTestUser("member@example.com", "Member", passwordEncoder);
        member.getRoles().add(userRole);
        entityManager.persistAndFlush(member);

        // 비공개 프로젝트
        testProject = TestDataFactory.createTestProject("Test Project", "TP", owner);
        testProject.setIsPublic(false);
        entityManager.persistAndFlush(testProject);

        // 공개 프로젝트
        publicProject = TestDataFactory.createTestProject("Public Project", "PP", owner);
        publicProject.setIsPublic(true);
        entityManager.persistAndFlush(publicProject);

        // 프로젝트 멤버 추가
        ProjectMember ownerMembership = TestDataFactory.createTestProjectMember(testProject, owner, ProjectMember.Role.OWNER);
        testProject.addMember(ownerMembership);
        
        ProjectMember memberMembership = TestDataFactory.createTestProjectMember(testProject, member, ProjectMember.Role.DEVELOPER);
        testProject.addMember(memberMembership);
        
        entityManager.persistAndFlush(testProject);
        entityManager.clear();
    }

    @Test
    void 프로젝트_키로_조회_성공() {
        // When
        Optional<Project> result = projectRepository.findByKey("TP");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getKey()).isEqualTo("TP");
        assertThat(result.get().getName()).isEqualTo("Test Project");
    }

    @Test
    void 프로젝트_키로_조회_실패() {
        // When
        Optional<Project> result = projectRepository.findByKey("NONEXISTENT");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void 프로젝트_키_중복_체크_true() {
        // When
        boolean exists = projectRepository.existsByKey("TP");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void 프로젝트_키_중복_체크_false() {
        // When
        boolean exists = projectRepository.existsByKey("NEWKEY");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void 공개_프로젝트_조회() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Project> result = projectRepository.findPublicProjects(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getKey()).isEqualTo("PP");
        assertThat(result.getContent().get(0).getIsPublic()).isTrue();
    }

    @Test
    void 사용자_프로젝트_조회_상태별() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When - owner의 활성 프로젝트 조회
        Page<Project> result = projectRepository.findUserProjectsByStatus(owner, Project.Status.ACTIVE, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2); // testProject + publicProject
        assertThat(result.getContent()).extracting("key")
                .containsExactlyInAnyOrder("TP", "PP");
    }

    @Test
    void 사용자_프로젝트_검색() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Project> result = projectRepository.searchUserProjects(owner, "Test", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Project");
    }

    @Test
    void 프로젝트_이름_검색() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Project> result = projectRepository.findByNameContainingIgnoreCaseAndStatus("Public", Project.Status.ACTIVE, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Public Project");
    }

    @Test
    void 소유자별_프로젝트_조회() {
        // When
        List<Project> result = projectRepository.findByOwner(owner);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("key")
                .containsExactlyInAnyOrder("TP", "PP");
    }

    @Test
    void 프로젝트_생성_및_저장() {
        // Given
        Project newProject = TestDataFactory.createTestProject("New Project", "NP", owner);
        newProject.setDescription("This is a new project for testing");
        newProject.setIsPublic(true);

        // When
        Project savedProject = projectRepository.save(newProject);

        // Then
        assertThat(savedProject).isNotNull();
        assertThat(savedProject.getId()).isNotNull();
        assertThat(savedProject.getKey()).isEqualTo("NP");
        assertThat(savedProject.getName()).isEqualTo("New Project");
        assertThat(savedProject.getOwner()).isEqualTo(owner);
        assertThat(savedProject.getCreatedAt()).isNotNull();
        assertThat(savedProject.getUpdatedAt()).isNotNull();
    }

    @Test
    void 프로젝트_업데이트() {
        // Given
        String updatedName = "Updated Project Name";
        String updatedDescription = "Updated description";

        // When
        testProject.setName(updatedName);
        testProject.setDescription(updatedDescription);
        Project updatedProject = projectRepository.save(testProject);

        // Then
        assertThat(updatedProject.getName()).isEqualTo(updatedName);
        assertThat(updatedProject.getDescription()).isEqualTo(updatedDescription);
        assertThat(updatedProject.getUpdatedAt()).isAfter(updatedProject.getCreatedAt());
    }

    @Test
    void 프로젝트_삭제() {
        // Given
        Long projectId = testProject.getId();

        // When
        projectRepository.delete(testProject);

        // Then
        Optional<Project> deletedProject = projectRepository.findById(projectId);
        assertThat(deletedProject).isEmpty();
    }

    @Test
    void 프로젝트_상태_변경() {
        // When
        testProject.setStatus(Project.Status.ARCHIVED);
        Project archivedProject = projectRepository.save(testProject);

        // Then
        assertThat(archivedProject.getStatus()).isEqualTo(Project.Status.ARCHIVED);
    }

    @Test
    void 프로젝트_멤버_수_조회() {
        // When - 프로젝트를 다시 조회하여 멤버 수 확인
        Optional<Project> foundProject = projectRepository.findById(testProject.getId());

        // Then
        assertThat(foundProject).isPresent();
        assertThat(foundProject.get().getMembers()).hasSize(2); // owner + member
    }

    @Test
    void 활성_프로젝트만_조회() {
        // Given - 보관된 프로젝트 생성
        Project archivedProject = TestDataFactory.createTestProject("Archived Project", "AP", owner);
        archivedProject.setStatus(Project.Status.ARCHIVED);
        entityManager.persistAndFlush(archivedProject);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Project> activeProjects = projectRepository.findByStatus(Project.Status.ACTIVE, pageable);
        Page<Project> archivedProjects = projectRepository.findByStatus(Project.Status.ARCHIVED, pageable);

        // Then
        assertThat(activeProjects.getContent()).hasSize(2); // testProject + publicProject
        assertThat(archivedProjects.getContent()).hasSize(1);
        assertThat(archivedProjects.getContent().get(0).getKey()).isEqualTo("AP");
    }

    @Test
    void 프로젝트_멤버십_조회() {
        // When
        Optional<Project> project = projectRepository.findById(testProject.getId());

        // Then
        assertThat(project).isPresent();
        
        Project foundProject = project.get();
        assertThat(foundProject.getMembers()).hasSize(2);
        
        // Owner 멤버십 확인
        Optional<ProjectMember> ownerMember = foundProject.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(owner.getId()))
                .findFirst();
        assertThat(ownerMember).isPresent();
        assertThat(ownerMember.get().getRole()).isEqualTo(ProjectMember.Role.OWNER);
        
        // 일반 멤버십 확인
        Optional<ProjectMember> regularMember = foundProject.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(member.getId()))
                .findFirst();
        assertThat(regularMember).isPresent();
        assertThat(regularMember.get().getRole()).isEqualTo(ProjectMember.Role.DEVELOPER);
    }

    @Test
    void 페이징_정렬_테스트() {
        // Given
        Pageable pageable = PageRequest.of(0, 1); // 페이지 크기 1

        // When
        Page<Project> firstPage = projectRepository.findByStatus(Project.Status.ACTIVE, pageable);

        // Then
        assertThat(firstPage.getContent()).hasSize(1);
        assertThat(firstPage.getTotalElements()).isEqualTo(2);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.isFirst()).isTrue();
        assertThat(firstPage.hasNext()).isTrue();
    }
} 
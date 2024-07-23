package com.wally.service;

import com.wally.model.Project;
import com.wally.model.ProjectUser;
import com.wally.repository.ProjectRepository;
import com.wally.repository.ProjectUserRepository;
import com.wally.request.ProjectCreate;
import com.wally.request.ProjectSearch;
import com.wally.request.ProjectUpdate;
import com.wally.response.ProjectResp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectUserRepository projectUserRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;


    @Test
    @DisplayName("프로젝트 생성")
    void createProject() throws Exception {
        // given
        ProjectCreate projectCreate = ProjectCreate.builder()
                .name("프로젝트 이름")
                .description("프로젝트 설명")
                .category("프로젝트 카테고리")
                .ownerId(1L)
                .build();
        Long userId = 1L;

        // when
        projectService.createProject(projectCreate, userId);

        // then
    }

    @Test
    @DisplayName("사용자가 속한 프로젝트 조회")
    void getProjectByUser() throws Exception {
        // given
        ProjectSearch projectSearch = ProjectSearch.builder()
                .ownerId(1L)
                .build();
        Page<ProjectResp> projectRespPage = null;
        given(projectRepository.getList(projectSearch)).willReturn(projectRespPage);

        // when
        projectService.getProjectList(projectSearch);

        // then
    }

    @Test
    @DisplayName("프로젝트 단건 조회")
    void getProjectById() throws Exception {
        // given
        Long projectId = 1L;
        Project project = Project.builder()
                .name("프로젝트 이름")
                .description("프로젝트 설명")
                .category("프로젝트 카테고리")
                .build();

        given(projectRepository.findById(projectId)).willReturn(Optional.ofNullable(project));

        // when
        projectService.getProjectById(projectId);

        // then
    }

    @Test
    @DisplayName("프로젝트 삭제")
    void deleteProject() throws Exception {
        // given
        Long projectId = 1L;
        Long userId = 1L;
        Project project = Project.builder()
                .name("프로젝트 이름")
                .description("프로젝트 설명")
                .category("프로젝트 카테고리")
                .build();

        given(projectRepository.findById(projectId)).willReturn(Optional.ofNullable(project));

        // when
        projectService.deleteProject(projectId, userId);

        // then
    }

    @Test
    @DisplayName("프로젝트 수정")
    void updateProject() throws Exception {
        // given
        ProjectUpdate projectUpdate = ProjectUpdate.builder()
                .name("프로젝트 이름")
                .description("프로젝트 설명")
                .category("프로젝트 카테고리")
                .build();
        Long projectId = 1L;
        Project project = Project.builder()
                .name("프로젝트 이름")
                .description("프로젝트 설명")
                .category("프로젝트 카테고리")
                .build();

        given(projectRepository.findById(projectId)).willReturn(Optional.ofNullable(project));

        // when
        projectService.updateProject(projectUpdate, projectId);

        // then
    }

    @Test
    @DisplayName("프로젝트에 사용자 추가")
    void addUserToProject() throws Exception {
        // given
        Long projectId = 1L;
        Long userId = 1L;

        // when
        projectService.addUserToProject(projectId, userId);

        // then
    }

    @Test
    @DisplayName("프로젝트에서 사용자 삭제")
    void removeUserFromProject() throws Exception {
        // given
        Long projectId = 1L;
        Long userId = 1L;
        ProjectUser projectUser = ProjectUser.builder()
                .projectId(1L)
                .userId(1L)
                .build();

        given(projectUserRepository.findByProjectIdAndUserId(projectId, userId)).willReturn(projectUser);

        // when
        projectService.removeUserFromProject(projectId, userId);

        // then
    }

    @Test
    void getChatByProjectId() {
    }

    @Test
    @DisplayName("프로젝트 목록 조회")
    void searchProject() {

    }
}
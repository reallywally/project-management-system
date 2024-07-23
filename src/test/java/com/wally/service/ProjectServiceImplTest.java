package com.wally.service;

import com.wally.repository.ProjectRepository;
import com.wally.request.ProjectCreate;
import com.wally.request.ProjectSearch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

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

        // when
        projectService.getProjectList(projectSearch);

        // then
    }

    @Test
    void getProjectById() {
    }

    @Test
    void deleteProject() {
    }

    @Test
    void updateProject() {
    }

    @Test
    void addUserToProject() {
    }

    @Test
    void removeUserFromProject() {
    }

    @Test
    void getChatByProjectId() {
    }

    @Test
    void searchProject() {
    }
}
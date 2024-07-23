package com.wally.service;

import com.wally.model.Chat;
import com.wally.model.Project;
import com.wally.model.ProjectUser;
import com.wally.model.User;
import com.wally.repository.ProjectRepository;
import com.wally.repository.ProjectUserRepository;
import com.wally.request.ProjectCreate;
import com.wally.request.ProjectSearch;
import com.wally.request.ProjectUpdate;
import com.wally.response.ProjectResp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserService userService;
    private final ChatService chatService;

    @Override
    public void createProject(ProjectCreate projectCreate, Long userId) throws Exception {
        Project createdProject = Project.builder()
                .ownerId(userId)
                .name(projectCreate.getName())
                .category(projectCreate.getCategory())
                .description(projectCreate.getDescription())
                .build();

        Project project = projectRepository.save(createdProject);

        addProjectUser(project.getId(), userId);


        // 이건 ChatService에서 하는게 맞지 않나
//        Chat chat = new Chat();
//        chat.setProject(savedProject);
//
//        Chat projectedChat = chatService.createChat(chat);
//        savedProject.setChat(projectedChat);
    }

    @Override
    public Page<ProjectResp> getProjectList(ProjectSearch projectSearch) throws Exception {
        return  projectRepository.getList(projectSearch);
    }

    @Override
    public Project getProjectById(Long projectId) throws Exception {
        Optional<Project> project = projectRepository.findById(projectId);

        if (project.isEmpty()) {
            throw new Exception("Project not found");
        }

        return project.get();
    }

    @Override
    public void deleteProject(Long projectId, Long userId) throws Exception {
        getProjectById(projectId);
        projectRepository.deleteById(projectId);
    }

    @Override
    public void updateProject(ProjectUpdate projectUpdate, Long projectId) throws Exception {

        Project project = getProjectById(projectId);

        project.update(projectUpdate);
    }

    @Override
    public void addUserToProject(Long projectId, Long userId) throws Exception {

        ProjectUser projectUser = ProjectUser.builder()
                .projectId(projectId)
                .userId(userId)
                .build();

        projectUserRepository.save(projectUser);
    }

    @Override
    public void removeUserFromProject(Long projectId, Long userId) throws Exception {
        ProjectUser projectUser = projectUserRepository.findByProjectIdAndUserId(projectId, userId);

        if (projectUser == null) {
            throw new Exception("User not found in project");
        }

        projectUserRepository.delete(projectUser);
    }

    @Override
    public Chat getChatByProjectId(Long projectId) throws Exception {
        // todo. 나중에 개선
//        Project project = getProjectById(projectId);
//
//        return project.getChat();
        return null;
    }

    @Override
    public List<Project> searchProject(String keyword, User user) throws Exception {
        String partinalName = "% " + keyword + "%";

        return null;
        //return projectRepository.findByNameContainingAndTeamContains(partinalName, user);
    }

    // private -----------------------------------------------------
    private void addProjectUser(Long projectId, Long userId) {
        ProjectUser projectUser = ProjectUser.builder()
                .projectId(projectId)
                .userId(userId)
                .build();

        projectUserRepository.save(projectUser);
    }
}

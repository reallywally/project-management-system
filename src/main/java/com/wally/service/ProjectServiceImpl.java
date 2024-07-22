package com.wally.service;

import com.wally.model.Chat;
import com.wally.model.Project;
import com.wally.model.User;
import com.wally.repository.ProjectRepository;
import com.wally.request.ProjectCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ChatService chatService;

    @Override
    public void createProject(ProjectCreate projectCreate, User user) throws Exception {
        Project createdProject = Project.builder()
                .ownerId(user.getId())
                .name(projectCreate.getName())
                .category(projectCreate.getCategory())
                .description(projectCreate.getDescription())
                .build();

        projectRepository.save(createdProject);

        // 이건 ChatService에서 하는게 맞지 않나
//        Chat chat = new Chat();
//        chat.setProject(savedProject);
//
//        Chat projectedChat = chatService.createChat(chat);
//        savedProject.setChat(projectedChat);
    }

    @Override
    public List<Project> getProjectByTeam(User user, String category, String tag) throws Exception {
        List<Project> projects = projectRepository.findByTeamContainingOrOwner(user, user);

        if (category != null) {
            projects = projects.stream()
                    .filter(project -> project.getCategory().equals(category))
                    .collect(Collectors.toList());
        }

        if (tag != null) {
            projects = projects.stream()
                    .filter(project -> project.getTags().contains(tag))
                    .collect(Collectors.toList());
        }

        return null;
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
    public Project updateProject(Project updatedProject, Long id) throws Exception {
        Project project = getProjectById(id);

        project.setName(updatedProject.getName());
        project.setDescription(updatedProject.getDescription());
        project.setTags(updatedProject.getTags());

        return projectRepository.save(project);
    }

    @Override
    public void addUserToProject(Long projectId, Long userId) throws Exception {
        Project project = getProjectById(projectId);
        User user = userService.findUserById(userId);

        if (!project.getTeam().contains(user)) {
            project.getChat().getUsers().add(user);
            project.getTeam().add(user);

            projectRepository.save(project);
        }
    }

    @Override
    public void removeUserFromProject(Long projectId, Long userId) throws Exception {
        Project project = getProjectById(projectId);
        User user = userService.findUserById(userId);

        if (!project.getTeam().contains(user)) {
            project.getChat().getUsers().remove(user);
            project.getTeam().remove(user);

            projectRepository.save(project);
        }
    }

    @Override
    public Chat getChatByProjectId(Long projectId) throws Exception {
        Project project = getProjectById(projectId);

        return project.getChat();
    }

    @Override
    public List<Project> searchProject(String keyword, User user) throws Exception {
        String partinalName = "% " + keyword + "%";

        return projectRepository.findByNameContainingAndTeamContains(partinalName, user);
    }
}

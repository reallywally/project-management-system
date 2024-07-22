package com.wally.service;

import com.wally.model.Chat;
import com.wally.model.Project;
import com.wally.model.User;
import com.wally.request.ProjectCreate;
import com.wally.request.ProjectUpdate;

import java.util.List;

public interface ProjectService {

    void createProject(ProjectCreate projectCreate, User user) throws Exception;

    List<Project> getProjectByTeam(User user, String category, String tag) throws Exception;

    Project getProjectById(Long projectId) throws Exception;

    void deleteProject(Long projectId, Long userId) throws Exception;

    void updateProject(ProjectUpdate projectUpdate, Long id) throws Exception;

    void addUserToProject(Long projectId, Long userId) throws Exception;

    void removeUserFromProject(Long projectId, Long userId) throws Exception;

    Chat getChatByProjectId(Long projectId) throws Exception;

    List<Project> searchProject(String keyword, User user) throws Exception;
}

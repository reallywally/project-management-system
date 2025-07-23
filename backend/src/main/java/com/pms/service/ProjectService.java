package com.pms.service;

import com.pms.entity.Project;
import com.pms.entity.ProjectMember;
import com.pms.entity.User;
import com.pms.repository.ProjectRepository;
import com.pms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Project createProject(String name, String key, String description, Long ownerId, 
                               LocalDate startDate, LocalDate endDate, Boolean isPublic) {
        
        if (projectRepository.existsByKey(key)) {
            throw new RuntimeException("Project key already exists: " + key);
        }
        
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Project project = new Project();
        project.setName(name);
        project.setKey(key.toUpperCase());
        project.setDescription(description);
        project.setOwner(owner);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setIsPublic(isPublic != null ? isPublic : false);
        project.setStatus(Project.Status.ACTIVE);
        
        Project savedProject = projectRepository.save(project);
        
        // Add owner as project member with OWNER role
        ProjectMember ownerMember = new ProjectMember(savedProject, owner, ProjectMember.Role.OWNER);
        savedProject.addMember(ownerMember);
        
        return projectRepository.save(savedProject);
    }
    
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }
    
    public Optional<Project> findByKey(String key) {
        return projectRepository.findByKey(key);
    }
    
    public Page<Project> findUserProjects(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return projectRepository.findUserProjectsByStatus(user, Project.Status.ACTIVE, pageable);
    }
    
    public Page<Project> searchUserProjects(Long userId, String query, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return projectRepository.searchUserProjects(user, query, pageable);
    }
    
    public Page<Project> findPublicProjects(Pageable pageable) {
        return projectRepository.findPublicProjects(pageable);
    }
    
    public Project updateProject(Long projectId, String name, String description, 
                               LocalDate startDate, LocalDate endDate, Boolean isPublic) {
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        if (name != null) project.setName(name);
        if (description != null) project.setDescription(description);
        if (startDate != null) project.setStartDate(startDate);
        if (endDate != null) project.setEndDate(endDate);
        if (isPublic != null) project.setIsPublic(isPublic);
        
        return projectRepository.save(project);
    }
    
    public ProjectMember addMember(Long projectId, Long userId, ProjectMember.Role role) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user is already a member
        boolean alreadyMember = project.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(userId));
        
        if (alreadyMember) {
            throw new RuntimeException("User is already a member of this project");
        }
        
        ProjectMember member = new ProjectMember(project, user, role);
        project.addMember(member);
        
        projectRepository.save(project);
        
        return member;
    }
    
    public void removeMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        ProjectMember memberToRemove = project.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User is not a member of this project"));
        
        // Cannot remove project owner
        if (memberToRemove.getRole() == ProjectMember.Role.OWNER) {
            throw new RuntimeException("Cannot remove project owner");
        }
        
        project.removeMember(memberToRemove);
        projectRepository.save(project);
    }
    
    public ProjectMember updateMemberRole(Long projectId, Long userId, ProjectMember.Role newRole) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        ProjectMember member = project.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User is not a member of this project"));
        
        // Cannot change owner role
        if (member.getRole() == ProjectMember.Role.OWNER) {
            throw new RuntimeException("Cannot change owner role");
        }
        
        member.setRole(newRole);
        projectRepository.save(project);
        
        return member;
    }
    
    public void archiveProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        project.setStatus(Project.Status.ARCHIVED);
        projectRepository.save(project);
    }
    
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        project.setStatus(Project.Status.DELETED);
        projectRepository.save(project);
    }
    
    public boolean isUserMemberOfProject(Long userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        return project.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(userId));
    }
    
    public ProjectMember.Role getUserRoleInProject(Long userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        return project.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(userId))
                .map(ProjectMember::getRole)
                .findFirst()
                .orElse(null);
    }
} 
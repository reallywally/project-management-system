package com.pms.controller;

import com.pms.dto.request.CreateProjectRequest;
import com.pms.dto.request.UpdateProjectRequest;
import com.pms.dto.request.AddMemberRequest;
import com.pms.dto.response.ApiResponse;
import com.pms.dto.response.ProjectResponse;
import com.pms.entity.Project;
import com.pms.entity.ProjectMember;
import com.pms.entity.User;
import com.pms.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
@Tag(name = "Project Management", description = "Project management APIs")
public class ProjectController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    
    @Autowired
    private ProjectService projectService;
    
    @PostMapping
    @Operation(summary = "Create project", description = "Create a new project")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Project project = projectService.createProject(
                    request.getName(),
                    request.getKey(),
                    request.getDescription(),
                    currentUser.getId(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getIsPublic()
            );
            
            ProjectResponse response = ProjectResponse.from(project);
            return ResponseEntity.ok(ApiResponse.success(response, "Project created successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to create project", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("PROJECT_CREATION_FAILED", e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get user projects", description = "Get projects for current user")
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getUserProjects(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {
        
        try {
            Sort sort = Sort.by(sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Project> projects;
            if (search != null && !search.trim().isEmpty()) {
                projects = projectService.searchUserProjects(currentUser.getId(), search, pageable);
            } else {
                projects = projectService.findUserProjects(currentUser.getId(), pageable);
            }
            
            Page<ProjectResponse> response = projects.map(ProjectResponse::from);
            
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            logger.error("Failed to get user projects", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_PROJECTS_FAILED", e.getMessage()));
        }
    }
    
    @GetMapping("/public")
    @Operation(summary = "Get public projects", description = "Get publicly available projects")
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getPublicProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = Sort.by(sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Project> projects = projectService.findPublicProjects(pageable);
            Page<ProjectResponse> response = projects.map(ProjectResponse::from);
            
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            logger.error("Failed to get public projects", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_PUBLIC_PROJECTS_FAILED", e.getMessage()));
        }
    }
    
    @GetMapping("/{projectId}")
    @Operation(summary = "Get project by ID", description = "Get project details by ID")
    @PreAuthorize("@projectService.isUserMemberOfProject(authentication.principal.id, #projectId)")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(@PathVariable Long projectId) {
        
        try {
            Project project = projectService.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            
            ProjectResponse response = ProjectResponse.from(project);
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            logger.error("Failed to get project by ID: {}", projectId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_PROJECT_FAILED", e.getMessage()));
        }
    }
    
    @GetMapping("/key/{projectKey}")
    @Operation(summary = "Get project by key", description = "Get project details by key")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectByKey(
            @PathVariable String projectKey,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Project project = projectService.findByKey(projectKey)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            
            // Check if user has access
            if (!project.getIsPublic() && 
                !projectService.isUserMemberOfProject(currentUser.getId(), project.getId())) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("ACCESS_DENIED", "You don't have access to this project"));
            }
            
            ProjectResponse response = ProjectResponse.from(project);
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            logger.error("Failed to get project by key: {}", projectKey, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_PROJECT_FAILED", e.getMessage()));
        }
    }
    
    @PutMapping("/{projectId}")
    @Operation(summary = "Update project", description = "Update project details")
    @PreAuthorize("@projectService.getUserRoleInProject(authentication.principal.id, #projectId) != null and " +
                  "@projectService.getUserRoleInProject(authentication.principal.id, #projectId).name() in {'OWNER', 'ADMIN'}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectRequest request) {
        
        try {
            Project project = projectService.updateProject(
                    projectId,
                    request.getName(),
                    request.getDescription(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getIsPublic()
            );
            
            ProjectResponse response = ProjectResponse.from(project);
            return ResponseEntity.ok(ApiResponse.success(response, "Project updated successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to update project: {}", projectId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("PROJECT_UPDATE_FAILED", e.getMessage()));
        }
    }
    
    @PostMapping("/{projectId}/members")
    @Operation(summary = "Add project member", description = "Add a member to the project")
    @PreAuthorize("@projectService.getUserRoleInProject(authentication.principal.id, #projectId) != null and " +
                  "@projectService.getUserRoleInProject(authentication.principal.id, #projectId).name() in {'OWNER', 'ADMIN'}")
    public ResponseEntity<ApiResponse<Void>> addMember(
            @PathVariable Long projectId,
            @Valid @RequestBody AddMemberRequest request) {
        
        try {
            projectService.addMember(projectId, request.getUserId(), request.getRole());
            
            return ResponseEntity.ok(ApiResponse.success(null, "Member added successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to add member to project: {}", projectId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("ADD_MEMBER_FAILED", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{projectId}/members/{userId}")
    @Operation(summary = "Remove project member", description = "Remove a member from the project")
    @PreAuthorize("@projectService.getUserRoleInProject(authentication.principal.id, #projectId) != null and " +
                  "@projectService.getUserRoleInProject(authentication.principal.id, #projectId).name() in {'OWNER', 'ADMIN'}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        
        try {
            projectService.removeMember(projectId, userId);
            
            return ResponseEntity.ok(ApiResponse.success(null, "Member removed successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to remove member from project: {}", projectId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("REMOVE_MEMBER_FAILED", e.getMessage()));
        }
    }
    
    @PutMapping("/{projectId}/members/{userId}/role")
    @Operation(summary = "Update member role", description = "Update a member's role in the project")
    @PreAuthorize("@projectService.getUserRoleInProject(authentication.principal.id, #projectId) != null and " +
                  "@projectService.getUserRoleInProject(authentication.principal.id, #projectId).name() == 'OWNER'")
    public ResponseEntity<ApiResponse<Void>> updateMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestParam ProjectMember.Role role) {
        
        try {
            projectService.updateMemberRole(projectId, userId, role);
            
            return ResponseEntity.ok(ApiResponse.success(null, "Member role updated successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to update member role in project: {}", projectId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("UPDATE_MEMBER_ROLE_FAILED", e.getMessage()));
        }
    }
    
    @PutMapping("/{projectId}/archive")
    @Operation(summary = "Archive project", description = "Archive the project")
    @PreAuthorize("@projectService.getUserRoleInProject(authentication.principal.id, #projectId) != null and " +
                  "@projectService.getUserRoleInProject(authentication.principal.id, #projectId).name() == 'OWNER'")
    public ResponseEntity<ApiResponse<Void>> archiveProject(@PathVariable Long projectId) {
        
        try {
            projectService.archiveProject(projectId);
            
            return ResponseEntity.ok(ApiResponse.success(null, "Project archived successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to archive project: {}", projectId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("ARCHIVE_PROJECT_FAILED", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{projectId}")
    @Operation(summary = "Delete project", description = "Delete the project")
    @PreAuthorize("@projectService.getUserRoleInProject(authentication.principal.id, #projectId) != null and " +
                  "@projectService.getUserRoleInProject(authentication.principal.id, #projectId).name() == 'OWNER'")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long projectId) {
        
        try {
            projectService.deleteProject(projectId);
            
            return ResponseEntity.ok(ApiResponse.success(null, "Project deleted successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to delete project: {}", projectId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("DELETE_PROJECT_FAILED", e.getMessage()));
        }
    }
} 
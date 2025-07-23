package com.pms.controller;

import com.pms.dto.request.CreateIssueRequest;
import com.pms.dto.request.UpdateIssueRequest;
import com.pms.dto.request.ReorderIssuesRequest;
import com.pms.dto.request.CreateSubtaskRequest;
import com.pms.dto.response.ApiResponse;
import com.pms.dto.response.IssueResponse;
import com.pms.entity.Issue;
import com.pms.entity.User;
import com.pms.service.IssueService;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/issues")
@Tag(name = "Issue Management", description = "Issue management APIs")
public class IssueController {
    
    private static final Logger logger = LoggerFactory.getLogger(IssueController.class);
    
    @Autowired
    private IssueService issueService;
    
    @Autowired
    private ProjectService projectService;
    
    @PostMapping
    @Operation(summary = "Create issue", description = "Create a new issue")
    @PreAuthorize("@projectService.isUserMemberOfProject(authentication.principal.id, #request.projectId)")
    public ResponseEntity<ApiResponse<IssueResponse>> createIssue(
            @Valid @RequestBody CreateIssueRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Issue issue = issueService.createIssue(
                    request.getTitle(),
                    request.getDescription(),
                    request.getType(),
                    request.getPriority(),
                    request.getProjectId(),
                    currentUser.getId(),
                    request.getAssigneeId(),
                    request.getDueDate(),
                    request.getStoryPoints(),
                    request.getLabelIds()
            );
            
            IssueResponse response = IssueResponse.from(issue);
            return ResponseEntity.ok(ApiResponse.success(response, "Issue created successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to create issue", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("ISSUE_CREATION_FAILED", e.getMessage()));
        }
    }
    
    @GetMapping("/{issueId}")
    @Operation(summary = "Get issue by ID", description = "Get issue details by ID")
    public ResponseEntity<ApiResponse<IssueResponse>> getIssueById(
            @PathVariable Long issueId,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Issue issue = issueService.findById(issueId)
                    .orElseThrow(() -> new RuntimeException("Issue not found"));
            
            // Check if user has access to the project
            if (!projectService.isUserMemberOfProject(currentUser.getId(), issue.getProject().getId())) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("ACCESS_DENIED", "You don't have access to this issue"));
            }
            
            IssueResponse response = IssueResponse.from(issue);
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            logger.error("Failed to get issue by ID: {}", issueId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ISSUE_FAILED", e.getMessage()));
        }
    }
    
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get issues by project", description = "Get all issues for a project")
    @PreAuthorize("@projectService.isUserMemberOfProject(authentication.principal.id, #projectId)")
    public ResponseEntity<ApiResponse<Page<IssueResponse>>> getIssuesByProject(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Issue.Status status,
            @RequestParam(required = false) String search) {
        
        try {
            Sort sort = Sort.by(sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Issue> issues;
            if (search != null && !search.trim().isEmpty()) {
                issues = issueService.searchIssues(projectId, search, pageable);
            } else if (status != null) {
                issues = issueService.findIssuesByProjectAndStatus(projectId, status, pageable);
            } else {
                issues = issueService.findIssuesByProject(projectId, pageable);
            }
            
            Page<IssueResponse> response = issues.map(IssueResponse::from);
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            logger.error("Failed to get issues for project: {}", projectId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ISSUES_FAILED", e.getMessage()));
        }
    }
    
    @GetMapping("/project/{projectId}/kanban")
    @Operation(summary = "Get kanban issues", description = "Get issues for kanban board")
    @PreAuthorize("@projectService.isUserMemberOfProject(authentication.principal.id, #projectId)")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getKanbanIssues(@PathVariable Long projectId) {
        
        try {
            List<Issue> issues = issueService.getKanbanIssues(projectId);
            List<IssueResponse> response = issues.stream()
                    .map(IssueResponse::from)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            logger.error("Failed to get kanban issues for project: {}", projectId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_KANBAN_ISSUES_FAILED", e.getMessage()));
        }
    }
    
    @GetMapping("/assigned-to-me")
    @Operation(summary = "Get my assigned issues", description = "Get issues assigned to current user")
    public ResponseEntity<ApiResponse<Page<IssueResponse>>> getMyAssignedIssues(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = Sort.by(sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Issue> issues = issueService.findUserAssignedIssues(currentUser.getId(), pageable);
            Page<IssueResponse> response = issues.map(IssueResponse::from);
            
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            logger.error("Failed to get assigned issues for user: {}", currentUser.getId(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_ASSIGNED_ISSUES_FAILED", e.getMessage()));
        }
    }
    
    @GetMapping("/reported-by-me")
    @Operation(summary = "Get my reported issues", description = "Get issues reported by current user")
    public ResponseEntity<ApiResponse<Page<IssueResponse>>> getMyReportedIssues(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = Sort.by(sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Issue> issues = issueService.findUserReportedIssues(currentUser.getId(), pageable);
            Page<IssueResponse> response = issues.map(IssueResponse::from);
            
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            logger.error("Failed to get reported issues for user: {}", currentUser.getId(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_REPORTED_ISSUES_FAILED", e.getMessage()));
        }
    }
    
    @PutMapping("/{issueId}")
    @Operation(summary = "Update issue", description = "Update issue details")
    public ResponseEntity<ApiResponse<IssueResponse>> updateIssue(
            @PathVariable Long issueId,
            @Valid @RequestBody UpdateIssueRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Issue existingIssue = issueService.findById(issueId)
                    .orElseThrow(() -> new RuntimeException("Issue not found"));
            
            // Check if user has access to the project
            if (!projectService.isUserMemberOfProject(currentUser.getId(), existingIssue.getProject().getId())) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("ACCESS_DENIED", "You don't have access to this issue"));
            }
            
            Issue issue = issueService.updateIssue(
                    issueId,
                    request.getTitle(),
                    request.getDescription(),
                    request.getType(),
                    request.getPriority(),
                    request.getStatus(),
                    request.getAssigneeId(),
                    request.getDueDate(),
                    request.getStoryPoints(),
                    request.getLabelIds()
            );
            
            IssueResponse response = IssueResponse.from(issue);
            return ResponseEntity.ok(ApiResponse.success(response, "Issue updated successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to update issue: {}", issueId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("ISSUE_UPDATE_FAILED", e.getMessage()));
        }
    }
    
    @PutMapping("/{issueId}/status")
    @Operation(summary = "Update issue status", description = "Update issue status")
    public ResponseEntity<ApiResponse<IssueResponse>> updateIssueStatus(
            @PathVariable Long issueId,
            @RequestParam Issue.Status status,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Issue existingIssue = issueService.findById(issueId)
                    .orElseThrow(() -> new RuntimeException("Issue not found"));
            
            // Check if user has access to the project
            if (!projectService.isUserMemberOfProject(currentUser.getId(), existingIssue.getProject().getId())) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("ACCESS_DENIED", "You don't have access to this issue"));
            }
            
            Issue issue = issueService.updateIssueStatus(issueId, status);
            IssueResponse response = IssueResponse.from(issue);
            
            return ResponseEntity.ok(ApiResponse.success(response, "Issue status updated successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to update issue status: {}", issueId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("UPDATE_ISSUE_STATUS_FAILED", e.getMessage()));
        }
    }
    
    @PutMapping("/{issueId}/assign")
    @Operation(summary = "Assign issue", description = "Assign issue to a user")
    public ResponseEntity<ApiResponse<IssueResponse>> assignIssue(
            @PathVariable Long issueId,
            @RequestParam(required = false) Long assigneeId,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Issue existingIssue = issueService.findById(issueId)
                    .orElseThrow(() -> new RuntimeException("Issue not found"));
            
            // Check if user has access to the project
            if (!projectService.isUserMemberOfProject(currentUser.getId(), existingIssue.getProject().getId())) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("ACCESS_DENIED", "You don't have access to this issue"));
            }
            
            Issue issue = issueService.assignIssue(issueId, assigneeId);
            IssueResponse response = IssueResponse.from(issue);
            
            return ResponseEntity.ok(ApiResponse.success(response, "Issue assigned successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to assign issue: {}", issueId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("ASSIGN_ISSUE_FAILED", e.getMessage()));
        }
    }
    
    @PutMapping("/project/{projectId}/reorder")
    @Operation(summary = "Reorder issues", description = "Reorder issues in kanban board")
    @PreAuthorize("@projectService.isUserMemberOfProject(authentication.principal.id, #projectId)")
    public ResponseEntity<ApiResponse<Void>> reorderIssues(
            @PathVariable Long projectId,
            @Valid @RequestBody ReorderIssuesRequest request) {
        
        try {
            issueService.reorderIssues(projectId, request.getStatus(), request.getIssueIds());
            
            return ResponseEntity.ok(ApiResponse.success(null, "Issues reordered successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to reorder issues for project: {}", projectId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("REORDER_ISSUES_FAILED", e.getMessage()));
        }
    }
    
    @PostMapping("/{issueId}/subtasks")
    @Operation(summary = "Create subtask", description = "Create a subtask for an issue")
    public ResponseEntity<ApiResponse<IssueResponse>> createSubtask(
            @PathVariable Long issueId,
            @Valid @RequestBody CreateSubtaskRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Issue parentIssue = issueService.findById(issueId)
                    .orElseThrow(() -> new RuntimeException("Parent issue not found"));
            
            // Check if user has access to the project
            if (!projectService.isUserMemberOfProject(currentUser.getId(), parentIssue.getProject().getId())) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("ACCESS_DENIED", "You don't have access to this issue"));
            }
            
            Issue subtask = issueService.createSubtask(
                    issueId,
                    request.getTitle(),
                    request.getDescription(),
                    request.getAssigneeId()
            );
            
            IssueResponse response = IssueResponse.from(subtask);
            return ResponseEntity.ok(ApiResponse.success(response, "Subtask created successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to create subtask for issue: {}", issueId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("CREATE_SUBTASK_FAILED", e.getMessage()));
        }
    }
    
    @GetMapping("/{issueId}/subtasks")
    @Operation(summary = "Get subtasks", description = "Get subtasks for an issue")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getSubtasks(
            @PathVariable Long issueId,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Issue parentIssue = issueService.findById(issueId)
                    .orElseThrow(() -> new RuntimeException("Parent issue not found"));
            
            // Check if user has access to the project
            if (!projectService.isUserMemberOfProject(currentUser.getId(), parentIssue.getProject().getId())) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("ACCESS_DENIED", "You don't have access to this issue"));
            }
            
            List<Issue> subtasks = issueService.getSubtasks(issueId);
            List<IssueResponse> response = subtasks.stream()
                    .map(IssueResponse::from)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            logger.error("Failed to get subtasks for issue: {}", issueId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_SUBTASKS_FAILED", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{issueId}")
    @Operation(summary = "Delete issue", description = "Delete an issue")
    public ResponseEntity<ApiResponse<Void>> deleteIssue(
            @PathVariable Long issueId,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Issue issue = issueService.findById(issueId)
                    .orElseThrow(() -> new RuntimeException("Issue not found"));
            
            // Check if user has access to the project and is reporter or has admin role
            if (!projectService.isUserMemberOfProject(currentUser.getId(), issue.getProject().getId())) {
                return ResponseEntity.status(403)
                        .body(ApiResponse.error("ACCESS_DENIED", "You don't have access to this issue"));
            }
            
            // Only reporter or project admin/owner can delete
            if (!issue.getReporter().getId().equals(currentUser.getId())) {
                var userRole = projectService.getUserRoleInProject(currentUser.getId(), issue.getProject().getId());
                if (userRole == null || (!userRole.name().equals("ADMIN") && !userRole.name().equals("OWNER"))) {
                    return ResponseEntity.status(403)
                            .body(ApiResponse.error("ACCESS_DENIED", "Only reporter or project admin can delete issues"));
                }
            }
            
            issueService.deleteIssue(issueId);
            
            return ResponseEntity.ok(ApiResponse.success(null, "Issue deleted successfully"));
            
        } catch (Exception e) {
            logger.error("Failed to delete issue: {}", issueId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("DELETE_ISSUE_FAILED", e.getMessage()));
        }
    }
    
    @GetMapping("/upcoming-due-dates")
    @Operation(summary = "Get upcoming due dates", description = "Get issues with upcoming due dates")
    public ResponseEntity<ApiResponse<List<IssueResponse>>> getUpcomingDueDates(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "7") int days) {
        
        try {
            List<Issue> issues = issueService.getUpcomingDueDates(currentUser.getId(), days);
            List<IssueResponse> response = issues.stream()
                    .map(IssueResponse::from)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(response));
            
        } catch (Exception e) {
            logger.error("Failed to get upcoming due dates for user: {}", currentUser.getId(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("GET_UPCOMING_DUE_DATES_FAILED", e.getMessage()));
        }
    }
} 
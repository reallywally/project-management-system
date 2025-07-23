package com.pms.dto.request;

import com.pms.entity.Issue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class CreateIssueRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must not exceed 500 characters")
    private String title;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    private Issue.Type type = Issue.Type.TASK;
    
    private Issue.Priority priority = Issue.Priority.MEDIUM;
    
    @NotNull(message = "Project ID is required")
    private Long projectId;
    
    private Long assigneeId;
    
    private LocalDateTime dueDate;
    
    private BigDecimal storyPoints;
    
    private Set<Long> labelIds;
    
    // Constructors
    public CreateIssueRequest() {}
    
    public CreateIssueRequest(String title, String description, Issue.Type type, Issue.Priority priority,
                            Long projectId, Long assigneeId, LocalDateTime dueDate, 
                            BigDecimal storyPoints, Set<Long> labelIds) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
        this.dueDate = dueDate;
        this.storyPoints = storyPoints;
        this.labelIds = labelIds;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Issue.Type getType() {
        return type;
    }
    
    public void setType(Issue.Type type) {
        this.type = type;
    }
    
    public Issue.Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Issue.Priority priority) {
        this.priority = priority;
    }
    
    public Long getProjectId() {
        return projectId;
    }
    
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    
    public Long getAssigneeId() {
        return assigneeId;
    }
    
    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    
    public BigDecimal getStoryPoints() {
        return storyPoints;
    }
    
    public void setStoryPoints(BigDecimal storyPoints) {
        this.storyPoints = storyPoints;
    }
    
    public Set<Long> getLabelIds() {
        return labelIds;
    }
    
    public void setLabelIds(Set<Long> labelIds) {
        this.labelIds = labelIds;
    }
} 
package com.pms.dto.request;

import com.pms.entity.Issue;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class UpdateIssueRequest {
    
    @Size(max = 500, message = "Title must not exceed 500 characters")
    private String title;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    private Issue.Type type;
    
    private Issue.Priority priority;
    
    private Issue.Status status;
    
    private Long assigneeId;
    
    private LocalDateTime dueDate;
    
    private BigDecimal storyPoints;
    
    private Set<Long> labelIds;
    
    // Constructors
    public UpdateIssueRequest() {}
    
    public UpdateIssueRequest(String title, String description, Issue.Type type, Issue.Priority priority,
                            Issue.Status status, Long assigneeId, LocalDateTime dueDate, 
                            BigDecimal storyPoints, Set<Long> labelIds) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.status = status;
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
    
    public Issue.Status getStatus() {
        return status;
    }
    
    public void setStatus(Issue.Status status) {
        this.status = status;
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
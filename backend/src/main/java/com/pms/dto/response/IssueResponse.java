package com.pms.dto.response;

import com.pms.entity.Issue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class IssueResponse {
    
    private Long id;
    private String title;
    private String description;
    private Issue.Status status;
    private Issue.Priority priority;
    private Issue.Type type;
    private String issueNumber;
    private ProjectResponse project;
    private UserResponse assignee;
    private UserResponse reporter;
    private IssueResponse parentIssue;
    private List<IssueResponse> subtasks;
    private LocalDateTime dueDate;
    private BigDecimal storyPoints;
    private Integer position;
    private List<LabelResponse> labels;
    private List<CommentResponse> comments;
    private List<AttachmentResponse> attachments;
    private int completedSubtaskCount;
    private double subtaskProgress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public IssueResponse() {}
    
    // Static factory method
    public static IssueResponse from(Issue issue) {
        IssueResponse response = new IssueResponse();
        response.setId(issue.getId());
        response.setTitle(issue.getTitle());
        response.setDescription(issue.getDescription());
        response.setStatus(issue.getStatus());
        response.setPriority(issue.getPriority());
        response.setType(issue.getType());
        response.setIssueNumber(issue.getIssueNumber());
        response.setProject(ProjectResponse.from(issue.getProject()));
        response.setAssignee(issue.getAssignee() != null ? UserResponse.basicInfo(issue.getAssignee()) : null);
        response.setReporter(issue.getReporter() != null ? UserResponse.basicInfo(issue.getReporter()) : null);
        response.setParentIssue(issue.getParentIssue() != null ? IssueResponse.basicInfo(issue.getParentIssue()) : null);
        response.setSubtasks(issue.getSubtasks().stream()
                .map(IssueResponse::basicInfo)
                .collect(Collectors.toList()));
        response.setDueDate(issue.getDueDate());
        response.setStoryPoints(issue.getStoryPoints());
        response.setPosition(issue.getPosition());
        response.setLabels(issue.getLabels().stream()
                .map(LabelResponse::from)
                .collect(Collectors.toList()));
        response.setComments(issue.getComments().stream()
                .filter(comment -> !comment.getIsDeleted())
                .map(CommentResponse::from)
                .collect(Collectors.toList()));
        response.setAttachments(issue.getAttachments().stream()
                .map(AttachmentResponse::from)
                .collect(Collectors.toList()));
        response.setCompletedSubtaskCount(issue.getCompletedSubtaskCount());
        response.setSubtaskProgress(issue.getSubtaskProgress());
        response.setCreatedAt(issue.getCreatedAt());
        response.setUpdatedAt(issue.getUpdatedAt());
        
        return response;
    }
    
    // Static factory method for basic info (to avoid circular references)
    public static IssueResponse basicInfo(Issue issue) {
        IssueResponse response = new IssueResponse();
        response.setId(issue.getId());
        response.setTitle(issue.getTitle());
        response.setStatus(issue.getStatus());
        response.setPriority(issue.getPriority());
        response.setType(issue.getType());
        response.setIssueNumber(issue.getIssueNumber());
        response.setAssignee(issue.getAssignee() != null ? UserResponse.basicInfo(issue.getAssignee()) : null);
        response.setDueDate(issue.getDueDate());
        response.setStoryPoints(issue.getStoryPoints());
        response.setPosition(issue.getPosition());
        response.setCreatedAt(issue.getCreatedAt());
        response.setUpdatedAt(issue.getUpdatedAt());
        
        return response;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public Issue.Status getStatus() {
        return status;
    }
    
    public void setStatus(Issue.Status status) {
        this.status = status;
    }
    
    public Issue.Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Issue.Priority priority) {
        this.priority = priority;
    }
    
    public Issue.Type getType() {
        return type;
    }
    
    public void setType(Issue.Type type) {
        this.type = type;
    }
    
    public String getIssueNumber() {
        return issueNumber;
    }
    
    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }
    
    public ProjectResponse getProject() {
        return project;
    }
    
    public void setProject(ProjectResponse project) {
        this.project = project;
    }
    
    public UserResponse getAssignee() {
        return assignee;
    }
    
    public void setAssignee(UserResponse assignee) {
        this.assignee = assignee;
    }
    
    public UserResponse getReporter() {
        return reporter;
    }
    
    public void setReporter(UserResponse reporter) {
        this.reporter = reporter;
    }
    
    public IssueResponse getParentIssue() {
        return parentIssue;
    }
    
    public void setParentIssue(IssueResponse parentIssue) {
        this.parentIssue = parentIssue;
    }
    
    public List<IssueResponse> getSubtasks() {
        return subtasks;
    }
    
    public void setSubtasks(List<IssueResponse> subtasks) {
        this.subtasks = subtasks;
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
    
    public Integer getPosition() {
        return position;
    }
    
    public void setPosition(Integer position) {
        this.position = position;
    }
    
    public List<LabelResponse> getLabels() {
        return labels;
    }
    
    public void setLabels(List<LabelResponse> labels) {
        this.labels = labels;
    }
    
    public List<CommentResponse> getComments() {
        return comments;
    }
    
    public void setComments(List<CommentResponse> comments) {
        this.comments = comments;
    }
    
    public List<AttachmentResponse> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<AttachmentResponse> attachments) {
        this.attachments = attachments;
    }
    
    public int getCompletedSubtaskCount() {
        return completedSubtaskCount;
    }
    
    public void setCompletedSubtaskCount(int completedSubtaskCount) {
        this.completedSubtaskCount = completedSubtaskCount;
    }
    
    public double getSubtaskProgress() {
        return subtaskProgress;
    }
    
    public void setSubtaskProgress(double subtaskProgress) {
        this.subtaskProgress = subtaskProgress;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 
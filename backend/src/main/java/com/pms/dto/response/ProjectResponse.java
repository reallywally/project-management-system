package com.pms.dto.response;

import com.pms.entity.Project;
import com.pms.entity.ProjectMember;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectResponse {
    
    private Long id;
    private String name;
    private String key;
    private String description;
    private Project.Status status;
    private UserResponse owner;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPublic;
    private String avatarUrl;
    private List<ProjectMemberResponse> members;
    private int issueCount;
    private int memberCount;
    private double progressPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public ProjectResponse() {}
    
    // Static factory method
    public static ProjectResponse from(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setKey(project.getKey());
        response.setDescription(project.getDescription());
        response.setStatus(project.getStatus());
        response.setOwner(UserResponse.from(project.getOwner()));
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        response.setIsPublic(project.getIsPublic());
        // response.setAvatarUrl(project.getAvatarUrl());
        response.setMembers(project.getMembers().stream()
                .map(ProjectMemberResponse::from)
                .collect(Collectors.toList()));
        response.setIssueCount(project.getIssueCount());
        response.setMemberCount(project.getMemberCount());
        response.setProgressPercentage(project.getProgressPercentage());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());
        
        return response;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Project.Status getStatus() {
        return status;
    }
    
    public void setStatus(Project.Status status) {
        this.status = status;
    }
    
    public UserResponse getOwner() {
        return owner;
    }
    
    public void setOwner(UserResponse owner) {
        this.owner = owner;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public List<ProjectMemberResponse> getMembers() {
        return members;
    }
    
    public void setMembers(List<ProjectMemberResponse> members) {
        this.members = members;
    }
    
    public int getIssueCount() {
        return issueCount;
    }
    
    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }
    
    public int getMemberCount() {
        return memberCount;
    }
    
    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
    
    public double getProgressPercentage() {
        return progressPercentage;
    }
    
    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
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
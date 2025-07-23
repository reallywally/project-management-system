package com.pms.dto.response;

import com.pms.entity.ProjectMember;

import java.time.LocalDateTime;

public class ProjectMemberResponse {
    
    private Long id;
    private UserResponse user;
    private ProjectMember.Role role;
    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public ProjectMemberResponse() {}
    
    // Static factory method
    public static ProjectMemberResponse from(ProjectMember member) {
        ProjectMemberResponse response = new ProjectMemberResponse();
        response.setId(member.getId());
        response.setUser(UserResponse.basicInfo(member.getUser()));
        response.setRole(member.getRole());
        response.setJoinedAt(member.getJoinedAt());
        response.setCreatedAt(member.getCreatedAt());
        response.setUpdatedAt(member.getUpdatedAt());
        
        return response;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public UserResponse getUser() {
        return user;
    }
    
    public void setUser(UserResponse user) {
        this.user = user;
    }
    
    public ProjectMember.Role getRole() {
        return role;
    }
    
    public void setRole(ProjectMember.Role role) {
        this.role = role;
    }
    
    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
    
    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
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
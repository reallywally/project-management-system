package com.pms.dto.request;

import com.pms.entity.ProjectMember;
import jakarta.validation.constraints.NotNull;

public class AddMemberRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Role is required")
    private ProjectMember.Role role;
    
    // Constructors
    public AddMemberRequest() {}
    
    public AddMemberRequest(Long userId, ProjectMember.Role role) {
        this.userId = userId;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public ProjectMember.Role getRole() {
        return role;
    }
    
    public void setRole(ProjectMember.Role role) {
        this.role = role;
    }
} 
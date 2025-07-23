package com.pms.dto.request;

import com.pms.entity.Issue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ReorderIssuesRequest {
    
    @NotNull(message = "Status is required")
    private Issue.Status status;
    
    @NotEmpty(message = "Issue IDs list cannot be empty")
    private List<Long> issueIds;
    
    // Constructors
    public ReorderIssuesRequest() {}
    
    public ReorderIssuesRequest(Issue.Status status, List<Long> issueIds) {
        this.status = status;
        this.issueIds = issueIds;
    }
    
    // Getters and Setters
    public Issue.Status getStatus() {
        return status;
    }
    
    public void setStatus(Issue.Status status) {
        this.status = status;
    }
    
    public List<Long> getIssueIds() {
        return issueIds;
    }
    
    public void setIssueIds(List<Long> issueIds) {
        this.issueIds = issueIds;
    }
} 
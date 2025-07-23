package com.pms.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UpdateProjectRequest {
    
    @Size(max = 255, message = "Project name must not exceed 255 characters")
    private String name;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private Boolean isPublic;
    
    // Constructors
    public UpdateProjectRequest() {}
    
    public UpdateProjectRequest(String name, String description, 
                              LocalDate startDate, LocalDate endDate, Boolean isPublic) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPublic = isPublic;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
} 
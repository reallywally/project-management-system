package com.wally.response;

import com.wally.model.Project;
import lombok.Data;

@Data
public class ProjectResp {

    private Long id;
    private String name;
    private String category;
    private String description;
    private Long ownerId;

    public ProjectResp(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.category = project.getCategory();
        this.description = project.getDescription();
        this.ownerId = project.getOwnerId();
    }
}

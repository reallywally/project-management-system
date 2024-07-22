package com.wally.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wally.request.ProjectUpdate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String category;

    @ElementCollection
    private List<String> tags = new ArrayList<>();

//    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
//    private Chat chat;

    private Long ownerId;

//    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Issue> issues = new ArrayList<>();

//    @ManyToMany
//    private List<User> team = new ArrayList<>();

    @Builder
    public Project(String name, String description, String category, List<String> tags, Long ownerId) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.tags = tags;
        this.ownerId = ownerId;
    }

    public void update(ProjectUpdate projectUpdate){
        name = projectUpdate.getName();
        description = projectUpdate.getDescription();
        category = projectUpdate.getCategory();
        tags = projectUpdate.getTags();
    }
}

package com.wally.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDate dueDate;
    private List<String> tag = new ArrayList<>();

//    @ManyToOne
//    private User assignee;
    private Long assigneeId;

//    @JsonIgnore
//    @ManyToOne
//    private Project project;
    private Long projectId;

//    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Comment> comments = new ArrayList<>();
}

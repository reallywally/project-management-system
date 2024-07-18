package com.wally.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class IssueDto {
    private Long id;
    private String title;
    private String description;
    private String status;
    private Long projectId;
    private String priority;
    private LocalDate dueDate;
    private List<String> tag = new ArrayList<>();

    private User assignee;
    private Project project;

    private List<Comment> comments = new ArrayList<>();

}

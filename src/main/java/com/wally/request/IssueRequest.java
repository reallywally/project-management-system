package com.wally.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class IssueRequest {
    private String title;
    private String description;
    private String status;
    private String priority;
    private Long assigneeId;
    private Long projectId;
    private LocalDate dueDate;
}

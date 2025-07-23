package com.pms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "issue", indexes = {
    @Index(name = "idx_project_id", columnList = "project_id"),
    @Index(name = "idx_assignee_id", columnList = "assignee_id"),
    @Index(name = "idx_reporter_id", columnList = "reporter_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_priority", columnList = "priority"),
    @Index(name = "idx_parent_issue_id", columnList = "parent_issue_id"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_project_status_priority", columnList = "project_id, status, priority")
})
public class Issue extends BaseEntity {

    public enum Status {
        TODO, IN_PROGRESS, IN_REVIEW, TESTING, DONE, CLOSED
    }

    public enum Priority {
        LOWEST, LOW, MEDIUM, HIGH, HIGHEST
    }

    public enum Type {
        STORY, BUG, TASK, EPIC, SUBTASK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    @NotBlank
    @Size(max = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type = Type.TASK;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_issue_id")
    private Issue parentIssue;

    @OneToMany(mappedBy = "parentIssue", cascade = CascadeType.ALL)
    private List<Issue> subtasks = new ArrayList<>();

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "story_points", precision = 3, scale = 1)
    private BigDecimal storyPoints;

    @Column(nullable = false)
    private Integer position = 0;

    @ManyToMany
    @JoinTable(
        name = "issue_label",
        joinColumns = @JoinColumn(name = "issue_id"),
        inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    // Constructors
    public Issue() {}

    public Issue(String title, Project project, User reporter) {
        this.title = title;
        this.project = project;
        this.reporter = reporter;
    }

    // Helper methods
    public String getIssueNumber() {
        return project.getKey() + "-" + id;
    }

    public void addLabel(Label label) {
        labels.add(label);
        label.getIssues().add(this);
    }

    public void removeLabel(Label label) {
        labels.remove(label);
        label.getIssues().remove(this);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setIssue(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setIssue(null);
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
        attachment.setIssue(this);
    }

    public void removeAttachment(Attachment attachment) {
        attachments.remove(attachment);
        attachment.setIssue(null);
    }

    public void addSubtask(Issue subtask) {
        subtasks.add(subtask);
        subtask.setParentIssue(this);
        subtask.setType(Type.SUBTASK);
    }

    public void removeSubtask(Issue subtask) {
        subtasks.remove(subtask);
        subtask.setParentIssue(null);
    }

    public boolean isCompleted() {
        return status == Status.DONE || status == Status.CLOSED;
    }

    public boolean isSubtask() {
        return type == Type.SUBTASK && parentIssue != null;
    }

    public int getCompletedSubtaskCount() {
        return (int) subtasks.stream().filter(Issue::isCompleted).count();
    }

    public double getSubtaskProgress() {
        if (subtasks.isEmpty()) {
            return 0.0;
        }
        return (double) getCompletedSubtaskCount() / subtasks.size() * 100;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public Issue getParentIssue() {
        return parentIssue;
    }

    public void setParentIssue(Issue parentIssue) {
        this.parentIssue = parentIssue;
    }

    public List<Issue> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Issue> subtasks) {
        this.subtasks = subtasks;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(BigDecimal storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Set<Label> getLabels() {
        return labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue issue = (Issue) o;
        return Objects.equals(id, issue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", type=" + type +
                '}';
    }
} 
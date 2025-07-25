package com.pms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"project", "assignee", "reporter", "parentIssue", "subIssues", "comments", "attachments", "labels"})
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
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 500, message = "제목은 500자 이하여야 합니다")
    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.TODO;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private Type type = Type.TASK;

    @Column(name = "story_points", precision = 5, scale = 2)
    private BigDecimal storyPoints;

    @Column(name = "original_estimate")
    private Integer originalEstimate;

    @Column(name = "remaining_estimate")
    private Integer remainingEstimate;

    @Column(name = "time_spent")
    private Integer timeSpent;

    @Column(name = "position", nullable = false)
    private Integer position = 0;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "resolution")
    private String resolution;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_issue_id")
    private Issue parentIssue;

    @OneToMany(mappedBy = "parentIssue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Issue> subIssues = new ArrayList<>();

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "issue_label",
        joinColumns = @JoinColumn(name = "issue_id"),
        inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();

    // 편의 메서드들
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

    public void addLabel(Label label) {
        labels.add(label);
        label.getIssues().add(this);
    }

    public void removeLabel(Label label) {
        labels.remove(label);
        label.getIssues().remove(this);
    }

    public void addSubIssue(Issue subIssue) {
        subIssues.add(subIssue);
        subIssue.setParentIssue(this);
    }

    public void removeSubIssue(Issue subIssue) {
        subIssues.remove(subIssue);
        subIssue.setParentIssue(null);
    }

    public boolean isSubIssue() {
        return parentIssue != null;
    }

    public boolean hasSubIssues() {
        return !subIssues.isEmpty();
    }

    public boolean isCompleted() {
        return status == Status.DONE || status == Status.CLOSED;
    }

    public boolean isInProgress() {
        return status == Status.IN_PROGRESS || status == Status.IN_REVIEW || status == Status.TESTING;
    }

    public void resolve(String resolution) {
        this.status = Status.DONE;
        this.resolution = resolution;
        this.resolvedAt = LocalDateTime.now();
    }

    public void close() {
        this.status = Status.CLOSED;
        if (resolvedAt == null) {
            this.resolvedAt = LocalDateTime.now();
        }
    }

    public void reopen() {
        this.status = Status.TODO;
        this.resolution = null;
        this.resolvedAt = null;
    }

    // IssueResponse에서 사용하는 편의 메서드들
    public String getIssueNumber() {
        return project != null ? project.getKey() + "-" + id : "UNKNOWN-" + id;
    }

    public List<Issue> getSubtasks() {
        return subIssues; // subtasks는 subIssues의 별칭
    }

    public void setSubtasks(List<Issue> subtasks) {
        this.subIssues = subtasks; // subtasks는 subIssues의 별칭
    }

    public int getCompletedSubtaskCount() {
        return (int) subIssues.stream()
                .filter(Issue::isCompleted)
                .count();
    }

    public double getSubtaskProgress() {
        if (subIssues.isEmpty()) {
            return 0.0;
        }
        return (double) getCompletedSubtaskCount() / subIssues.size() * 100.0;
    }
} 
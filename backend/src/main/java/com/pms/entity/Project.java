package com.pms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "project", indexes = {
    @Index(name = "idx_key", columnList = "project_key"),
    @Index(name = "idx_owner_id", columnList = "owner_id"),
    @Index(name = "idx_status", columnList = "status")
})
public class Project extends BaseEntity {

    public enum Status {
        ACTIVE, ARCHIVED, DELETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    private String name;

    @Column(name = "project_key", unique = true, nullable = false, length = 10)
    @NotBlank
    @Size(max = 10)
    private String key;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Issue> issues = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Label> labels = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ActivityLog> activityLogs = new ArrayList<>();

    // Constructors
    public Project() {}

    public Project(String name, String key, User owner) {
        this.name = name;
        this.key = key;
        this.owner = owner;
    }

    // Helper methods
    public void addMember(ProjectMember member) {
        members.add(member);
        member.setProject(this);
    }

    public void removeMember(ProjectMember member) {
        members.remove(member);
        member.setProject(null);
    }

    public void addIssue(Issue issue) {
        issues.add(issue);
        issue.setProject(this);
    }

    public void removeIssue(Issue issue) {
        issues.remove(issue);
        issue.setProject(null);
    }

    public void addLabel(Label label) {
        labels.add(label);
        label.setProject(this);
    }

    public void removeLabel(Label label) {
        labels.remove(label);
        label.setProject(null);
    }

    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    public boolean isArchived() {
        return status == Status.ARCHIVED;
    }

    public boolean isDeleted() {
        return status == Status.DELETED;
    }

    public int getMemberCount() {
        return members.size();
    }

    public int getIssueCount() {
        return issues.size();
    }

    public long getCompletedIssueCount() {
        return issues.stream()
                .filter(issue -> issue.getStatus() == Issue.Status.DONE || 
                               issue.getStatus() == Issue.Status.CLOSED)
                .count();
    }

    public double getProgressPercentage() {
        if (issues.isEmpty()) {
            return 0.0;
        }
        return (double) getCompletedIssueCount() / issues.size() * 100;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<ProjectMember> getMembers() {
        return members;
    }

    public void setMembers(List<ProjectMember> members) {
        this.members = members;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public List<ActivityLog> getActivityLogs() {
        return activityLogs;
    }

    public void setActivityLogs(List<ActivityLog> activityLogs) {
        this.activityLogs = activityLogs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", status=" + status +
                ", isPublic=" + isPublic +
                '}';
    }
} 
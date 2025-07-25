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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project", indexes = {
    @Index(name = "idx_key", columnList = "project_key"),
    @Index(name = "idx_owner_id", columnList = "owner_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"owner", "members", "issues", "labels", "notifications", "activityLogs"})
public class Project extends BaseEntity {

    public enum Status {
        ACTIVE, ARCHIVED, DELETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "프로젝트 이름은 필수입니다")
    @Size(min = 2, max = 100, message = "프로젝트 이름은 2~100자 사이여야 합니다")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "프로젝트 키는 필수입니다")
    @Size(min = 2, max = 10, message = "프로젝트 키는 2~10자 사이여야 합니다")
    @Column(name = "project_key", nullable = false, unique = true, length = 10)
    private String key;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "repository_url")
    private String repositoryUrl;

    @Column(name = "wiki_url")
    private String wikiUrl;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "color", length = 7)
    private String color;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Issue> issues = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Label> labels = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityLog> activityLogs = new ArrayList<>();

    // 편의 메서드들
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

    public boolean isOwner(User user) {
        return owner != null && owner.equals(user);
    }

    public boolean hasMember(User user) {
        return members.stream()
                .anyMatch(member -> member.getUser().equals(user));
    }

    public boolean isActive() {
        return Status.ACTIVE.equals(status);
    }

    public boolean isArchived() {
        return Status.ARCHIVED.equals(status);
    }

    public boolean isDeleted() {
        return Status.DELETED.equals(status);
    }

    public void archive() {
        this.status = Status.ARCHIVED;
    }

    public void restore() {
        this.status = Status.ACTIVE;
    }

    public void delete() {
        this.status = Status.DELETED;
    }

    // ProjectResponse에서 사용하는 편의 메서드들
    public int getIssueCount() {
        return issues.size();
    }

    public int getMemberCount() {
        return members.size();
    }

    public double getProgressPercentage() {
        if (issues.isEmpty()) {
            return 0.0;
        }
        
        long completedIssues = issues.stream()
                .filter(issue -> issue.getStatus() == Issue.Status.DONE || 
                               issue.getStatus() == Issue.Status.CLOSED)
                .count();
        
        return (double) completedIssues / issues.size() * 100.0;
    }

    public List<ProjectMember> getMembers() {
        return members;
    }
} 
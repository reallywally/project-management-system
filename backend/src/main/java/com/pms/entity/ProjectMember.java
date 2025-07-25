package com.pms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_member", 
       uniqueConstraints = @UniqueConstraint(name = "uk_project_user", columnNames = {"project_id", "user_id"}),
       indexes = {
           @Index(name = "idx_project_id", columnList = "project_id"),
           @Index(name = "idx_user_id", columnList = "user_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"project", "user"})
public class ProjectMember extends BaseEntity {

    public enum Role {
        OWNER, ADMIN, DEVELOPER, VIEWER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    // 편의 생성자
    public ProjectMember(Project project, User user, Role role) {
        this.project = project;
        this.user = user;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }

    // 편의 메서드들
    public boolean isOwner() {
        return role == Role.OWNER;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN || role == Role.OWNER;
    }

    public boolean canManageProject() {
        return role == Role.OWNER || role == Role.ADMIN;
    }

    public boolean canEditIssues() {
        return role != Role.VIEWER;
    }
} 
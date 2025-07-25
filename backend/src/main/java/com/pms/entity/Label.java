package com.pms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "label", 
       uniqueConstraints = @UniqueConstraint(name = "uk_project_label", columnNames = {"project_id", "name"}),
       indexes = @Index(name = "idx_project_id", columnList = "project_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"project", "issues"})
public class Label extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "라벨명은 필수입니다")
    @Size(max = 100, message = "라벨명은 100자 이하여야 합니다")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "올바른 색상 코드 형식이어야 합니다 (예: #FF0000)")
    @Column(name = "color", nullable = false, length = 7)
    private String color;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToMany(mappedBy = "labels")
    private Set<Issue> issues = new HashSet<>();

    // 편의 생성자
    public Label(String name, String color, Project project) {
        this.name = name;
        this.color = color;
        this.project = project;
    }

    // 편의 메서드들
    public void addIssue(Issue issue) {
        issues.add(issue);
        issue.getLabels().add(this);
    }

    public void removeIssue(Issue issue) {
        issues.remove(issue);
        issue.getLabels().remove(this);
    }

    public int getIssueCount() {
        return issues.size();
    }
} 
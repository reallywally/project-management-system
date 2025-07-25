package com.pms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "comment", indexes = {
    @Index(name = "idx_issue_id", columnList = "issue_id"),
    @Index(name = "idx_author_id", columnList = "author_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"issue", "author"})
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "댓글 내용은 필수입니다")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "edited_at")
    private java.time.LocalDateTime editedAt;

    // 편의 생성자
    public Comment(String content, Issue issue, User author) {
        this.content = content;
        this.issue = issue;
        this.author = author;
        this.isDeleted = false;
    }

    // 편의 메서드들
    public void edit(String newContent) {
        this.content = newContent;
        this.editedAt = java.time.LocalDateTime.now();
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

    public boolean isEdited() {
        return editedAt != null;
    }
} 
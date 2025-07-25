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
@Table(name = "attachment", indexes = {
    @Index(name = "idx_issue_id", columnList = "issue_id"),
    @Index(name = "idx_uploaded_by", columnList = "uploaded_by")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"issue", "uploadedBy"})
public class Attachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "원본 파일명은 필수입니다")
    @Column(name = "original_name", nullable = false)
    private String originalName;

    @NotBlank(message = "저장된 파일명은 필수입니다")
    @Column(name = "stored_name", nullable = false)
    private String storedName;

    @NotBlank(message = "파일 경로는 필수입니다")
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    // 편의 생성자
    public Attachment(String originalName, String storedName, String filePath, String contentType, Long fileSize, Issue issue, User uploadedBy) {
        this.originalName = originalName;
        this.storedName = storedName;
        this.filePath = filePath;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.issue = issue;
        this.uploadedBy = uploadedBy;
    }

    // 편의 메서드들
    public String getFileExtension() {
        int lastDotIndex = originalName.lastIndexOf('.');
        return lastDotIndex > 0 ? originalName.substring(lastDotIndex + 1).toLowerCase() : "";
    }

    public boolean isImage() {
        String extension = getFileExtension();
        return extension.matches("jpg|jpeg|png|gif|bmp|webp");
    }

    public String getFormattedFileSize() {
        if (fileSize == null) return "Unknown size";
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }
} 
package com.pms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "activity_log", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_project_id", columnList = "project_id"),
    @Index(name = "idx_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"user", "project"})
public class ActivityLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    // 편의 생성자
    public ActivityLog(String action, String entityType, Long entityId, User user) {
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.user = user;
    }

    public ActivityLog(String action, String entityType, Long entityId, String oldValue, String newValue, User user) {
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.user = user;
    }

    // 편의 메서드들
    public void setProject(Project project) {
        this.project = project;
    }

    public void setClientInfo(String ipAddress, String userAgent) {
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public boolean hasValueChange() {
        return oldValue != null || newValue != null;
    }
} 
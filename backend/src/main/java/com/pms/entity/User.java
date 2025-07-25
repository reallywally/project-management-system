package com.pms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "user", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_email_verification_token", columnList = "email_verification_token"),
    @Index(name = "idx_password_reset_token", columnList = "password_reset_token")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"password", "roles", "projectMemberships", "issues", "comments", "notifications", "activityLogs"})
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Email(message = "올바른 이메일 형식이 아닙니다")
    @NotBlank(message = "이메일은 필수입니다")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자리여야 합니다")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2~50자 사이여야 합니다")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Size(min = 2, max = 30, message = "닉네임은 2~30자 사이여야 합니다")
    @Column(name = "nickname", length = 30)
    private String nickname;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "position", length = 50)
    private String position;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    @Column(name = "email_verification_expires_at")
    private LocalDateTime emailVerificationExpiresAt;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_expires_at")
    private LocalDateTime passwordResetExpiresAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> projectMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY)
    private List<Issue> issues = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityLog> activityLogs = new ArrayList<>();

    // UserDetails 인터페이스 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountLockedUntil == null || accountLockedUntil.isBefore(LocalDateTime.now());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive && emailVerified;
    }

    // 편의 메서드들
    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.getUsers().remove(this);
    }

    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.accountLockedUntil = null;
    }

    public void lockAccount(int lockDurationMinutes) {
        this.accountLockedUntil = LocalDateTime.now().plusMinutes(lockDurationMinutes);
    }

    public boolean isAccountLocked() {
        return accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now());
    }
} 
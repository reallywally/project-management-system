package com.pms;

import com.pms.entity.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestDataFactory {
    
    public static User createTestUser(String email, String name, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setNickname(name);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEmailVerified(true);
        user.setIsActive(true);
        return user;
    }
    
    public static Role createTestRole(String name) {
        Role role = new Role();
        role.setName(name);
        role.setDescription("Test " + name + " role");
        return role;
    }
    
    public static Project createTestProject(String name, String key, User owner) {
        Project project = new Project();
        project.setName(name);
        project.setKey(key);
        project.setDescription("Test project: " + name);
        project.setOwner(owner);
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusMonths(3));
        project.setIsPublic(false);
        project.setStatus(Project.Status.ACTIVE);
        return project;
    }
    
    public static ProjectMember createTestProjectMember(Project project, User user, ProjectMember.Role role) {
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(role);
        member.setJoinedAt(LocalDateTime.now());
        return member;
    }
    
    public static Issue createTestIssue(String title, Project project, User reporter) {
        Issue issue = new Issue();
        issue.setTitle(title);
        issue.setDescription("Test issue: " + title);
        issue.setType(Issue.Type.TASK);
        issue.setPriority(Issue.Priority.MEDIUM);
        issue.setStatus(Issue.Status.TODO);
        issue.setProject(project);
        issue.setReporter(reporter);
        issue.setPosition(0);
        issue.setStoryPoints(new BigDecimal("3"));
        return issue;
    }
    
    public static Label createTestLabel(String name, String color, Project project) {
        Label label = new Label();
        label.setName(name);
        label.setColor(color);
        label.setProject(project);
        return label;
    }
    
    public static Comment createTestComment(String content, Issue issue, User author) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setIssue(issue);
        comment.setAuthor(author);
        comment.setIsDeleted(false);
        return comment;
    }
} 
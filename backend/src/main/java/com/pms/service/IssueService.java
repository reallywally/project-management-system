package com.pms.service;

import com.pms.entity.Issue;
import com.pms.entity.Label;
import com.pms.entity.Project;
import com.pms.entity.User;
import com.pms.repository.IssueRepository;
import com.pms.repository.LabelRepository;
import com.pms.repository.ProjectRepository;
import com.pms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class IssueService {
    
    @Autowired
    private IssueRepository issueRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LabelRepository labelRepository;
    
    public Issue createIssue(String title, String description, Issue.Type type, Issue.Priority priority,
                           Long projectId, Long reporterId, Long assigneeId, LocalDateTime dueDate, 
                           BigDecimal storyPoints, Set<Long> labelIds) {
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("Reporter not found"));
        
        Issue issue = new Issue();
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setType(type != null ? type : Issue.Type.TASK);
        issue.setPriority(priority != null ? priority : Issue.Priority.MEDIUM);
        issue.setProject(project);
        issue.setReporter(reporter);
        issue.setDueDate(dueDate);
        issue.setStoryPoints(storyPoints);
        issue.setStatus(Issue.Status.TODO);
        
        // Set assignee if provided
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            issue.setAssignee(assignee);
        }
        
        // Calculate position (add to the end)
        List<Issue> existingIssues = issueRepository.findByProjectAndStatusOrderByPositionAsc(project, Issue.Status.TODO);
        issue.setPosition(existingIssues.size());
        
        Issue savedIssue = issueRepository.save(issue);
        
        // Add labels if provided
        if (labelIds != null && !labelIds.isEmpty()) {
            for (Long labelId : labelIds) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new RuntimeException("Label not found: " + labelId));
                savedIssue.addLabel(label);
            }
            savedIssue = issueRepository.save(savedIssue);
        }
        
        return savedIssue;
    }
    
    public Optional<Issue> findById(Long id) {
        return issueRepository.findById(id);
    }
    
    public Page<Issue> findIssuesByProject(Long projectId, Pageable pageable) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        return issueRepository.findByProject(project, pageable);
    }
    
    public Page<Issue> findIssuesByProjectAndStatus(Long projectId, Issue.Status status, Pageable pageable) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        return issueRepository.findByProjectAndStatus(project, status, pageable);
    }
    
    public Page<Issue> searchIssues(Long projectId, String query, Pageable pageable) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        return issueRepository.searchByProject(project, query, pageable);
    }
    
    public Page<Issue> findUserAssignedIssues(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return issueRepository.findByAssignee(user, pageable);
    }
    
    public Page<Issue> findUserReportedIssues(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return issueRepository.findByReporter(user, pageable);
    }
    
    public List<Issue> getKanbanIssues(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        return issueRepository.findByProjectOrderByPositionAsc(project);
    }
    
    public Issue updateIssue(Long issueId, String title, String description, Issue.Type type, 
                           Issue.Priority priority, Issue.Status status, Long assigneeId, 
                           LocalDateTime dueDate, BigDecimal storyPoints, Set<Long> labelIds) {
        
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        
        if (title != null) issue.setTitle(title);
        if (description != null) issue.setDescription(description);
        if (type != null) issue.setType(type);
        if (priority != null) issue.setPriority(priority);
        if (status != null) issue.setStatus(status);
        if (dueDate != null) issue.setDueDate(dueDate);
        if (storyPoints != null) issue.setStoryPoints(storyPoints);
        
        // Update assignee
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            issue.setAssignee(assignee);
        }
        
        // Update labels
        if (labelIds != null) {
            // Clear existing labels
            issue.getLabels().clear();
            
            // Add new labels
            for (Long labelId : labelIds) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new RuntimeException("Label not found: " + labelId));
                issue.addLabel(label);
            }
        }
        
        return issueRepository.save(issue);
    }
    
    public Issue updateIssueStatus(Long issueId, Issue.Status newStatus) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        
        Issue.Status oldStatus = issue.getStatus();
        issue.setStatus(newStatus);
        
        // Update position when moving between columns
        if (!oldStatus.equals(newStatus)) {
            List<Issue> newStatusIssues = issueRepository.findByProjectAndStatusOrderByPositionAsc(
                    issue.getProject(), newStatus);
            issue.setPosition(newStatusIssues.size());
        }
        
        return issueRepository.save(issue);
    }
    
    public Issue assignIssue(Long issueId, Long assigneeId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            issue.setAssignee(assignee);
        } else {
            issue.setAssignee(null);
        }
        
        return issueRepository.save(issue);
    }
    
    public void reorderIssues(Long projectId, Issue.Status status, List<Long> issueIds) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        for (int i = 0; i < issueIds.size(); i++) {
            Long issueId = issueIds.get(i);
            Issue issue = issueRepository.findById(issueId)
                    .orElseThrow(() -> new RuntimeException("Issue not found: " + issueId));
            
            issue.setStatus(status);
            issue.setPosition(i);
            issueRepository.save(issue);
        }
    }
    
    public Issue createSubtask(Long parentIssueId, String title, String description, Long assigneeId) {
        Issue parentIssue = issueRepository.findById(parentIssueId)
                .orElseThrow(() -> new RuntimeException("Parent issue not found"));
        
        Issue subtask = new Issue();
        subtask.setTitle(title);
        subtask.setDescription(description);
        subtask.setType(Issue.Type.SUBTASK);
        subtask.setPriority(parentIssue.getPriority());
        subtask.setProject(parentIssue.getProject());
        subtask.setReporter(parentIssue.getReporter());
        subtask.setParentIssue(parentIssue);
        subtask.setStatus(Issue.Status.TODO);
        
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            subtask.setAssignee(assignee);
        }
        
        // Calculate position
        List<Issue> existingSubtasks = issueRepository.findSubtasksByParentIssue(parentIssue);
        subtask.setPosition(existingSubtasks.size());
        
        return issueRepository.save(subtask);
    }
    
    public List<Issue> getSubtasks(Long parentIssueId) {
        Issue parentIssue = issueRepository.findById(parentIssueId)
                .orElseThrow(() -> new RuntimeException("Parent issue not found"));
        
        return issueRepository.findSubtasksByParentIssue(parentIssue);
    }
    
    public void deleteIssue(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));
        
        // Delete all subtasks first
        List<Issue> subtasks = issueRepository.findSubtasksByParentIssue(issue);
        for (Issue subtask : subtasks) {
            issueRepository.delete(subtask);
        }
        
        issueRepository.delete(issue);
    }
    
    public List<Issue> getUpcomingDueDates(Long userId, int days) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LocalDateTime deadline = LocalDateTime.now().plusDays(days);
        return issueRepository.findUpcomingDueDatesByUser(user, deadline);
    }
} 
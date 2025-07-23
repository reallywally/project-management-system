package com.pms.repository;

import com.pms.entity.Issue;
import com.pms.entity.Project;
import com.pms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    
    Page<Issue> findByProject(Project project, Pageable pageable);
    
    Page<Issue> findByProjectAndStatus(Project project, Issue.Status status, Pageable pageable);
    
    Page<Issue> findByAssignee(User assignee, Pageable pageable);
    
    Page<Issue> findByReporter(User reporter, Pageable pageable);
    
    Page<Issue> findByAssigneeAndStatus(User assignee, Issue.Status status, Pageable pageable);
    
    List<Issue> findByProjectOrderByPositionAsc(Project project);
    
    List<Issue> findByProjectAndStatusOrderByPositionAsc(Project project, Issue.Status status);
    
    @Query("SELECT i FROM Issue i WHERE i.assignee = :user AND i.dueDate <= :date AND i.status NOT IN ('DONE', 'CLOSED')")
    List<Issue> findUpcomingDueDatesByUser(@Param("user") User user, @Param("date") LocalDateTime date);
    
    @Query("SELECT i FROM Issue i WHERE i.project = :project AND " +
           "(LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Issue> searchByProject(@Param("project") Project project, @Param("query") String query, Pageable pageable);
    
    @Query("SELECT i FROM Issue i WHERE i.project = :project AND i.status = :status AND i.priority = :priority")
    Page<Issue> findByProjectAndStatusAndPriority(@Param("project") Project project, 
                                                 @Param("status") Issue.Status status,
                                                 @Param("priority") Issue.Priority priority,
                                                 Pageable pageable);
    
    @Query("SELECT COUNT(i) FROM Issue i WHERE i.project = :project AND i.status = :status")
    Long countByProjectAndStatus(@Param("project") Project project, @Param("status") Issue.Status status);
    
    @Query("SELECT i FROM Issue i WHERE i.parentIssue = :parentIssue ORDER BY i.position")
    List<Issue> findSubtasksByParentIssue(@Param("parentIssue") Issue parentIssue);
} 
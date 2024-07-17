package com.wally.repository;

import com.wally.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByProjectId(Long projectId);
}

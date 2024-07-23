package com.wally.repository;

import com.wally.model.ProjectUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    ProjectUser findByProjectIdAndUserId(Long projectId, Long userId);
}

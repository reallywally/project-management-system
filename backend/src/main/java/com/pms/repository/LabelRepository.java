package com.pms.repository;

import com.pms.entity.Label;
import com.pms.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    
    List<Label> findByProject(Project project);
    
    Optional<Label> findByProjectAndName(Project project, String name);
    
    Boolean existsByProjectAndName(Project project, String name);
} 
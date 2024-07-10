package com.wally.repository;

import com.wally.model.Project;
import com.wally.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwner(User user);

    List<Project> findByNameContainingAndTeamContaining(String partialName, User user);

    @Query("SELECT p FROM Project p join p.team t WHERE t=:user")
    List<Project> findByProjectByTeam(User user);

    List<Project> findByTeamContainingOrOwner(User user, User owner);
}

package com.pms.repository;

import com.pms.entity.Project;
import com.pms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    Optional<Project> findByKey(String key);
    
    Boolean existsByKey(String key);
    
    List<Project> findByOwner(User owner);
    
    List<Project> findByOwnerAndStatus(User owner, Project.Status status);
    
    List<Project> findByStatus(Project.Status status);
    
    @Query("SELECT p FROM Project p WHERE p.status = :status")
    Page<Project> findByStatus(@Param("status") Project.Status status, Pageable pageable);
    
    @Query("SELECT DISTINCT p FROM Project p " +
           "LEFT JOIN p.members pm " +
           "WHERE (p.owner = :user OR pm.user = :user) " +
           "AND p.status = :status")
    Page<Project> findUserProjectsByStatus(@Param("user") User user, 
                                         @Param("status") Project.Status status, 
                                         Pageable pageable);
    
    @Query("SELECT DISTINCT p FROM Project p " +
           "LEFT JOIN p.members pm " +
           "WHERE (p.owner = :user OR pm.user = :user) " +
           "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.key) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Project> searchUserProjects(@Param("user") User user, 
                                   @Param("query") String query, 
                                   Pageable pageable);

    // 별칭 메서드 - 테스트 호환성을 위해
    default Page<Project> findUserProjectsBySearch(User user, String query, Pageable pageable) {
        return searchUserProjects(user, query, pageable);
    }
    
    @Query("SELECT p FROM Project p WHERE p.isPublic = true AND p.status = 'ACTIVE'")
    Page<Project> findPublicProjects(Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.isPublic = :isPublic AND p.status = :status")
    Page<Project> findByIsPublicAndStatus(@Param("isPublic") Boolean isPublic, 
                                        @Param("status") Project.Status status, 
                                        Pageable pageable);

    // 별칭 메서드 - 테스트 호환성을 위해
    default Page<Project> findByIsPublicTrueAndStatus(Project.Status status, Pageable pageable) {
        return findByIsPublicAndStatus(true, status, pageable);
    }

    Page<Project> findByNameContainingIgnoreCaseAndStatus(String name, Project.Status status, Pageable pageable);
} 
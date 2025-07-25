package com.pms.repository;

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
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailVerificationToken(String token);
    
    Optional<User> findByPasswordResetToken(String token);
    
    Boolean existsByEmail(String email);
    
    Boolean existsByNickname(String nickname);
    
    Optional<User> findByEmailAndIsActiveTrue(String email);
    
    List<User> findByEmailContainingIgnoreCase(String email);
    
    List<User> findByNameContainingIgnoreCase(String name);
    
    List<User> findByRoles_Name(String roleName);
    
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.nickname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "u.isActive = true")
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    Page<User> findActiveUsers(Pageable pageable);
} 
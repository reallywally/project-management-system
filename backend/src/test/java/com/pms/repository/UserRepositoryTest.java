package com.pms.repository;

import com.pms.TestDataFactory;
import com.pms.config.TestConfig;
import com.pms.entity.Role;
import com.pms.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = TestDataFactory.createTestRole("USER");
        entityManager.persistAndFlush(userRole);

        testUser = TestDataFactory.createTestUser("test@example.com", "Test User", passwordEncoder);
        testUser.getRoles().add(userRole);
        entityManager.persistAndFlush(testUser);
        
        entityManager.clear();
    }

    @Test
    void 이메일로_사용자_조회_성공() {
        // When
        Optional<User> result = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        assertThat(result.get().getName()).isEqualTo("Test User");
    }

    @Test
    void 이메일로_사용자_조회_실패() {
        // When
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void 이메일_중복_체크_true() {
        // When
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void 이메일_중복_체크_false() {
        // When
        boolean exists = userRepository.existsByEmail("new@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test  
    void 닉네임_중복_체크_true() {
        // When
        boolean exists = userRepository.existsByNickname(testUser.getNickname());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void 닉네임_중복_체크_false() {
        // When
        boolean exists = userRepository.existsByNickname("New User Nickname");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void 이메일_인증_토큰으로_사용자_조회() {
        // Given
        String verificationToken = "verification-token-123";
        testUser.setEmailVerificationToken(verificationToken);
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> result = userRepository.findByEmailVerificationToken(verificationToken);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmailVerificationToken()).isEqualTo(verificationToken);
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void 이메일_인증_토큰으로_사용자_조회_실패() {
        // When
        Optional<User> result = userRepository.findByEmailVerificationToken("invalid-token");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void 비밀번호_재설정_토큰으로_사용자_조회() {
        // Given
        String resetToken = "reset-token-123";
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);
        testUser.setPasswordResetToken(resetToken);
        testUser.setPasswordResetExpiresAt(expiry);
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> result = userRepository.findByPasswordResetToken(resetToken);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getPasswordResetToken()).isEqualTo(resetToken);
        assertThat(result.get().getPasswordResetExpiresAt()).isEqualTo(expiry);
    }

    @Test
    void 활성_사용자만_조회() {
        // Given - 비활성 사용자 추가
        User inactiveUser = TestDataFactory.createTestUser("inactive@example.com", "Inactive User", passwordEncoder);
        inactiveUser.setIsActive(false);
        inactiveUser.getRoles().add(userRole);
        entityManager.persistAndFlush(inactiveUser);

        // When
        Optional<User> activeUser = userRepository.findByEmailAndIsActiveTrue("test@example.com");
        Optional<User> inactiveUserResult = userRepository.findByEmailAndIsActiveTrue("inactive@example.com");

        // Then
        assertThat(activeUser).isPresent();
        assertThat(inactiveUserResult).isEmpty();
    }

    @Test
    void 이메일_패턴으로_사용자_검색() {
        // Given - 추가 사용자들 생성
        User user2 = TestDataFactory.createTestUser("test2@example.com", "Test User 2", passwordEncoder);
        user2.getRoles().add(userRole);
        entityManager.persistAndFlush(user2);

        User user3 = TestDataFactory.createTestUser("another@different.com", "Another User", passwordEncoder);
        user3.getRoles().add(userRole);
        entityManager.persistAndFlush(user3);

        // When
        var results = userRepository.findByEmailContainingIgnoreCase("test");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting("email")
                .containsExactlyInAnyOrder("test@example.com", "test2@example.com");
    }

    @Test
    void 이름으로_사용자_검색() {
        // Given
        User user2 = TestDataFactory.createTestUser("user2@example.com", "Another Test User", passwordEncoder);
        user2.getRoles().add(userRole);
        entityManager.persistAndFlush(user2);

        // When
        var results = userRepository.findByNameContainingIgnoreCase("test");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting("name")
                .containsExactlyInAnyOrder("Test User", "Another Test User");
    }

    @Test
    void 사용자_생성_및_저장() {
        // Given
        User newUser = TestDataFactory.createTestUser("newuser@example.com", "New User", passwordEncoder);
        newUser.getRoles().add(userRole);
        newUser.setPhone("010-1234-5678");
        newUser.setEmailVerified(false);

        // When
        User savedUser = userRepository.save(newUser);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("newuser@example.com");
        assertThat(savedUser.getPhone()).isEqualTo("010-1234-5678");
        assertThat(savedUser.getEmailVerified()).isFalse();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    void 사용자_업데이트() {
        // Given
        String updatedName = "Updated User Name";
        String updatedPhone = "010-9876-5432";

        // When
        testUser.setName(updatedName);
        testUser.setPhone(updatedPhone);
        User updatedUser = userRepository.save(testUser);

        // Then
        assertThat(updatedUser.getName()).isEqualTo(updatedName);
        assertThat(updatedUser.getPhone()).isEqualTo(updatedPhone);
        assertThat(updatedUser.getUpdatedAt()).isAfter(updatedUser.getCreatedAt());
    }

    @Test
    void 사용자_삭제() {
        // Given
        Long userId = testUser.getId();

        // When
        userRepository.delete(testUser);

        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test 
    void 역할별_사용자_조회() {
        // Given - 관리자 역할 생성
        Role adminRole = TestDataFactory.createTestRole("ADMIN");
        entityManager.persistAndFlush(adminRole);

        User adminUser = TestDataFactory.createTestUser("admin@example.com", "Admin User", passwordEncoder);
        adminUser.getRoles().clear(); // 기본 역할 제거
        adminUser.getRoles().add(adminRole); // ADMIN 역할만 추가
        entityManager.persistAndFlush(adminUser);

        // When
        var userRoleUsers = userRepository.findByRoles_Name("USER");
        var adminRoleUsers = userRepository.findByRoles_Name("ADMIN");

        // Then
        assertThat(userRoleUsers).hasSize(1);
        assertThat(userRoleUsers.get(0).getEmail()).isEqualTo("test@example.com");
        
        assertThat(adminRoleUsers).hasSize(1);
        assertThat(adminRoleUsers.get(0).getEmail()).isEqualTo("admin@example.com");
    }
} 
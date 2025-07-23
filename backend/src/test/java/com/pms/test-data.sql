-- 테스트용 기본 역할 데이터
INSERT INTO roles (name, description, created_at, updated_at) VALUES 
('USER', 'Default user role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ADMIN', 'Administrator role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 테스트용 사용자 데이터 (비밀번호: password123)
INSERT INTO users (email, name, nickname, password, email_verified, is_active, created_at, updated_at) VALUES 
('test@example.com', 'Test User', 'TestUser', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin@example.com', 'Admin User', 'AdminUser', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 사용자-역할 관계
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1), -- test@example.com -> USER
(2, 1), -- admin@example.com -> USER  
(2, 2); -- admin@example.com -> ADMIN

-- 테스트용 프로젝트 데이터
INSERT INTO projects (name, project_key, description, owner_id, start_date, end_date, is_public, status, created_at, updated_at) VALUES 
('Sample Project', 'SP', 'This is a sample project for testing', 1, '2024-01-01', '2024-12-31', false, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Public Project', 'PP', 'This is a public project', 2, '2024-01-01', '2024-06-30', true, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 프로젝트 멤버십
INSERT INTO project_members (project_id, user_id, role, joined_at) VALUES 
(1, 1, 'OWNER', CURRENT_TIMESTAMP),
(1, 2, 'MEMBER', CURRENT_TIMESTAMP),
(2, 2, 'OWNER', CURRENT_TIMESTAMP);

-- 테스트용 라벨
INSERT INTO labels (name, color, project_id, created_at, updated_at) VALUES 
('Frontend', '#FF5733', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Backend', '#33FF57', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bug', '#FF3333', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Feature', '#3333FF', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 테스트용 이슈
INSERT INTO issues (title, description, type, priority, status, project_id, reporter_id, assignee_id, position, story_points, created_at, updated_at) VALUES 
('로그인 기능 구현', '사용자 로그인 기능을 구현해야 합니다.', 'FEATURE', 'HIGH', 'TODO', 1, 1, 2, 0, 5.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('비밀번호 재설정 버그', '비밀번호 재설정이 작동하지 않습니다.', 'BUG', 'CRITICAL', 'IN_PROGRESS', 1, 2, 1, 1, 3.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('대시보드 UI 개선', '대시보드 사용자 경험을 개선해야 합니다.', 'IMPROVEMENT', 'MEDIUM', 'REVIEW', 1, 1, 1, 2, 8.0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 이슈-라벨 관계
INSERT INTO issue_labels (issue_id, label_id) VALUES 
(1, 2), -- 로그인 기능 -> Backend
(2, 2), -- 비밀번호 재설정 -> Backend  
(2, 3), -- 비밀번호 재설정 -> Bug
(3, 1); -- 대시보드 UI -> Frontend

-- 테스트용 댓글
INSERT INTO comments (content, issue_id, author_id, is_deleted, created_at, updated_at) VALUES 
('이 기능은 JWT를 사용해서 구현하면 좋겠습니다.', 1, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('동의합니다. Spring Security와 함께 사용하죠.', 1, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('버그를 재현했습니다. 토큰 만료 처리에 문제가 있네요.', 2, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); 
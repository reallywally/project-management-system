# 데이터베이스 스키마 설계

## 1. ERD 개요

본 프로젝트 관리 시스템의 데이터베이스는 MySQL 8.0을 기반으로 설계되었으며, 사용자 관리, 프로젝트 관리, 이슈 트래킹, 알림 시스템을 지원하는 구조로 구성되어 있습니다.

## 2. 주요 엔티티 설계

### 2.1 USER (사용자)
사용자 정보를 관리하는 핵심 엔티티입니다.

```sql
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    avatar_url TEXT,
    phone VARCHAR(20),
    email_verified BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    email_verification_token VARCHAR(255),
    email_verification_expires_at DATETIME,
    password_reset_token VARCHAR(255),
    password_reset_expires_at DATETIME,
    
    INDEX idx_email (email),
    INDEX idx_email_verification_token (email_verification_token),
    INDEX idx_password_reset_token (password_reset_token)
);
```

**주요 속성:**
- `id`: 사용자 고유 식별자 (Primary Key)
- `email`: 로그인용 이메일 (Unique)
- `password`: 암호화된 비밀번호 (BCrypt)
- `email_verified`: 이메일 인증 여부
- `is_active`: 계정 활성화 상태

### 2.2 ROLE (역할)
시스템 내 역할 정의를 위한 엔티티입니다.

```sql
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 기본 역할 데이터
INSERT INTO role (name, description) VALUES 
('ADMIN', '시스템 관리자'),
('USER', '일반 사용자'),
('PROJECT_MANAGER', '프로젝트 관리자'),
('DEVELOPER', '개발자'),
('VIEWER', '읽기 전용 사용자');
```

### 2.3 USER_ROLE (사용자-역할 매핑)
사용자와 역할 간의 다대다 관계를 관리합니다.

```sql
CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);
```

### 2.4 PROJECT (프로젝트)
프로젝트 정보를 관리하는 엔티티입니다.

```sql
CREATE TABLE project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    `key` VARCHAR(10) NOT NULL UNIQUE,
    description TEXT,
    status ENUM('ACTIVE', 'ARCHIVED', 'DELETED') DEFAULT 'ACTIVE',
    owner_id BIGINT NOT NULL,
    start_date DATE,
    end_date DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_public BOOLEAN DEFAULT FALSE,
    avatar_url TEXT,
    
    FOREIGN KEY (owner_id) REFERENCES user(id),
    INDEX idx_key (`key`),
    INDEX idx_owner_id (owner_id),
    INDEX idx_status (status)
);
```

**주요 속성:**
- `key`: 프로젝트 식별 키 (예: PROJ, TEST) - 이슈 번호 생성에 사용
- `status`: 프로젝트 상태 (ACTIVE, ARCHIVED, DELETED)
- `is_public`: 공개 프로젝트 여부

### 2.5 PROJECT_MEMBER (프로젝트 멤버)
프로젝트와 사용자 간의 멤버십을 관리합니다.

```sql
CREATE TABLE project_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role ENUM('OWNER', 'ADMIN', 'DEVELOPER', 'VIEWER') NOT NULL,
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_project_user (project_id, user_id),
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_project_id (project_id),
    INDEX idx_user_id (user_id)
);
```

### 2.6 ISSUE (이슈)
프로젝트 내 이슈를 관리하는 핵심 엔티티입니다.

```sql
CREATE TABLE issue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    status ENUM('TODO', 'IN_PROGRESS', 'IN_REVIEW', 'TESTING', 'DONE', 'CLOSED') DEFAULT 'TODO',
    priority ENUM('LOWEST', 'LOW', 'MEDIUM', 'HIGH', 'HIGHEST') DEFAULT 'MEDIUM',
    type ENUM('STORY', 'BUG', 'TASK', 'EPIC', 'SUBTASK') DEFAULT 'TASK',
    project_id BIGINT NOT NULL,
    assignee_id BIGINT,
    reporter_id BIGINT NOT NULL,
    parent_issue_id BIGINT,
    due_date DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    story_points DECIMAL(3,1),
    position INTEGER DEFAULT 0,
    
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    FOREIGN KEY (assignee_id) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (reporter_id) REFERENCES user(id),
    FOREIGN KEY (parent_issue_id) REFERENCES issue(id) ON DELETE SET NULL,
    INDEX idx_project_id (project_id),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_reporter_id (reporter_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_parent_issue_id (parent_issue_id),
    INDEX idx_created_at (created_at)
);
```

**주요 속성:**
- `type`: 이슈 유형 (STORY, BUG, TASK, EPIC, SUBTASK)
- `status`: 이슈 상태 (칸반 보드 컬럼과 매핑)
- `priority`: 우선순위 (5단계)
- `parent_issue_id`: 서브태스크 관계를 위한 자기 참조
- `position`: 칸반 보드 내 정렬 순서

### 2.7 LABEL (라벨)
프로젝트별 이슈 라벨을 관리합니다.

```sql
CREATE TABLE label (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(7) NOT NULL, -- HEX color code (#FFFFFF)
    project_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_project_label (project_id, name),
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    INDEX idx_project_id (project_id)
);
```

### 2.8 ISSUE_LABEL (이슈-라벨 매핑)
이슈와 라벨 간의 다대다 관계를 관리합니다.

```sql
CREATE TABLE issue_label (
    issue_id BIGINT NOT NULL,
    label_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (issue_id, label_id),
    FOREIGN KEY (issue_id) REFERENCES issue(id) ON DELETE CASCADE,
    FOREIGN KEY (label_id) REFERENCES label(id) ON DELETE CASCADE
);
```

### 2.9 COMMENT (댓글)
이슈에 대한 댓글을 관리합니다.

```sql
CREATE TABLE comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    issue_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (issue_id) REFERENCES issue(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES user(id),
    INDEX idx_issue_id (issue_id),
    INDEX idx_author_id (author_id),
    INDEX idx_created_at (created_at)
);
```

### 2.10 ATTACHMENT (첨부파일)
이슈의 첨부파일을 관리합니다.

```sql
CREATE TABLE attachment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_name VARCHAR(255) NOT NULL,
    stored_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    content_type VARCHAR(100),
    file_size BIGINT,
    issue_id BIGINT NOT NULL,
    uploaded_by BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (issue_id) REFERENCES issue(id) ON DELETE CASCADE,
    FOREIGN KEY (uploaded_by) REFERENCES user(id),
    INDEX idx_issue_id (issue_id),
    INDEX idx_uploaded_by (uploaded_by)
);
```

### 2.11 NOTIFICATION (알림)
사용자 알림을 관리합니다.

```sql
CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('ISSUE_ASSIGNED', 'ISSUE_UPDATED', 'COMMENT_ADDED', 'PROJECT_INVITED', 'MENTION') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    user_id BIGINT NOT NULL,
    issue_id BIGINT,
    project_id BIGINT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    read_at DATETIME,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (issue_id) REFERENCES issue(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
);
```

### 2.12 ACTIVITY_LOG (활동 로그)
시스템 내 사용자 활동을 추적합니다.

```sql
CREATE TABLE activity_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    old_value TEXT,
    new_value TEXT,
    user_id BIGINT NOT NULL,
    project_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_project_id (project_id),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_created_at (created_at)
);
```

## 3. 주요 관계 설명

### 3.1 사용자 관계
- **User ↔ Role**: 다대다 관계 (USER_ROLE 테이블로 매핑)
- **User ↔ Project**: 소유 관계 (1:N) + 멤버십 관계 (N:M)

### 3.2 프로젝트 관계
- **Project ↔ Issue**: 1대다 관계 (프로젝트 당 여러 이슈)
- **Project ↔ Label**: 1대다 관계 (프로젝트별 라벨 관리)
- **Project ↔ ProjectMember**: 1대다 관계 (프로젝트 멤버십)

### 3.3 이슈 관계
- **Issue ↔ Issue**: 자기참조 관계 (부모-자식 이슈, 서브태스크)
- **Issue ↔ Label**: 다대다 관계 (ISSUE_LABEL 테이블로 매핑)
- **Issue ↔ Comment**: 1대다 관계 (이슈별 댓글)
- **Issue ↔ Attachment**: 1대다 관계 (이슈별 첨부파일)

## 4. 인덱스 전략

### 4.1 기본 인덱스
- 모든 Primary Key에는 자동으로 클러스터드 인덱스 생성
- Foreign Key에는 성능을 위한 인덱스 추가

### 4.2 복합 인덱스
```sql
-- 프로젝트별 이슈 조회 최적화
CREATE INDEX idx_project_status_priority ON issue(project_id, status, priority);

-- 사용자별 할당된 이슈 조회 최적화
CREATE INDEX idx_assignee_status ON issue(assignee_id, status);

-- 알림 조회 최적화
CREATE INDEX idx_user_read_created ON notification(user_id, is_read, created_at);
```

## 5. 제약 조건

### 5.1 데이터 무결성
- 모든 Foreign Key에 적절한 CASCADE 설정
- 필수 필드에 NOT NULL 제약조건
- 이메일, 프로젝트 키 등에 UNIQUE 제약조건

### 5.2 체크 제약조건
```sql
-- 프로젝트 종료일은 시작일보다 늦어야 함
ALTER TABLE project ADD CONSTRAINT chk_project_dates 
    CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date);

-- 스토리 포인트는 양수여야 함
ALTER TABLE issue ADD CONSTRAINT chk_story_points 
    CHECK (story_points IS NULL OR story_points >= 0);
```

## 6. 파티셔닝 전략

대용량 데이터 처리를 위한 파티셔닝 고려사항:

### 6.1 시간 기반 파티셔닝
```sql
-- NOTIFICATION 테이블 월별 파티셔닝
ALTER TABLE notification PARTITION BY RANGE (YEAR(created_at) * 100 + MONTH(created_at)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    -- ... 월별 파티션 추가
);

-- ACTIVITY_LOG 테이블 월별 파티셔닝
ALTER TABLE activity_log PARTITION BY RANGE (YEAR(created_at) * 100 + MONTH(created_at)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    -- ... 월별 파티션 추가
);
```

## 7. 성능 최적화

### 7.1 쿼리 최적화
- 자주 사용되는 쿼리에 대한 복합 인덱스 생성
- 대용량 텍스트 검색을 위한 Full-Text 인덱스 고려

### 7.2 캐싱 전략
- 자주 조회되는 프로젝트 정보는 Redis 캐시 활용
- 사용자 세션 정보는 Redis에 저장 
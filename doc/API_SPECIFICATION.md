# REST API 명세서

## 목차
1. [인증 API](#1-인증-api)
2. [사용자 API](#2-사용자-api)
3. [프로젝트 API](#3-프로젝트-api)
4. [이슈 API](#4-이슈-api)
5. [댓글 API](#5-댓글-api)
6. [알림 API](#6-알림-api)
7. [대시보드 API](#7-대시보드-api)
8. [검색 API](#8-검색-api)
9. [파일 업로드 API](#9-파일-업로드-api)

## API 공통 사항

### Base URL
```
Production: https://api.projectmanager.com/v1
Development: http://localhost:8080/api/v1
```

### 인증 방식
- JWT Bearer Token 방식 사용
- Header: `Authorization: Bearer {token}`

### 공통 응답 형식
```json
{
  "success": true,
  "data": {},
  "message": "Success message",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 에러 응답 형식
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Error message",
    "details": []
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

---

## 1. 인증 API

### 1.1 회원가입
**POST** `/auth/register`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123!",
  "name": "홍길동",
  "nickname": "길동이"
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": 1,
      "email": "user@example.com",
      "name": "홍길동",
      "nickname": "길동이",
      "emailVerified": false,
      "createdAt": "2024-01-01T00:00:00Z"
    },
    "message": "회원가입이 완료되었습니다. 이메일 인증을 진행해주세요."
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "입력값이 올바르지 않습니다.",
    "details": [
      {
        "field": "email",
        "message": "이미 사용 중인 이메일입니다."
      }
    ]
  }
}
```

### 1.2 로그인
**POST** `/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123!"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "email": "user@example.com",
      "name": "홍길동",
      "nickname": "길동이",
      "avatarUrl": "https://example.com/avatar.jpg",
      "roles": ["USER"]
    }
  }
}
```

### 1.3 토큰 재발급
**POST** `/auth/refresh`

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

### 1.4 로그아웃
**POST** `/auth/logout`

**Headers:** `Authorization: Bearer {token}`

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "로그아웃되었습니다."
}
```

### 1.5 비밀번호 재설정 요청
**POST** `/auth/forgot-password`

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "비밀번호 재설정 이메일이 발송되었습니다."
}
```

### 1.6 비밀번호 재설정
**POST** `/auth/reset-password`

**Request Body:**
```json
{
  "token": "reset-token-here",
  "newPassword": "newPassword123!"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "비밀번호가 성공적으로 변경되었습니다."
}
```

---

## 2. 사용자 API

### 2.1 내 정보 조회
**GET** `/users/me`

**Headers:** `Authorization: Bearer {token}`

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "nickname": "길동이",
    "avatarUrl": "https://example.com/avatar.jpg",
    "phone": "010-1234-5678",
    "emailVerified": true,
    "isActive": true,
    "roles": ["USER"],
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

### 2.2 내 정보 수정
**PUT** `/users/me`

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "name": "홍길동",
  "nickname": "새로운닉네임",
  "phone": "010-1234-5678"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "nickname": "새로운닉네임",
    "phone": "010-1234-5678",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

### 2.3 사용자 검색
**GET** `/users/search?q={query}&page={page}&size={size}`

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `q`: 검색어 (이름, 닉네임, 이메일)
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20, 최대: 100)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "email": "user@example.com",
        "name": "홍길동",
        "nickname": "길동이",
        "avatarUrl": "https://example.com/avatar.jpg"
      }
    ],
    "pageable": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

---

## 3. 프로젝트 API

### 3.1 프로젝트 목록 조회
**GET** `/projects?page={page}&size={size}&status={status}`

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)
- `status`: 프로젝트 상태 (ACTIVE, ARCHIVED, DELETED)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "프로젝트 관리 시스템",
        "key": "PMS",
        "description": "Jira와 유사한 프로젝트 관리 시스템",
        "status": "ACTIVE",
        "owner": {
          "id": 1,
          "name": "홍길동",
          "email": "user@example.com"
        },
        "memberCount": 5,
        "issueCount": 23,
        "isPublic": false,
        "avatarUrl": "https://example.com/project-avatar.jpg",
        "createdAt": "2024-01-01T00:00:00Z",
        "updatedAt": "2024-01-01T00:00:00Z"
      }
    ],
    "pageable": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

### 3.2 프로젝트 생성
**POST** `/projects`

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "name": "새 프로젝트",
  "key": "NEW",
  "description": "새로운 프로젝트입니다",
  "isPublic": false,
  "startDate": "2024-01-01",
  "endDate": "2024-12-31"
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": 2,
    "name": "새 프로젝트",
    "key": "NEW",
    "description": "새로운 프로젝트입니다",
    "status": "ACTIVE",
    "owner": {
      "id": 1,
      "name": "홍길동",
      "email": "user@example.com"
    },
    "isPublic": false,
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "createdAt": "2024-01-01T00:00:00Z"
  }
}
```

### 3.3 프로젝트 상세 조회
**GET** `/projects/{projectId}`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `projectId`: 프로젝트 ID

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "프로젝트 관리 시스템",
    "key": "PMS",
    "description": "Jira와 유사한 프로젝트 관리 시스템",
    "status": "ACTIVE",
    "owner": {
      "id": 1,
      "name": "홍길동",
      "email": "user@example.com"
    },
    "members": [
      {
        "id": 1,
        "user": {
          "id": 1,
          "name": "홍길동",
          "email": "user@example.com",
          "avatarUrl": "https://example.com/avatar.jpg"
        },
        "role": "OWNER",
        "joinedAt": "2024-01-01T00:00:00Z"
      }
    ],
    "labels": [
      {
        "id": 1,
        "name": "버그",
        "color": "#FF0000"
      },
      {
        "id": 2,
        "name": "기능",
        "color": "#00FF00"
      }
    ],
    "statistics": {
      "totalIssues": 23,
      "todoIssues": 5,
      "inProgressIssues": 3,
      "doneIssues": 15
    },
    "isPublic": false,
    "avatarUrl": "https://example.com/project-avatar.jpg",
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

### 3.4 프로젝트 수정
**PUT** `/projects/{projectId}`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `projectId`: 프로젝트 ID

**Request Body:**
```json
{
  "name": "수정된 프로젝트 이름",
  "description": "수정된 설명",
  "isPublic": true,
  "startDate": "2024-01-01",
  "endDate": "2024-12-31"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "수정된 프로젝트 이름",
    "description": "수정된 설명",
    "isPublic": true,
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

### 3.5 프로젝트 멤버 초대
**POST** `/projects/{projectId}/members`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `projectId`: 프로젝트 ID

**Request Body:**
```json
{
  "userEmail": "newmember@example.com",
  "role": "DEVELOPER"
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": 2,
    "user": {
      "id": 2,
      "name": "김개발",
      "email": "newmember@example.com",
      "avatarUrl": "https://example.com/avatar2.jpg"
    },
    "role": "DEVELOPER",
    "joinedAt": "2024-01-01T00:00:00Z"
  }
}
```

### 3.6 프로젝트 멤버 역할 변경
**PUT** `/projects/{projectId}/members/{memberId}`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `projectId`: 프로젝트 ID
- `memberId`: 멤버 ID

**Request Body:**
```json
{
  "role": "ADMIN"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 2,
    "role": "ADMIN",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

### 3.7 프로젝트 멤버 제거
**DELETE** `/projects/{projectId}/members/{memberId}`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `projectId`: 프로젝트 ID
- `memberId`: 멤버 ID

**Success Response (204 No Content):**
```json
{
  "success": true,
  "message": "멤버가 제거되었습니다."
}
```

---

## 4. 이슈 API

### 4.1 이슈 목록 조회
**GET** `/projects/{projectId}/issues?page={page}&size={size}&status={status}&assignee={assigneeId}&priority={priority}&type={type}`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `projectId`: 프로젝트 ID

**Query Parameters:**
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)
- `status`: 이슈 상태
- `assignee`: 담당자 ID
- `priority`: 우선순위
- `type`: 이슈 유형

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "로그인 기능 구현",
        "description": "JWT 기반 로그인 기능을 구현합니다.",
        "status": "IN_PROGRESS",
        "priority": "HIGH",
        "type": "STORY",
        "issueNumber": "PMS-1",
        "assignee": {
          "id": 1,
          "name": "홍길동",
          "email": "user@example.com",
          "avatarUrl": "https://example.com/avatar.jpg"
        },
        "reporter": {
          "id": 1,
          "name": "홍길동",
          "email": "user@example.com"
        },
        "labels": [
          {
            "id": 2,
            "name": "기능",
            "color": "#00FF00"
          }
        ],
        "storyPoints": 5.0,
        "dueDate": "2024-01-15T00:00:00Z",
        "createdAt": "2024-01-01T00:00:00Z",
        "updatedAt": "2024-01-01T00:00:00Z",
        "commentCount": 3,
        "attachmentCount": 1,
        "subtaskCount": 2
      }
    ],
    "pageable": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

### 4.2 이슈 생성
**POST** `/projects/{projectId}/issues`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `projectId`: 프로젝트 ID

**Request Body:**
```json
{
  "title": "새로운 이슈",
  "description": "이슈 설명입니다.",
  "type": "TASK",
  "priority": "MEDIUM",
  "assigneeId": 2,
  "labelIds": [1, 2],
  "dueDate": "2024-01-15T00:00:00Z",
  "storyPoints": 3.0,
  "parentIssueId": null
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": 2,
    "title": "새로운 이슈",
    "description": "이슈 설명입니다.",
    "status": "TODO",
    "priority": "MEDIUM",
    "type": "TASK",
    "issueNumber": "PMS-2",
    "assignee": {
      "id": 2,
      "name": "김개발",
      "email": "dev@example.com"
    },
    "reporter": {
      "id": 1,
      "name": "홍길동",
      "email": "user@example.com"
    },
    "labels": [
      {
        "id": 1,
        "name": "버그",
        "color": "#FF0000"
      }
    ],
    "storyPoints": 3.0,
    "dueDate": "2024-01-15T00:00:00Z",
    "createdAt": "2024-01-01T00:00:00Z"
  }
}
```

### 4.3 이슈 상세 조회
**GET** `/issues/{issueId}`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `issueId`: 이슈 ID

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "로그인 기능 구현",
    "description": "# 로그인 기능 구현\n\n## 요구사항\n- JWT 기반 인증\n- 이메일/비밀번호 로그인",
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "type": "STORY",
    "issueNumber": "PMS-1",
    "project": {
      "id": 1,
      "name": "프로젝트 관리 시스템",
      "key": "PMS"
    },
    "assignee": {
      "id": 1,
      "name": "홍길동",
      "email": "user@example.com",
      "avatarUrl": "https://example.com/avatar.jpg"
    },
    "reporter": {
      "id": 1,
      "name": "홍길동",
      "email": "user@example.com"
    },
    "labels": [
      {
        "id": 2,
        "name": "기능",
        "color": "#00FF00"
      }
    ],
    "parentIssue": null,
    "subtasks": [
      {
        "id": 3,
        "title": "로그인 API 개발",
        "status": "DONE",
        "assignee": {
          "id": 1,
          "name": "홍길동"
        }
      }
    ],
    "storyPoints": 5.0,
    "position": 1,
    "dueDate": "2024-01-15T00:00:00Z",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z",
    "attachments": [
      {
        "id": 1,
        "originalName": "login-mockup.png",
        "fileSize": 1024000,
        "contentType": "image/png",
        "downloadUrl": "/api/v1/attachments/1/download"
      }
    ]
  }
}
```

### 4.4 이슈 수정
**PUT** `/issues/{issueId}`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `issueId`: 이슈 ID

**Request Body:**
```json
{
  "title": "수정된 이슈 제목",
  "description": "수정된 설명",
  "status": "IN_REVIEW",
  "priority": "HIGHEST",
  "assigneeId": 3,
  "labelIds": [1],
  "dueDate": "2024-01-20T00:00:00Z",
  "storyPoints": 8.0
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "수정된 이슈 제목",
    "description": "수정된 설명",
    "status": "IN_REVIEW",
    "priority": "HIGHEST",
    "assignee": {
      "id": 3,
      "name": "박테스터",
      "email": "tester@example.com"
    },
    "storyPoints": 8.0,
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

### 4.5 이슈 상태 변경 (칸반 보드)
**PATCH** `/issues/{issueId}/status`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `issueId`: 이슈 ID

**Request Body:**
```json
{
  "status": "DONE",
  "position": 2
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "status": "DONE",
    "position": 2,
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

### 4.6 이슈 삭제
**DELETE** `/issues/{issueId}`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `issueId`: 이슈 ID

**Success Response (204 No Content):**
```json
{
  "success": true,
  "message": "이슈가 삭제되었습니다."
}
```

---

## 5. 댓글 API

### 5.1 댓글 목록 조회
**GET** `/issues/{issueId}/comments?page={page}&size={size}`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `issueId`: 이슈 ID

**Query Parameters:**
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "content": "이 기능에 대해 추가 요구사항이 있습니다.",
        "author": {
          "id": 1,
          "name": "홍길동",
          "email": "user@example.com",
          "avatarUrl": "https://example.com/avatar.jpg"
        },
        "createdAt": "2024-01-01T00:00:00Z",
        "updatedAt": "2024-01-01T00:00:00Z",
        "isDeleted": false
      }
    ],
    "pageable": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

### 5.2 댓글 작성
**POST** `/issues/{issueId}/comments`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `issueId`: 이슈 ID

**Request Body:**
```json
{
  "content": "새로운 댓글입니다."
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": 2,
    "content": "새로운 댓글입니다.",
    "author": {
      "id": 1,
      "name": "홍길동",
      "email": "user@example.com",
      "avatarUrl": "https://example.com/avatar.jpg"
    },
    "createdAt": "2024-01-01T00:00:00Z"
  }
}
```

### 5.3 댓글 수정
**PUT** `/comments/{commentId}`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `commentId`: 댓글 ID

**Request Body:**
```json
{
  "content": "수정된 댓글 내용입니다."
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "content": "수정된 댓글 내용입니다.",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

### 5.4 댓글 삭제
**DELETE** `/comments/{commentId}`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `commentId`: 댓글 ID

**Success Response (204 No Content):**
```json
{
  "success": true,
  "message": "댓글이 삭제되었습니다."
}
```

---

## 6. 알림 API

### 6.1 알림 목록 조회
**GET** `/notifications?page={page}&size={size}&isRead={isRead}`

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)
- `isRead`: 읽음 여부 (true/false)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "type": "ISSUE_ASSIGNED",
        "title": "새로운 이슈가 할당되었습니다",
        "message": "PMS-1: 로그인 기능 구현 이슈가 할당되었습니다.",
        "isRead": false,
        "issue": {
          "id": 1,
          "title": "로그인 기능 구현",
          "issueNumber": "PMS-1"
        },
        "project": {
          "id": 1,
          "name": "프로젝트 관리 시스템"
        },
        "createdAt": "2024-01-01T00:00:00Z"
      }
    ],
    "pageable": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1
    },
    "unreadCount": 5
  }
}
```

### 6.2 알림 읽음 처리
**PATCH** `/notifications/{notificationId}/read`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `notificationId`: 알림 ID

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "isRead": true,
    "readAt": "2024-01-01T00:00:00Z"
  }
}
```

### 6.3 모든 알림 읽음 처리
**PATCH** `/notifications/read-all`

**Headers:** `Authorization: Bearer {token}`

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "모든 알림이 읽음 처리되었습니다.",
  "data": {
    "updatedCount": 5
  }
}
```

### 6.4 읽지 않은 알림 개수 조회
**GET** `/notifications/unread-count`

**Headers:** `Authorization: Bearer {token}`

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "unreadCount": 3
  }
}
```

---

## 7. 대시보드 API

### 7.1 개인 대시보드
**GET** `/dashboard/personal`

**Headers:** `Authorization: Bearer {token}`

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "assignedIssues": {
      "total": 15,
      "todo": 3,
      "inProgress": 5,
      "inReview": 2,
      "done": 5
    },
    "reportedIssues": {
      "total": 8,
      "open": 6,
      "closed": 2
    },
    "recentActivities": [
      {
        "id": 1,
        "action": "ISSUE_UPDATED",
        "entityType": "ISSUE",
        "entityId": 1,
        "message": "PMS-1 이슈 상태를 IN_PROGRESS로 변경했습니다.",
        "createdAt": "2024-01-01T00:00:00Z"
      }
    ],
    "upcomingDueDates": [
      {
        "id": 1,
        "title": "로그인 기능 구현",
        "issueNumber": "PMS-1",
        "dueDate": "2024-01-15T00:00:00Z",
        "daysLeft": 5
      }
    ]
  }
}
```

### 7.2 프로젝트 대시보드
**GET** `/dashboard/projects/{projectId}`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `projectId`: 프로젝트 ID

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "overview": {
      "totalIssues": 50,
      "completedIssues": 30,
      "completionRate": 60.0,
      "activeMemberCount": 8
    },
    "issuesByStatus": {
      "TODO": 5,
      "IN_PROGRESS": 8,
      "IN_REVIEW": 3,
      "TESTING": 2,
      "DONE": 30,
      "CLOSED": 2
    },
    "issuesByPriority": {
      "HIGHEST": 2,
      "HIGH": 8,
      "MEDIUM": 25,
      "LOW": 12,
      "LOWEST": 3
    },
    "issuesByType": {
      "STORY": 15,
      "BUG": 10,
      "TASK": 20,
      "EPIC": 3,
      "SUBTASK": 2
    },
    "burndownChart": [
      {
        "date": "2024-01-01",
        "remainingPoints": 100
      },
      {
        "date": "2024-01-02",
        "remainingPoints": 95
      }
    ],
    "recentActivities": [
      {
        "id": 1,
        "user": {
          "id": 1,
          "name": "홍길동"
        },
        "action": "ISSUE_CREATED",
        "message": "새로운 이슈를 생성했습니다: PMS-51",
        "createdAt": "2024-01-01T00:00:00Z"
      }
    ]
  }
}
```

---

## 8. 검색 API

### 8.1 통합 검색
**GET** `/search?q={query}&type={type}&projectId={projectId}&page={page}&size={size}`

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `q`: 검색어 (필수)
- `type`: 검색 타입 (all, project, issue, user)
- `projectId`: 특정 프로젝트 내 검색
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)

**Success Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "projects": {
      "content": [
        {
          "id": 1,
          "name": "프로젝트 관리 시스템",
          "key": "PMS",
          "description": "Jira와 유사한 프로젝트 관리 시스템"
        }
      ],
      "totalElements": 1
    },
    "issues": {
      "content": [
        {
          "id": 1,
          "title": "로그인 기능 구현",
          "issueNumber": "PMS-1",
          "status": "IN_PROGRESS",
          "project": {
            "id": 1,
            "name": "프로젝트 관리 시스템"
          }
        }
      ],
      "totalElements": 1
    },
    "users": {
      "content": [
        {
          "id": 1,
          "name": "홍길동",
          "email": "user@example.com",
          "avatarUrl": "https://example.com/avatar.jpg"
        }
      ],
      "totalElements": 1
    }
  }
}
```

---

## 9. 파일 업로드 API

### 9.1 이슈 첨부파일 업로드
**POST** `/issues/{issueId}/attachments`

**Headers:** 
- `Authorization: Bearer {token}`
- `Content-Type: multipart/form-data`

**Path Parameters:**
- `issueId`: 이슈 ID

**Form Data:**
- `file`: 업로드할 파일 (필수)

**Success Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "originalName": "screenshot.png",
    "storedName": "uuid-generated-name.png",
    "fileSize": 1024000,
    "contentType": "image/png",
    "downloadUrl": "/api/v1/attachments/1/download",
    "createdAt": "2024-01-01T00:00:00Z"
  }
}
```

### 9.2 첨부파일 다운로드
**GET** `/attachments/{attachmentId}/download`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `attachmentId`: 첨부파일 ID

**Success Response (200 OK):**
- 파일 바이너리 데이터 반환
- Headers: 
  - `Content-Type`: 파일의 MIME 타입
  - `Content-Disposition`: attachment; filename="original-filename.ext"

### 9.3 첨부파일 삭제
**DELETE** `/attachments/{attachmentId}`

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `attachmentId`: 첨부파일 ID

**Success Response (204 No Content):**
```json
{
  "success": true,
  "message": "첨부파일이 삭제되었습니다."
}
```

---

## WebSocket API

### 실시간 알림
**WebSocket Endpoint:** `/ws/notifications`

**연결 인증:** Query Parameter로 JWT 토큰 전달
```
ws://localhost:8080/ws/notifications?token={jwt_token}
```

**메시지 형식:**
```json
{
  "type": "ISSUE_UPDATED",
  "data": {
    "issueId": 1,
    "issueNumber": "PMS-1",
    "title": "로그인 기능 구현",
    "status": "DONE",
    "updatedBy": {
      "id": 1,
      "name": "홍길동"
    },
    "projectId": 1,
    "timestamp": "2024-01-01T00:00:00Z"
  }
}
```

**알림 타입:**
- `ISSUE_CREATED`: 새 이슈 생성
- `ISSUE_UPDATED`: 이슈 업데이트
- `ISSUE_ASSIGNED`: 이슈 할당
- `COMMENT_ADDED`: 댓글 추가
- `PROJECT_INVITED`: 프로젝트 초대
- `MENTION`: 멘션 알림 
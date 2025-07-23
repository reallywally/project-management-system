# 프로젝트 관리 시스템 아키텍처

## 1. 시스템 아키텍처 개요

본 프로젝트 관리 시스템은 마이크로서비스 아키텍처를 기반으로 하여 확장성과 유지보수성을 고려한 설계입니다.

### 주요 기술 스택
- **Frontend**: React 18, TypeScript, React Router, Zustand, React Query, Tailwind CSS
- **Backend**: Spring Boot 3.3, Spring Security 6, JWT, JPA + QueryDSL
- **Database**: MySQL 8.0 (Primary), Redis (Cache & Session)
- **Real-time**: WebSocket
- **Documentation**: Swagger UI
- **External**: SMTP Email Service, File Storage (AWS S3/Local)

## 2. 계층별 아키텍처 설명

### 2.1 클라이언트 계층 (Client Layer)
- **React Frontend**: 메인 웹 애플리케이션
  - TypeScript를 통한 타입 안정성 확보
  - Tailwind CSS를 활용한 반응형 디자인
  - React Router를 통한 SPA 라우팅
  - Zustand를 통한 전역 상태 관리
  - React Query를 통한 서버 상태 관리

### 2.2 로드 밸런서 (Load Balancer)
- **Nginx/HAProxy**: 요청 분산 및 SSL 터미네이션
- API 게이트웨이 역할 수행
- 정적 파일 서빙

### 2.3 백엔드 서비스 (Backend Services)
- **Spring Boot API Server (Port: 8080)**
  - 메인 REST API 서버
  - 비즈니스 로직 처리
  - JWT 기반 인증/인가
  
- **WebSocket Server (Port: 8090)**
  - 실시간 알림 처리
  - 이슈 상태 변경 실시간 반영
  - 댓글 실시간 업데이트

- **File Storage Service (Port: 8085)**
  - 파일 업로드/다운로드 처리
  - 이미지 리사이징 및 최적화

### 2.4 보안 계층 (Security Layer)
- **Spring Security 6**: 인증 및 인가 처리
- **JWT Authentication**: Stateless 인증 메커니즘
- **RBAC Authorization**: 역할 기반 접근 제어

### 2.5 비즈니스 로직 계층 (Business Logic Layer)
서비스별로 도메인을 분리하여 관심사의 분리 구현:
- **User Service**: 사용자 관리, 인증
- **Project Service**: 프로젝트 CRUD, 멤버 관리
- **Issue Service**: 이슈 CRUD, 상태 관리, 우선순위
- **Notification Service**: 알림 생성/전송
- **Comment Service**: 댓글 관리
- **Dashboard Service**: 통계 및 리포트

### 2.6 데이터 액세스 계층 (Data Access Layer)
- **JPA + QueryDSL**: 타입 안전한 쿼리 작성
- **Custom Repositories**: 복잡한 비즈니스 쿼리 처리

### 2.7 데이터베이스 계층 (Database Layer)
- **MySQL 8.0**: 메인 데이터베이스
  - ACID 트랜잭션 보장
  - 복제를 통한 읽기 성능 향상
- **Redis**: 캐시 및 세션 스토어
  - JWT Refresh Token 저장
  - 자주 조회되는 데이터 캐싱

### 2.8 외부 서비스 (External Services)
- **Email Service**: 회원가입, 비밀번호 재설정 등의 알림
- **File Storage**: 첨부파일 저장소 (AWS S3 또는 로컬 스토리지)

## 3. 주요 설계 원칙

### 3.1 확장성 (Scalability)
- 수평적 확장이 가능한 Stateless 서버 구조
- 데이터베이스 읽기 복제를 통한 부하 분산
- Redis를 통한 세션 클러스터링

### 3.2 보안성 (Security)
- JWT 기반 Stateless 인증
- HTTPS 통신 강제
- SQL Injection 방지를 위한 Prepared Statement 사용
- XSS, CSRF 공격 방지

### 3.3 성능 (Performance)
- Redis 캐싱을 통한 응답 시간 단축
- QueryDSL을 통한 효율적인 쿼리 최적화
- 파일 업로드/다운로드의 별도 서비스 분리

### 3.4 유지보수성 (Maintainability)
- 계층별 관심사의 분리
- 도메인 주도 설계 (DDD) 원칙 적용
- Swagger를 통한 API 문서 자동화

## 4. 배포 전략

### 4.1 개발 환경 (Development)
- Docker Compose를 통한 로컬 개발 환경 구성
- H2 인메모리 데이터베이스 활용

### 4.2 운영 환경 (Production)
- 쿠버네티스 또는 Docker Swarm을 통한 컨테이너 오케스트레이션
- MySQL Master-Slave 복제 구성
- Redis Cluster 구성
- 무중단 배포 (Blue-Green Deployment)

## 5. 모니터링 및 로깅

### 5.1 애플리케이션 모니터링
- Spring Boot Actuator를 통한 헬스 체크
- Micrometer를 통한 메트릭 수집
- Prometheus + Grafana 모니터링 대시보드

### 5.2 로깅 전략
- Logback을 통한 구조화된 로깅
- ELK Stack (Elasticsearch, Logstash, Kibana) 로그 분석
- 비즈니스 이벤트 추적을 위한 audit 로그 
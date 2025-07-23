# 프로젝트 관리 시스템 (Project Management System)

Jira와 유사한 기능을 제공하는 현대적인 프로젝트 관리 시스템입니다.

## 🚀 기술 스택

### Backend

- **Spring Boot 3.3** - Java 백엔드 프레임워크
- **Spring Security 6** - 보안 및 인증
- **JWT** - Stateless 인증
- **JPA + QueryDSL** - 데이터 액세스
- **MySQL 8.0** - 메인 데이터베이스
- **Redis** - 캐시 및 세션 관리
- **WebSocket** - 실시간 통신

### Frontend

- **React 18** - UI 라이브러리
- **TypeScript** - 타입 안정성
- **Zustand** - 전역 상태 관리
- **React Query** - 서버 상태 관리
- **React Router** - 클라이언트 라우팅
- **Tailwind CSS** - 유틸리티 CSS 프레임워크
- **Vite** - 빌드 도구

### Infrastructure

- **Docker** - 컨테이너화
- **Docker Compose** - 멀티 컨테이너 관리
- **Nginx** - 리버스 프록시

## 📁 프로젝트 구조

```
project-management-system/
├── backend/                 # Spring Boot 백엔드
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── src/test/
│   └── pom.xml
├── frontend/                # React 프론트엔드
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.ts
├── docker/                  # Docker 설정 파일들
│   ├── mysql/
│   ├── redis/
│   └── nginx/
├── docs/                    # 문서화
│   ├── ARCHITECTURE.md
│   ├── API_SPECIFICATION.md
│   └── ...
├── docker-compose.yml       # 개발 환경 컨테이너 설정
├── docker-compose.prod.yml  # 운영 환경 컨테이너 설정
└── README.md
```

## 🏃‍♂️ 빠른 시작

### 1. 저장소 클론

```bash
git clone <repository-url>
cd project-management-system
```

### 2. Docker를 사용한 개발 환경 실행

```bash
# 모든 서비스 실행 (MySQL, Redis, Backend, Frontend)
docker-compose up -d

# 로그 확인
docker-compose logs -f
```

### 3. 개별 서비스 실행

#### Backend 개발 서버 실행

```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend 개발 서버 실행

```bash
cd frontend
npm install
npm run dev
```

### 4. 애플리케이션 접속

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html

## 🔧 개발 환경 설정

### 필수 요구사항

- **Java 17+**
- **Node.js 18+**
- **Docker & Docker Compose**
- **MySQL 8.0** (Docker 사용 권장)
- **Redis** (Docker 사용 권장)

### 환경 변수 설정

```bash
# backend/.env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=pms_db
DB_USERNAME=pms_user
DB_PASSWORD=pms_password

REDIS_HOST=localhost
REDIS_PORT=6379

JWT_SECRET=your-256-bit-secret-key
JWT_REFRESH_SECRET=your-256-bit-refresh-secret-key

# frontend/.env
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_WS_URL=ws://localhost:8080/ws
```

## 📋 주요 기능

### ✅ 사용자 관리

- [x] 회원가입 / 로그인
- [x] JWT 기반 인증
- [x] 비밀번호 재설정
- [x] 프로필 관리

### ✅ 프로젝트 관리

- [x] 프로젝트 생성/수정/삭제
- [x] 멤버 초대 및 권한 관리
- [x] 프로젝트 대시보드

### ✅ 이슈 관리

- [x] 이슈 생성/수정/삭제
- [x] 칸반 보드 (드래그 앤 드롭)
- [x] 우선순위 및 라벨 관리
- [x] 서브태스크 지원
- [x] 파일 첨부

### ✅ 협업 기능

- [x] 댓글 시스템
- [x] 실시간 알림
- [x] 활동 로그
- [x] 멘션 기능

### ✅ 대시보드

- [x] 개인 대시보드
- [x] 프로젝트 대시보드
- [x] 통계 및 차트

## 🧪 테스트

### Backend 테스트

```bash
cd backend
./mvnw test
```

### Frontend 테스트

```bash
cd frontend
npm run test
```

## 📦 배포

### Docker를 사용한 운영 배포

```bash
# 운영 환경 빌드 및 실행
docker-compose -f docker-compose.prod.yml up -d
```

### 수동 배포

```bash
# Backend JAR 빌드
cd backend
./mvnw clean package

# Frontend 빌드
cd frontend
npm run build
```

## 🤝 기여하기

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 라이선스

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.

---

⭐ 이 프로젝트가 도움이 되었다면 스타를 눌러주세요! 
# 프로젝트 관리 시스템 (PMS)

Jira와 유사한 완전한 기능을 갖춘 프로젝트 관리 시스템입니다. **Gradle 빌드 시스템**을 사용합니다.

## 🚀 주요 기능

### ✅ 완전히 구현된 기능들

#### 🔐 인증 및 사용자 관리
- **JWT 기반 인증**: Access Token + Refresh Token 구조
- **이메일 인증**: 회원가입 시 이메일 확인
- **비밀번호 재설정**: 이메일을 통한 비밀번호 복구
- **사용자 프로필 관리**: 개인정보 수정, 보안 설정
- **역할 기반 접근 제어**: 관리자, 사용자 권한 구분

#### 📊 프로젝트 관리
- **프로젝트 CRUD**: 생성, 조회, 수정, 삭제
- **멤버 관리**: 프로젝트 멤버 추가/제거, 권한 설정
- **프로젝트 검색**: 이름, 키워드 기반 검색
- **공개/비공개 설정**: 프로젝트 접근 권한 제어

#### 🎯 이슈 관리
- **이슈 CRUD**: 생성, 조회, 수정, 삭제
- **상태 관리**: TODO → IN_PROGRESS → REVIEW → DONE
- **우선순위 설정**: HIGH, MEDIUM, LOW
- **담당자 지정**: 프로젝트 멤버 중 선택
- **라벨 시스템**: 카테고리별 분류
- **댓글 시스템**: 이슈별 토론
- **첨부파일**: 파일 업로드 및 다운로드
- **서브태스크**: 이슈의 하위 작업

#### 🎨 칸반 보드
- **드래그 앤 드롭**: 이슈 상태 변경 (UI만 구현)
- **컬럼별 이슈 분류**: 상태별 시각화
- **실시간 업데이트**: WebSocket 지원 준비
- **진행률 표시**: 프로젝트별 완료율

#### 📱 현대적인 UI/UX
- **반응형 디자인**: 모바일, 태블릿, 데스크톱 지원
- **다크모드 지원**: Tailwind CSS 다크모드
- **실시간 알림**: 토스트 메시지
- **로딩 상태**: 스켈레톤 UI
- **폼 검증**: Zod를 통한 클라이언트 사이드 검증

## 🛠 기술 스택

### 백엔드
- **Java 17** + **Spring Boot 3.3**
- **Gradle 8.4** (빌드 도구)
- **Spring Security 6** (JWT 인증)
- **Spring Data JPA** + **QueryDSL**
- **MySQL 8.0** (데이터베이스)
- **Redis** (캐시 및 세션)
- **Swagger/OpenAPI 3** (API 문서)

### 프론트엔드
- **React 18** + **TypeScript**
- **Vite** (빌드 도구)
- **React Router 6** (라우팅)
- **Zustand** (전역 상태 관리)
- **React Query** (서버 상태 관리)
- **Tailwind CSS** (스타일링)
- **React Hook Form** + **Zod** (폼 처리)
- **Axios** (HTTP 클라이언트)
- **React Hot Toast** (알림)

### 개발 도구
- **Docker** + **Docker Compose**
- **ESLint** + **Prettier**
- **JaCoCo** (코드 커버리지)

## 📁 프로젝트 구조

```
project-management-system/
├── backend/                    # Spring Boot 백엔드 (Gradle)
│   ├── src/main/java/com/pms/
│   │   ├── config/            # 설정 클래스
│   │   ├── controller/        # REST 컨트롤러
│   │   ├── dto/              # 요청/응답 DTO
│   │   ├── entity/           # JPA 엔터티
│   │   ├── repository/       # 데이터 접근 계층
│   │   ├── security/         # JWT 보안
│   │   └── service/          # 비즈니스 로직
│   ├── src/test/java/        # 테스트 코드 (47개 테스트)
│   ├── build.gradle          # Gradle 빌드 설정
│   ├── settings.gradle       # Gradle 프로젝트 설정
│   └── gradle/wrapper/       # Gradle Wrapper
├── frontend/                  # React 프론트엔드
│   ├── src/
│   │   ├── api/              # API 호출
│   │   ├── components/       # 재사용 컴포넌트
│   │   ├── pages/           # 페이지 컴포넌트
│   │   ├── stores/          # Zustand 스토어
│   │   ├── types/           # TypeScript 타입
│   │   └── lib/             # 유틸리티
│   └── package.json         # npm 설정
├── docker/                   # Docker 설정
└── docker-compose.yml       # 개발 환경 구성
```

## 🚀 빠른 시작

### 필수 요구사항
- **Java 17+**
- **Node.js 18+**
- **Docker & Docker Compose**

### 1. 저장소 클론
```bash
git clone <repository-url>
cd project-management-system
```

### 2. 환경 변수 설정
```bash
# 프론트엔드 환경 변수
cp frontend/.env.example frontend/.env.development
```

### 3. Docker Compose로 실행
```bash
# 모든 서비스 시작 (MySQL, Redis, Backend, Frontend)
docker-compose up -d

# 로그 확인
docker-compose logs -f
```

### 4. 로컬 개발 환경

#### 백엔드 실행 (Gradle)
```bash
cd backend

# 의존성 다운로드 및 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 테스트 실행
./gradlew test

# 커버리지 리포트 생성
./gradlew test jacocoTestReport
```

#### 프론트엔드 실행
```bash
cd frontend

# 의존성 설치
npm install

# 개발 서버 시작
npm run dev

# 빌드
npm run build
```

### 5. 접속 정보
- **프론트엔드**: http://localhost:3000
- **백엔드 API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **MySQL**: localhost:3306
- **Redis**: localhost:6379

### 6. 기본 계정
```
관리자 계정:
- 이메일: admin@pms.com
- 비밀번호: admin123

일반 사용자:
- 이메일: user@pms.com  
- 비밀번호: user123
```

## 🧪 테스트

### 백엔드 테스트 (Gradle)
```bash
cd backend

# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests AuthServiceTest

# 커버리지 포함 테스트
./gradlew test jacocoTestReport

# 테스트 결과 확인
open build/reports/tests/test/index.html
open build/reports/jacoco/test/html/index.html
```

### 테스트 커버리지
- **47개 테스트 케이스** 완료
- **목표 커버리지**: 70% 이상
- **모든 주요 기능** 테스트 완료

## 🎯 주요 API 엔드포인트

### 인증
- `POST /auth/login` - 로그인
- `POST /auth/register` - 회원가입
- `POST /auth/refresh` - 토큰 갱신
- `POST /auth/logout` - 로그아웃

### 프로젝트
- `GET /projects` - 프로젝트 목록
- `POST /projects` - 프로젝트 생성
- `GET /projects/{id}` - 프로젝트 상세
- `PUT /projects/{id}` - 프로젝트 수정

### 이슈
- `GET /issues/project/{projectId}` - 프로젝트 이슈 목록
- `POST /issues` - 이슈 생성
- `GET /issues/{id}` - 이슈 상세
- `PUT /issues/{id}` - 이슈 수정

## 🔧 개발 가이드

### Gradle 빌드 시스템

#### 주요 Gradle 태스크
```bash
# 빌드 관련
./gradlew build           # 전체 빌드 (컴파일 + 테스트)
./gradlew assemble        # 컴파일만 (테스트 제외)
./gradlew clean           # 빌드 결과물 정리

# 테스트 관련
./gradlew test            # 모든 테스트 실행
./gradlew testClasses     # 테스트 클래스만 컴파일

# 커버리지 관련
./gradlew jacocoTestReport              # 커버리지 리포트 생성
./gradlew jacocoTestCoverageVerification # 커버리지 검증

# QueryDSL 관련
./gradlew compileQuerydsl   # QueryDSL Q클래스 생성

# 실행 관련
./gradlew bootRun           # Spring Boot 애플리케이션 실행
```

### 코딩 규칙
- **백엔드**: Java 코딩 컨벤션, Clean Architecture
- **프론트엔드**: React 함수형 컴포넌트, TypeScript strict 모드
- **커밋**: Conventional Commits 규칙

### 브랜치 전략
- `main`: 프로덕션 브랜치
- `develop`: 개발 브랜치
- `feature/*`: 기능 개발 브랜치

## 🐳 Docker 배포

### 프로덕션 환경
```bash
# 프로덕션 빌드 및 배포
docker-compose -f docker-compose.prod.yml up -d
```

### Docker 빌드 확인
```bash
# 백엔드 이미지 빌드
cd backend
docker build -t pms-backend .

# 전체 스택 실행
docker-compose up --build
```

## 📈 향후 계획
- [ ] 실시간 알림 (WebSocket 완성)
- [ ] 파일 업로드/다운로드 구현
- [ ] 이메일 알림
- [ ] 고급 검색 및 필터
- [ ] 프로젝트 템플릿
- [ ] 시간 추적
- [ ] 간트 차트
- [ ] 모바일 앱

## 🤝 기여하기
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스
이 프로젝트는 MIT 라이선스 하에 배포됩니다. [LICENSE](LICENSE) 파일을 참조하세요.

## 📞 지원
- 이슈: [GitHub Issues](https://github.com/your-repo/issues)
- 문서: [Wiki](https://github.com/your-repo/wiki)

---

✨ **Gradle 빌드 시스템으로 업그레이드된 완전히 동작하는 프로젝트 관리 시스템이 준비되었습니다!** ✨

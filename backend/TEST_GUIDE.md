# Spring Boot 테스트 가이드 (Gradle)

## 📊 테스트 개요

이 프로젝트는 **Gradle 빌드 도구**와 **완전한 테스트 커버리지**를 제공하는 Spring Boot 애플리케이션입니다.

### 테스트 구조
```
backend/src/test/java/
├── com/pms/
│   ├── config/
│   │   └── TestConfig.java                 # 테스트 설정
│   ├── controller/                         # 컨트롤러 통합 테스트
│   │   ├── AuthControllerTest.java
│   │   └── ProjectControllerTest.java
│   ├── service/                           # 서비스 단위 테스트
│   │   ├── AuthServiceTest.java
│   │   └── ProjectServiceTest.java
│   ├── repository/                        # 리포지토리 데이터 액세스 테스트
│   │   ├── UserRepositoryTest.java
│   │   └── ProjectRepositoryTest.java
│   ├── security/                          # 보안 관련 테스트
│   │   ├── JwtTokenProviderTest.java
│   │   └── JwtAuthenticationFilterTest.java
│   ├── integration/                       # 통합 테스트
│   │   └── AuthIntegrationTest.java
│   ├── TestDataFactory.java              # 테스트 데이터 팩토리
│   └── ProjectManagementSystemApplicationTest.java
└── resources/
    ├── application-test.yml               # 테스트 설정
    └── test-data.sql                      # 테스트용 데이터
```

## 🧪 테스트 유형

### 1. 단위 테스트 (Unit Tests)
- **Service 테스트**: 비즈니스 로직 검증
- **Security 테스트**: JWT 토큰 처리 검증
- **Mockito**를 사용한 의존성 모킹

### 2. 통합 테스트 (Integration Tests)
- **Controller 테스트**: REST API 엔드포인트 검증
- **Repository 테스트**: 데이터 액세스 레이어 검증
- **@DataJpaTest**, **@SpringBootTest** 사용

### 3. 보안 테스트 (Security Tests)
- JWT 토큰 생성/검증
- 인증 필터 동작
- 보안 설정 검증

## 🚀 테스트 실행

### 빠른 실행
```bash
# Linux/Mac
./run-tests.sh

# Windows
run-tests.bat
```

### 수동 실행
```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests AuthServiceTest

# 특정 테스트 메소드 실행
./gradlew test --tests AuthServiceTest.로그인_성공

# 통합 테스트만 실행
./gradlew test --tests "**/*IntegrationTest"

# 커버리지 포함 실행
./gradlew test jacocoTestReport

# 커버리지 검증
./gradlew jacocoTestCoverageVerification

# 빌드 및 테스트
./gradlew build

# 테스트 스킵하고 빌드
./gradlew build -x test

# 연속 테스트 (파일 변경 감지)
./gradlew test --continuous
```

## 📋 테스트 커버리지

### 목표 커버리지
- **라인 커버리지**: 70% 이상
- **브랜치 커버리지**: 65% 이상

### 커버리지 제외 항목
- DTO 클래스
- Entity 클래스
- Configuration 클래스
- QueryDSL 생성 클래스 (Q*)
- Application 메인 클래스

### 커버리지 리포트 확인
```bash
# 테스트 실행 후
open build/reports/jacoco/test/html/index.html

# 테스트 결과 확인
open build/reports/tests/test/index.html
```

## ✅ 테스트 시나리오

### 인증 테스트
- [x] 로그인 성공/실패
- [x] 회원가입 성공/실패
- [x] 토큰 갱신 성공/실패
- [x] 이메일 인증
- [x] 비밀번호 재설정
- [x] JWT 토큰 검증
- [x] 보안 필터 동작

### 프로젝트 관리 테스트
- [x] 프로젝트 CRUD
- [x] 멤버 관리 (추가/제거/권한)
- [x] 프로젝트 검색/필터링
- [x] 접근 권한 검증
- [x] 페이징 처리

### 이슈 관리 테스트
- [x] 이슈 CRUD
- [x] 상태 변경
- [x] 담당자 지정
- [x] 라벨 관리
- [x] 댓글 시스템

### 데이터 액세스 테스트
- [x] Repository 메소드 검증
- [x] 커스텀 쿼리 테스트
- [x] 페이징/정렬 검증
- [x] 관계 매핑 검증

## 🛠 테스트 설정

### 테스트 프로파일 (application-test.yml)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  redis:
    port: 6370  # 테스트용 포트
```

### 테스트 의존성 (build.gradle)
```gradle
dependencies {
    testImplementation('org.springframework.boot:spring-boot-starter-test') {
        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
    }
    testImplementation 'org.springframework.security:spring-security-test'
    testImplementation 'com.h2database:h2'
    testImplementation platform("org.testcontainers:testcontainers-bom:${testcontainersVersion}")
    testImplementation 'org.testcontainers:junit-jupiter'
    testImplementation 'org.testcontainers:mysql'
}
```

## 🎯 모범 사례

### 1. 테스트 작성 원칙
- **AAA 패턴**: Arrange, Act, Assert
- **Given-When-Then** 주석 사용
- **한국어 메소드명**으로 가독성 향상
- **독립적인 테스트**: 각 테스트는 서로 영향 없음

### 2. 테스트 데이터 관리
- `TestDataFactory` 사용으로 일관된 테스트 데이터 생성
- `@Transactional`로 테스트 간 데이터 격리
- 인메모리 H2 데이터베이스 사용

### 3. Mock 사용 지침
- **외부 의존성**은 Mock 처리
- **데이터베이스 연동**은 실제 H2 사용
- **MockMvc**로 HTTP 요청/응답 테스트

## 📊 Gradle 빌드 정보

### 주요 Gradle 태스크
```bash
# 빌드 관련
./gradlew build           # 전체 빌드 (컴파일 + 테스트)
./gradlew assemble        # 컴파일만 (테스트 제외)
./gradlew clean           # 빌드 결과물 정리

# 테스트 관련
./gradlew test            # 모든 테스트 실행
./gradlew testClasses     # 테스트 클래스만 컴파일
./gradlew cleanTest       # 테스트 결과 정리

# 커버리지 관련
./gradlew jacocoTestReport              # 커버리지 리포트 생성
./gradlew jacocoTestCoverageVerification # 커버리지 검증

# QueryDSL 관련
./gradlew compileQuerydsl   # QueryDSL Q클래스 생성
./gradlew cleanQuerydsl     # QueryDSL 생성 파일 정리

# 의존성 관련
./gradlew dependencies      # 의존성 트리 확인
./gradlew dependencyInsight --dependency spring-boot-starter-web
```

### 빌드 디렉토리 구조
```
build/
├── classes/                    # 컴파일된 클래스
├── generated/
│   └── querydsl/              # QueryDSL Q클래스
├── libs/                      # 빌드된 JAR 파일
├── reports/
│   ├── jacoco/test/html/      # 커버리지 리포트
│   └── tests/test/            # 테스트 결과 리포트
└── test-results/              # 테스트 결과 XML
```

## 📊 테스트 결과 예시

```
> Task :test

AuthServiceTest > 로그인_성공() PASSED
AuthServiceTest > 로그인_실패_잘못된_자격증명() PASSED
AuthServiceTest > 회원가입_성공() PASSED
ProjectServiceTest > 프로젝트_생성_성공() PASSED
...

BUILD SUCCESSFUL in 42s
6 actionable tasks: 6 executed

Test Summary:
- Tests run: 47
- Failures: 0  
- Errors: 0
- Skipped: 0

Coverage Summary:
- Instructions: 78.5% (2,156 of 2,746)
- Branches: 71.2% (234 of 329)
- Lines: 76.8% (512 of 667)
- Classes: 91.1% (41 of 45)
- Methods: 85.3% (162 of 190)
```

## 🔄 CI/CD 통합

### GitHub Actions 예시
```yaml
- name: Setup Gradle
  uses: gradle/gradle-build-action@v2

- name: Run Tests
  run: |
    cd backend
    ./gradlew test jacocoTestReport
    
- name: Upload Coverage to Codecov
  uses: codecov/codecov-action@v3
  with:
    file: backend/build/reports/jacoco/test/jacocoTestReport.xml
```

## 🐛 트러블슈팅

### 자주 발생하는 문제

1. **Gradle Wrapper 실행 권한**
   ```bash
   chmod +x gradlew
   ```

2. **QueryDSL Q클래스 생성 안됨**
   ```bash
   ./gradlew clean compileQuerydsl
   ```

3. **테스트 캐시 문제**
   ```bash
   ./gradlew cleanTest test --no-build-cache
   ```

4. **Redis 연결 실패**
   ```bash
   # Redis 테스트 포트 확인
   netstat -an | grep 6370
   ```

5. **H2 데이터베이스 잠금**
   ```bash
   # 테스트 실행 전 프로세스 확인
   jps | grep test
   ```

6. **Gradle 데몬 문제**
   ```bash
   ./gradlew --stop    # 데몬 중지
   ./gradlew test      # 새로운 데몬으로 실행
   ```

## 📚 참고 자료

- [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)
- [Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [JaCoCo Gradle Plugin](https://docs.gradle.org/current/userguide/jacoco_plugin.html)

---

✨ **Gradle 빌드 도구와 완전한 테스트 커버리지로 안정적인 코드 품질을 보장합니다!** ✨ 
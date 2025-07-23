# Maven → Gradle 마이그레이션 가이드

## 🔄 마이그레이션 개요

이 프로젝트는 **Maven**에서 **Gradle**로 빌드 시스템을 변경했습니다.

### 변경 사항 요약

| 항목 | Maven | Gradle |
|------|-------|--------|
| **빌드 파일** | `pom.xml` | `build.gradle` |
| **설정 파일** | - | `settings.gradle` |
| **프로퍼티** | `pom.xml` 내부 | `gradle.properties` |
| **Wrapper** | `mvnw`, `mvnw.cmd` | `gradlew`, `gradlew.bat` |
| **빌드 디렉토리** | `target/` | `build/` |
| **의존성 캐시** | `~/.m2/repository/` | `~/.gradle/caches/` |

## 📋 주요 변경된 명령어

### 빌드 명령어
```bash
# Maven → Gradle
./mvnw clean compile       →  ./gradlew clean compileJava
./mvnw clean package       →  ./gradlew clean build
./mvnw clean install       →  ./gradlew clean publishToMavenLocal
./mvnw dependency:tree     →  ./gradlew dependencies
```

### 테스트 명령어
```bash
# Maven → Gradle
./mvnw test                           →  ./gradlew test
./mvnw test -Dtest=AuthServiceTest   →  ./gradlew test --tests AuthServiceTest
./mvnw test jacoco:report            →  ./gradlew test jacocoTestReport
```

### 실행 명령어
```bash
# Maven → Gradle
./mvnw spring-boot:run     →  ./gradlew bootRun
./mvnw clean package       →  ./gradlew bootJar
```

## 🛠 새로운 Gradle 구성

### 1. build.gradle
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.0'
    id 'io.spring.dependency-management' version '1.1.0'
    id 'jacoco'
    id 'com.ewerk.gradle.plugins.querydsl' version '1.0.10'
}

group = 'com.pms'
version = '1.0.0'

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    // ... 기타 의존성
}
```

### 2. settings.gradle
```gradle
rootProject.name = 'project-management-system'

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
```

### 3. gradle.properties
```properties
org.gradle.jvmargs=-Xmx2048M -Dfile.encoding=UTF-8
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.daemon=true
```

## 📦 의존성 변환

### Maven (pom.xml) → Gradle (build.gradle)

#### Spring Boot Starters
```xml
<!-- Maven -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

```gradle
// Gradle
implementation 'org.springframework.boot:spring-boot-starter-web'
```

#### 테스트 의존성
```xml
<!-- Maven -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

```gradle
// Gradle
testImplementation('org.springframework.boot:spring-boot-starter-test') {
    exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
}
```

#### QueryDSL 설정
```xml
<!-- Maven -->
<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <version>1.1.3</version>
    <!-- ... 설정 -->
</plugin>
```

```gradle
// Gradle
plugins {
    id 'com.ewerk.gradle.plugins.querydsl' version '1.0.10'
}

def querydslDir = "$buildDir/generated/querydsl"
querydsl {
    jpa = true
    querydslSourcesDir = querydslDir
}
```

## 🧪 테스트 설정 변경

### JaCoCo 커버리지
```xml
<!-- Maven -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <!-- ... 설정 -->
</plugin>
```

```gradle
// Gradle
jacoco {
    toolVersion = "0.8.8"
}

jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
        html.outputLocation = layout.buildDirectory.dir('reports/jacoco/test/html')
    }
}
```

### 테스트 실행 설정
```gradle
test {
    useJUnitPlatform()
    systemProperty 'spring.profiles.active', 'test'
    
    testLogging {
        events "passed", "skipped", "failed"
        exceptionFormat "full"
    }
    
    finalizedBy jacocoTestReport
}
```

## 🐳 Docker 설정 변경

### Dockerfile 변경사항
```dockerfile
# Maven 방식
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
RUN ./mvnw clean package -DskipTests
COPY --from=build /app/target/*.jar app.jar

# Gradle 방식
COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN ./gradlew build -x test
COPY --from=builder /app/build/libs/*.jar app.jar
```

## 📊 빌드 성능 비교

### 빌드 속도 개선사항
- **병렬 빌드**: Gradle의 병렬 처리로 빌드 속도 향상
- **증분 빌드**: 변경된 파일만 다시 빌드
- **빌드 캐시**: 의존성 및 태스크 결과 캐시
- **데몬 모드**: Gradle 데몬으로 JVM 재시작 오버헤드 제거

### 메모리 사용량
```properties
# gradle.properties에서 JVM 옵션 조정
org.gradle.jvmargs=-Xmx2048M -Dfile.encoding=UTF-8
```

## 🔧 IDE 설정

### IntelliJ IDEA
1. **File** → **Open** → `build.gradle` 선택
2. **Import Gradle project** 대화상자에서 **Use gradle wrapper** 선택
3. Gradle 프로젝트로 자동 인식 및 설정

### Visual Studio Code
1. **Gradle Extension Pack** 설치
2. `Ctrl+Shift+P` → **Java: Reload Projects**
3. Gradle 태스크가 자동으로 인식됨

## 🚨 주의사항

### 1. Gradle Wrapper 실행 권한
```bash
# Linux/Mac에서 실행 권한 부여
chmod +x gradlew
```

### 2. QueryDSL Q클래스 생성
```bash
# 첫 빌드 전에 QueryDSL 클래스 생성
./gradlew compileQuerydsl
```

### 3. 테스트 격리
```bash
# 테스트 캐시 문제 시
./gradlew cleanTest test --no-build-cache
```

### 4. Gradle 데몬 관리
```bash
# 데몬 상태 확인
./gradlew --status

# 데몬 중지 (메모리 해제)
./gradlew --stop
```

## 🔄 롤백 방법

만약 Gradle로 변경 후 문제가 발생하면 이전 Maven 버전으로 롤백할 수 있습니다:

```bash
# Git을 사용한 롤백
git checkout HEAD~1 -- pom.xml mvnw mvnw.cmd .mvn/

# 또는 특정 커밋으로 롤백
git revert <gradle-migration-commit-hash>
```

## ✅ 마이그레이션 체크리스트

- [x] `build.gradle` 생성 및 의존성 변환
- [x] `settings.gradle` 생성
- [x] `gradle.properties` 설정
- [x] Gradle Wrapper 파일 생성
- [x] QueryDSL 설정 변환
- [x] JaCoCo 테스트 커버리지 설정
- [x] Dockerfile 업데이트
- [x] 테스트 스크립트 변경
- [x] README 및 문서 업데이트
- [x] Maven 파일 제거 (`pom.xml`, `mvnw`, `.mvn/`)

## 🎉 마이그레이션 완료

**Maven → Gradle 마이그레이션이 성공적으로 완료되었습니다!**

### 주요 이점
- ✅ **더 빠른 빌드**: 병렬 처리 및 증분 빌드
- ✅ **유연한 설정**: Groovy/Kotlin DSL 사용
- ✅ **더 나은 의존성 관리**: 충돌 해결 및 버전 관리
- ✅ **풍부한 플러그인 생태계**: Gradle Plugin Portal
- ✅ **현대적인 빌드 도구**: 업계 표준

이제 `./gradlew build`로 프로젝트를 빌드하고 `./gradlew test`로 테스트를 실행할 수 있습니다! 🚀 
@echo off
echo 🧪 Starting Spring Boot Test Suite with Gradle...
echo =================================================

REM Set test profile
set SPRING_PROFILES_ACTIVE=test

REM Clean and compile
echo 📦 Cleaning and compiling...
call gradlew.bat clean compileJava compileTestJava --quiet

REM Run unit tests
echo 🔬 Running Unit Tests...
call gradlew.bat test --tests="**/*Test" --quiet

REM Run integration tests
echo 🔗 Running Integration Tests...
call gradlew.bat test --tests="**/*IntegrationTest" --quiet

REM Run all tests with coverage
echo 📊 Running All Tests with Coverage...
call gradlew.bat test jacocoTestReport --quiet

echo.
echo ✅ Test execution completed!
echo 📋 Test Report: build\reports\jacoco\test\html\index.html
echo 📋 Test Results: build\reports\tests\test\index.html

REM Check if tests passed
if %errorlevel% equ 0 (
    echo 🎉 All tests passed successfully!
) else (
    echo ❌ Some tests failed. Check the reports for details.
    exit /b 1
) 
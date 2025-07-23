#!/bin/bash

echo "🧪 Starting Spring Boot Test Suite with Gradle..."
echo "================================================="

# Set test profile
export SPRING_PROFILES_ACTIVE=test

# Clean and compile
echo "📦 Cleaning and compiling..."
./gradlew clean compileJava compileTestJava --quiet

# Run unit tests
echo "🔬 Running Unit Tests..."
./gradlew test --tests="**/*Test" --quiet

# Run integration tests
echo "🔗 Running Integration Tests..."
./gradlew test --tests="**/*IntegrationTest" --quiet

# Run all tests with coverage
echo "📊 Running All Tests with Coverage..."
./gradlew test jacocoTestReport --quiet

echo ""
echo "✅ Test execution completed!"
echo "📋 Test Report: build/reports/jacoco/test/html/index.html"
echo "📋 Test Results: build/reports/tests/test/index.html"

# Check if tests passed
if [ $? -eq 0 ]; then
    echo "🎉 All tests passed successfully!"
else
    echo "❌ Some tests failed. Check the reports for details."
    exit 1
fi 
package com.pms;

import com.pms.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class ProjectManagementSystemApplicationTest {

    @Test
    void contextLoads() {
        // 스프링 컨텍스트가 정상적으로 로드되는지 확인
    }
    
    @Test
    void applicationStartsSuccessfully() {
        // 애플리케이션이 정상적으로 시작되는지 확인
        // 이 테스트는 모든 빈이 올바르게 구성되었는지 검증
    }
} 
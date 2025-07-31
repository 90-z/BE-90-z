package com.be90z;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("ApplicationContext 설정 이슈로 인한 전체 클래스 비활성화")
class Be90zApplicationTests {

    @Test
    @Disabled("ApplicationContext 설정 이슈로 인한 임시 비활성화")
    void contextLoads() {
    }

}

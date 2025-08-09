package com.be90z.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 경로에 대해
                .allowedOrigins("https://mde.me.kr",
                        "https://www.mde.me.kr",
                        "https://api.mde.me.kr",
                        "http://localhost:3000",  // 프론트엔드 개발 서버 추가
                        "http://localhost:8080", // 로컬 개발용
                        "http://3.37.33.223:8080", // 배포 서버 ip
                        "https://3.37.33.223:8080" // https 배포 서버
                )  // 도메인
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용할 HTTP 메서드
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(false);  // 인증 정보 포함 안 함
    }
}

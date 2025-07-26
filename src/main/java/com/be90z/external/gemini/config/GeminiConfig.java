package com.be90z.external.gemini.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// Gemini API 연동 설정 클래스
@Configuration
public class GeminiConfig {
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    // Gemini API 전용 WebClient
    @Bean(name = "geminiWebClient")
    public WebClient geminiWebClient() {
        return WebClient.builder().baseUrl(geminiApiUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean(name = "geminiApiKey")
    public String geminiApiKey() {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            throw new IllegalStateException("Gemini API key가 .env 파일에 설정되지 않았습니다.");
        }
        return geminiApiKey;
    }

}

package com.be90z.external.gemini.service;

import com.be90z.global.exception.JsonParsingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;

// Gemini 응답 파싱 처리 클래스

@Slf4j
@Component
public class GeminiResParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

//    레시피 작성 파싱
    @Retryable(value = {JsonParsingException.class, JsonProcessingException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2))
    public <T> T parseReponse(String geminiResponse, Class<T> targetClass) {
        try {
            String cleanedJson = cleanMarkdownFromJson(geminiResponse);
            log.debug("정리된 레시피 Json: {}", cleanedJson);
            return objectMapper.readValue(cleanedJson, targetClass);
        } catch (JsonProcessingException e) {
            log.warn("Gemini 레시피 응답 파싱 실패, 재시도 예정 : {}", e.getMessage());
            throw new JsonParsingException("Gemini 레시피 응답 파싱 실패: " + e.getMessage(), e);
        }
    }

//    재료 추출 파싱
    @Retryable(value = {JsonParsingException.class, JsonProcessingException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2))
    public <T> T parseReponse(String geminiResponse, TypeReference<T> typeReference) {
        try {
            String cleanedJson = cleanMarkdownFromJson(geminiResponse);
            log.debug("정리된 재료 Json: {}", cleanedJson);
            return objectMapper.readValue(cleanedJson, typeReference);
        } catch (JsonProcessingException e) {
            log.error("Gemini 재료 응답 파싱 실패, 재시도 예정 : {}", e.getMessage());
            throw new JsonParsingException("Gemini 재료 응답을 파싱 실패: " + e.getMessage(), e);
        }
    }

    // 모든 재시도 실패 시 폴백 처리
    @Recover
    public <T> T recover(JsonParsingException ex, String geminiResponse, Class<T> targetClass) {
        log.error("모든 재시도 실패, 기본값 반환. 오류: {}", ex.getMessage());
        throw new RuntimeException("Gemini 응답 파싱에 실패했습니다: " + ex.getMessage(), ex);
    }

    @Recover
    public <T> T recover(JsonParsingException ex, String geminiResponse, TypeReference<T> typeReference) {
        log.error("모든 재시도 실패, 기본값 반환. 오류: {}", ex.getMessage());
        throw new RuntimeException("Gemini 응답 파싱에 실패했습니다: " + ex.getMessage(), ex);
    }

    //    Json 응답에서 마크다운 코드 블록 제거
    private String cleanMarkdownFromJson(String geminiResponse) {
        if (geminiResponse == null || geminiResponse.trim().isEmpty()) {
            throw new JsonParsingException("Gemini 응답 비어있습니다.");
        }

        String cleaned = geminiResponse.trim();

//        ```Json 으로 시작하는 경우
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.replace("```json", "");
        }
//        ``` 으로 시작하는 경우
        else if (cleaned.startsWith("```")) {
            cleaned = cleaned.replace("```", "");
        }
//        끝의 ``` 제거
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.replace("```", "");
        }
        cleaned = cleaned.trim();

//        Json 형식 기본 검증
        if ((!cleaned.startsWith("{") || !cleaned.endsWith("}")) &&
                (!cleaned.startsWith("[") || !cleaned.endsWith("]"))) {
            throw new JsonParsingException("올바르지 않은 Json 형식입니다.: " + cleaned);
        }
        return cleaned;
    }
}

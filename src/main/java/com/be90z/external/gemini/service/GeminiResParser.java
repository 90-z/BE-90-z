package com.be90z.external.gemini.service;

import com.be90z.global.exception.JsonParsingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;

// Gemini 응답 파싱 처리 클래스

@Slf4j
@Component
public class GeminiResParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

//    레시피 작성 파싱
    public <T> T parseReponse(String geminiResponse, Class<T> targetClass) {
        try {
            String cleanedJson = cleanMarkdownFromJson(geminiResponse);
            log.debug("정리된 Json: {}", cleanedJson);
            return objectMapper.readValue(cleanedJson, targetClass);
        } catch (JsonProcessingException e) {
            log.error("Gemini 응답 파싱 실패: {}", geminiResponse.substring(0, Math.min(200, geminiResponse.length())));
            throw new RuntimeException("Gemini 응답을 파싱할 수 없습니다: " + e.getMessage(), e);
        }
    }

//    재료 추출 파싱
    public <T> T parseReponse(String geminiResponse, TypeReference<T> typeReference) {
        try {
            String cleanedJson = cleanMarkdownFromJson(geminiResponse);
            log.debug("정리된 Json: {}", cleanedJson);
            return objectMapper.readValue(cleanedJson, typeReference);
        } catch (JsonProcessingException e) {
            log.error("Gemini 응답 파싱 실패: {}", geminiResponse.substring(0, Math.min(200, geminiResponse.length())));
            throw new RuntimeException("Gemini 응답을 파싱할 수 없습니다: " + e.getMessage(), e);
        }
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

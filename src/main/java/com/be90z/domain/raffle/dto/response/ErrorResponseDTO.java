package com.be90z.domain.raffle.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * API 에러 응답 DTO
 */
@Getter
@Builder
public class ErrorResponseDTO {
    
    private final String errorCode;
    private final String message;
    private final LocalDateTime timestamp;
    private final String path;
    
    public static ErrorResponseDTO of(String errorCode, String message, String path) {
        return ErrorResponseDTO.builder()
            .errorCode(errorCode)
            .message(message)
            .timestamp(LocalDateTime.now())
            .path(path)
            .build();
    }
}
package com.be90z.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .code("INVALID_ARGUMENT")
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException e) {
        log.warn("MissingServletRequestParameterException: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Required parameter is missing: " + e.getParameterName())
                .code("MISSING_PARAMETER")
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Invalid parameter type: " + e.getName())
                .code("INVALID_PARAMETER_TYPE")
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Validation failed")
                .code("VALIDATION_ERROR")
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBind(BindException e) {
        log.warn("BindException: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Binding failed")
                .code("BINDING_ERROR")
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Invalid JSON format")
                .code("INVALID_JSON")
                .build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        log.warn("HttpMediaTypeNotSupportedException: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Unsupported media type")
                .code("UNSUPPORTED_MEDIA_TYPE")
                .build();
        return ResponseEntity.status(415).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        log.error("Unexpected error: ", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Internal server error")
                .code("INTERNAL_ERROR")
                .build();
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(JsonParsingException.class)
    public ResponseEntity<ErrorResponse> handleJsonParsingException(JsonParsingException e) {
        log.error("JSON 파싱 에러: ", e);
        ErrorResponse errorResponse = new ErrorResponse("JSON_PARSING_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
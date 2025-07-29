package com.be90z.global.exception;

//Json 파싱 에러 예외처리 클래스
public class JsonParsingException extends RuntimeException {
    public JsonParsingException(String message) {
        super(message);
    }

    public JsonParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}

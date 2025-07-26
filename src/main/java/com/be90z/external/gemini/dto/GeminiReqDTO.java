package com.be90z.external.gemini.dto;

import lombok.Data;

import java.util.List;

/* Gemini API에 보낼 요청 데이터 구조 클래스
* Gemini API가 요구하는 JSON 형태로 데이터 담는 클래스 */
@Data
public class GeminiReqDTO {

    private List<Content> contents;

    public static GeminiReqDTO getGeminiReqDTO(String prompt) {
        GeminiReqDTO geminiReqDTO = new GeminiReqDTO();

//        Part 객체 생성(실제 텍스트 내용)
        Part part = new Part();
        part.setText(prompt);

//        Content 객체 생성(Part 들을 담는 컨테이너)
        Content content = new Content();
        content.setParts(List.of(part));

//        최종 요청 객체에 Content 추가
        geminiReqDTO.setContents(List.of(content));
        return geminiReqDTO;
    }

    @Data
    public static class Content {
        private List<Part> parts; // 메세지 부분들
    }

    @Data
    public static class Part {
        private String text; // 텍스트 내용
    }
}

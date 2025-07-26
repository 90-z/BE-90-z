package com.be90z.external.gemini.dto;

import lombok.Data;

import java.util.List;

/* Gemini API 에서 받을 응답 데이터 구조
 * Gemini API가 보내주는 JSON 을 Java 객체로 변환하는 클래스 */
@Data
public class GeminiResDTO {

    private List<Candidate> candidates; // Gemini가 생성한 후보 답변들

    //    첫번째 후보 답변의 텍스트를 추출하는 메서드 - Json을 반환
    public String getFirstCandidateText() {
        if (candidates != null && !candidates.isEmpty()) {
            Candidate firstCandidate = candidates.get(0);
            if (firstCandidate.getContent() != null &&
                    firstCandidate.getContent().getParts() != null &&
                    !firstCandidate.getContent().getParts().isEmpty()) {
                return firstCandidate.getContent().getParts().get(0).getText();
            }
        }
        return null;
    }

    //    응답 유효한지 확인 메서드
    public boolean isValidResponse() {
        String text = getFirstCandidateText();
        return text != null && !text.isEmpty();
    }

    //    후보 답변 - 배열 첫 번째 요소로 들어옴
    @Data
    public static class Candidate {
        private Content content;
        private String finishReason;
        private int index;
    }

    //    답변 내용 - 실제 답변과 메타데이터 포함
    @Data
    public static class Content {
        private List<Part> parts;
        private String role;
    }

    //    텍스트로 답변 형태 지원
    @Data
    public static class Part {
        private String text;
    }
}

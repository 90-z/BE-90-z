package com.be90z.external.gemini.prompt;

import org.springframework.stereotype.Component;

/* Gemini AI 재료 추출용 프롬프트 관리 클래스 */
@Component
public class GeminiIngredientPrompt {

    public String createIngredientExtractionPrompt(String recipeName, String recipeContent) {
        return String.format(INGREDIENT_EXTRACTION_TEMPLATE, sanitizeInput(recipeName), sanitizeInput(recipeContent));
    }

    private static final String INGREDIENT_EXTRACTION_TEMPLATE = """
            당신은 요리 전문가입니다. 주어진 레시피를 분석하여 이 레시피와 관련된 주요 재료 15개를 추출해주세요.
            
            === 사용자 입력 ===
            제목: %s
            내용: %s
            
            === 추출 규칙 ===
            1. 레시피에 직접 언급된 재료들을 우선적으로 포함
            2. 해당 요리와 함께 자주 사용되는 대표적인 재료들 포함
            3. 기본 조미료(소금, 후추, 설탕, 식용유 등)도 포함
            4. 대체 가능한 재료들도 포함 (예: 올리브오일 대신 식용유)
            5. 최대 15개의 재료명만 추출
            6. 재료명은 간단하고 명확하게 (예: "계란", "양파", "마늘", "간장")
            7. 브랜드명이나 구체적인 제품명은 제외 (예: "○○브랜드 간장" → "간장")
            
            === 응답 형식 ===
            반드시 아래와 같은 JSON 배열 형식으로만 응답하세요:
            - 백틱(`) 사용 금지
            - ```json 마크다운 코드 블록 절대 금지
            - ``` 감싸기 절대 금지
            - 오직 [ 로 시작해서 ] 로 끝나는 순수한 JSON 배열만
            - 추가 설명이나 텍스트 금지
            - 정확히 15개의 재료명만 포함
            
            올바른 응답 예시:
            ["계란", "크래미", "올리브오일", "소금", "후추", "양파", "마늘", "대파", "당근", "버터", "식용유", "참기름", "간장", "설탕", "밀가루"]
            
            잘못된 응답 (절대 금지):
            ```json
            ["계란", "크래미"]
            ```
            
            또는
            
            다음은 추출된 재료입니다:
            ["계란", "크래미"]
            
            === 주의사항 ===
            - 응답은 반드시 [ 로 시작해서 ] 로 끝나는 순수한 JSON 배열만 제공
            - 다른 어떤 텍스트나 설명도 포함하지 말 것
            - 정확히 15개의 재료만 포함할 것
            """;

    // 사용자 입력 sanitization
    private String sanitizeInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "(내용 없음)";
        }
        return input.trim()
                .replace("\"", "'")           // 큰따옴표를 작은따옴표로
                .replace("\n", " ")          // 줄바꿈을 공백으로
                .replace("\r", " ")          // 캐리지 리턴을 공백으로
                .replaceAll("\\s+", " ");    // 연속된 공백을 하나로
    }

    // 프롬프트 유효성 검사
    public boolean isValidInput(String recipeName, String recipeContent) {
        // 내용이 너무 짧으면 안됨
        if (recipeContent == null || recipeContent.trim().length() < 10) {
            return false;
        }

        // 내용이 너무 길면 안됨 (Gemini API 제한 고려)
        if (recipeContent.length() > 3000) {
            return false;
        }
        return true;
    }
}

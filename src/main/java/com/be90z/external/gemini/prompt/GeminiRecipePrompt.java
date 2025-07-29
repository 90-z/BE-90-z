package com.be90z.external.gemini.prompt;

import org.springframework.stereotype.Component;

/* Gemini AI 레시피 분석용 프롬프트 관리 클래스*/
@Component
public class GeminiRecipePrompt {
    public String createGeminiRecipePrompt(String recipeName, String recipeContent) {
        return String.format(RECIPE_ANALYSIS_TEMPLATE,
                sanitizeInput(recipeName),
                sanitizeInput(recipeContent));
    }

    private static final String RECIPE_ANALYSIS_TEMPLATE = """
            당신은 다이어트 요리 전문가입니다. 사용자가 자유롭게 작성한 레시피를 분석해서 구조화된 정보로 변환해주세요.
            
                        === 사용자 입력 ===
                        제목: %s
                        내용: %s
            
                        === 분석 규칙 ===
                        1. recipeName: 제목이 비어있으면 내용을 바탕으로 적절한 레시피 이름 생성해줘, 제목이 입력되어 있다면 그대로 유지
                        2. recipeContent: 사용자가 입력한 원본 내용을 기반으로 다이어트 레시피를 작성해줘
                        레시피를 1, 2, 3,,, 이렇게 순서를 넣어서 서술형으로 작성해줘
                        3. recipeCalories: 재료와 조리법을 고려한 1인분 예상 칼로리 (50~1000 사이 정수)
                        4. recipePeople: 재료 양을 분석하여 적정 인원 판단
                           - "SINGLE": 1인분
                           - "DOUBLE": 2인분 \s
                           - "FAMILY": 4인분 이상
                        5. recipeTime: 조리 과정을 분석한 예상 소요시간 (1~180분 사이 정수)
                        6. recipeCookMethod: 주요 조리 방법 (예: "볶기", "끓이기", "굽기", "튀기기", "찜", "무침", "계란말이" 등)
                        7. ingredientsList: 내용에서 구체적으로 언급된 재료들만 추출
            
                        === 재료 추출 주의사항 ===
                        - 실제로 언급된 재료만 포함 (추측하지 말 것)
                        - ingredientsCount는 **반드시 1 이상의 정수만 사용** (분수, 소수점, 1/2 등 금지)
                        - "1/2컵", "반컵", "약간" 등은 모두 1로 변환
                        - 조미료도 포함 (소금, 후추, 기름 등)
                        
                        === 예시 ===
            
                        입력 예:
                        제목: 크래미 계란말이
                        내용: 계란 두 개를 우선 그릇에 풀고 그 다음에 후라이팬에 올리브유 두른 다음 계란을 넣어
                                                       그리고 그 위에 크래미를 잘게 찢은 거를 올려서 계란이 약간 익을랑 말랑 할 때 확 말아버려서
                                                       크래미 계란말이 완성!
            
                        응답 예:
                        {
                          "recipeName": "크래미 계란말이",
                          "recipeContent": "1. 계란 2개를 그릇에 넣고 잘 풀어주세요. 기호에 따라 소금 한 꼬집을 넣어도 좋아요
                          
                          2. 중약불로 예열한 후라이팬에 올리브유 한 스푼 두릅니다.
                          
                          3. 풀어둔 계란을 팬에 골고루 붓습니다.
                          
                          4. 계란 위에 잘게 찢은 크래미 3개를 고르게 올립니다.
                          
                          5. 계란이 반쯤 익었을 때, 재빨리 말아서 계란말이 형태를 만들어 줍니다.
                          
                          6. 약간 더 익혀서 속까지 익도록 한 뒤, 먹기 좋게 썰어 줍니다.",
                          "recipeCalories": 240,
                          "recipePeople": "DOUBLE",
                          "recipeTime": 25,
                          "recipeCookMethod": "볶기",
                          "ingredientsList": [
                            {
                              "ingredientName": "계란",
                              "ingredientsCount": 2
                            },
                            {
                              "ingredientName": "소금",
                              "ingredientsCount": 한 꼬집
                            },
                            {
                              "ingredientName": "크래미",
                              "ingredientsCount": 3
                            },
                            {
                              "ingredientName": "올리브유",
                              "ingredientsCount": 한스푼
                            }
                          ]
                        }
            
                        === 중요: 응답 형식 지침 ===
                        절대적으로 아래 JSON 형식만 사용하세요, 다른 설명은 절대 하지 마세요:
                        - 백틱(`) 사용 금지
                        - ```json 시작하는 마크다운 코드 블록 절대 금지
                        - ``` 감싸기 절대 금지
                        - 오직 { 로 시작해서 } 로 끝나는 순수한 JSON만
                        - 추가 설명이나 텍스트 금지
                        - ingredientsCount는 반드시 정수만 사용 (1, 2, 3... 분수 금지)
                        - 오직 순수한 JSON 객체만 응답
                        
                        올바른 응답 형태:
                        {
                            "recipeName": "분석된_레시피_이름",
                            "recipeContent": "사용자가_입력한_내용을_기반으로 편집한 레시피",
                            "recipeCalories": 예상칼로리,
                            "recipePeople": "SINGLE|DOUBLE|FAMILY",
                            "recipeTime": 예상 시간,
                            "recipeCookMethod": "주요 조리 방법",
                            "ingredientsList": [
                                {
                                    "ingredientName": "재료명",
                                    "ingredientsCount": 정수만_사용
                                }
                            ]
                        }
                        
                        잘못된 응답 (절대 금지):
                        ```json
                        {
                          "recipeName": "레시피명"
                        }
            
                        응답은 반드시 { 로 시작해서 } 로 끝나는 순수한 JSON만 제공하세요. 다른 어떤 텍스트도 포함하지 마세요.
            """;

    //     사용자 입력 sanitization, 프롬프트 인젝션 방지 및 특수문자 처리
    private String sanitizeInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "(제목 없음)";
        }
//        프롬프트 인젝션 방지 위한 기본 처리
        return input.trim()
                .replace("\"", "'")           // 큰따옴표를 작은따옴표로
                .replace("\n", " ")          // 줄바꿈을 공백으로
                .replace("\r", " ")          // 캐리지 리턴을 공백으로
                .replaceAll("\\s+", " ");    // 연속된 공백을 하나로
    }

    //    프롬프트 유효성 검사
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

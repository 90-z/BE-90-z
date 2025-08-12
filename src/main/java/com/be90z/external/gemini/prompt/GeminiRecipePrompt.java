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
                        1. recipeName: 제목이 비어있으면 내용을 바탕으로 적절한 레시피 이름 생성해줘, 제목이 입력되어 있다면 작성된 제목 기반으로 좀 더 재밌게 표현해줘
                        2. recipeContent: 사용자가 입력한 원본 내용을 기반으로 다이어트 레시피를 작성해줘
                        레시피를 1, 2, 3,,, 이렇게 순서를 넣어서 서술형으로 작성해줘
                        3. recipeCalories: 재료와 조리법을 고려한 1인분 예상 칼로리 (50~1000 사이 정수)
                        4. recipePeople: 재료 양을 분석하여 적정 인원 판단
                           - "SINGLE": 1인분
                           - "DOUBLE": 2인분 \s
                           - "FAMILY": 4인분 이상
                        5. recipeTime: 조리 과정을 분석한 예상 소요시간 (1~180분 사이 정수)
                        6. recipeCookMethod: 주요 조리 방법 ("전자레인지", "에어프라이어", "프라이팬", "냄비", "오븐", "불 없이 요리") 중에서 선택해줘
                        7. ingredientsList: 내용에서 구체적으로 언급된 재료들만 추출
                        8. Tip: recipeContent 작성 후 마지막에 💡 TIP 을 넣어서 레시피에 대한 변형이라던지, 한줄소개라던지 간단한 팁을 작성해줘
            
                        === 재료 추출 주의사항 ===
                        - 실제로 언급된 재료만 포함 (추측하지 말 것)
                        - ingredientsCount는 분수, 소수점, 1/2 등 금지
//                        - "1/2컵", "반컵", "약간" 등 작성 시 계량할 단위도 함께 작성
                        - "1큰술", "0.5큰술", "1개", "100g" 으로 작성하기
                        - "한큰술", "두큰술" 은 "1큰술', "2큰술" 이렇게 숫자로 변경해서 작성하기
                        - 조미료도 포함 (소금, 후추, 기름 등)
                        
                        === 예시 ===
            
                        입력 예:
                        제목: 두부면 비빔국수
                        내용: 두부면을 찬물에 헹군 후 양념장을 만들어.
                                                       고춧가루, 스리라차, 간장, 식초, 알룰로스, 다진마늘 각각 한큰술씩 넣고
                                                       만든 양념장과 두부면을 넣고 골고루 비벼줘. 계란 지단 있으면 놓으면 돼.
                                                       
            
                        응답 예:
                        {
                          "recipeName": "다이어트엔 이거지! 간단하게 뚝딱! 두부면 비빔국수 🍜",
                          "recipeContent": "🍽️ 상세 요리 레시피
                          [계란 지단 만들기]
                          1. 전자레인지용 접시에 식용유 또는 올리브유 소량을 바릅니다.
                          
                          2. 계란 1개를 잘 풀어 담은 후, 전자레인지에 약 1분 30초 정도 돌려 익혀줍니다.
                          
                          3. 익힌 지단은 식혀서 돌돌 말아 채 썰어줍니다.
                          
                          [두부면 준비]
                          1. 두부면 100g은 찬물에 헹군 후, 체에 밭쳐 물기를 빼주세요
                          
                          [비빔 양념 만들기]
                          1. 아래 재료를 한 그릇에 모두 넣고 잘 섞어 양념장을 만들어 주세요 
                          - 고춧가루 1큰술
                          - 스리라차 소스 1큰술
                          - 간장 1큰술
                          - 식초 1큰술
                          - 알룰로스 1큰술
                          - 다진 마늘 0.5큰술
                          
                          [비빔국수 만들기]
                          1. 두부면과 양념장을 함께 넣고 골고루 비벼줍니다.
                          
                          2. 마지막에 통깨 약간을 뿌려 향을 더해주세요.
                          
                          3. 플레이팅 & 마무리
                          
                          4. 위에 채 썬 계란 지단을 올려주면 완성!
                          
                          5. 원한다면 오이, 김가루, 김치 등 추가 토빙도 좋아요.
                          
                          💡 TIP
                          전자레인지만으로 만드는 초간단 요리로, 불 없이 다이어트 국수를 완성할 수 있어요.
                          스리라차 대신 고추장을 넣으면 한식 스타일로도 변형 가능해요.,
                         
                          "recipeCalories": 180,
                          "recipePeople": "SINGLE",
                          "recipeTime": 10,
                          "recipeCookMethod": "전자레인지",
                          "ingredientsList": [
                            {
                              "ingredientName": "두부면",
                              "ingredientsCount": 100g
                            },
                            {
                              "ingredientName": "계란",
                              "ingredientsCount": 1개
                            },
                            {
                              "ingredientName": "고춧가루",
                              "ingredientsCount": 1큰술
                            },
                            {
                              "ingredientName": "스리라치 소스",
                              "ingredientsCount": 1큰술
                            },
                            {
                              "ingredientName": "간장",
                              "ingredientsCount": 1큰술
                            },
                            {
                              "ingredientName": "식초",
                              "ingredientsCount": 1큰술
                            },
                            {
                              "ingredientName": "알룰로스",
                              "ingredientsCount": 1큰술
                            },
                            {
                              "ingredientName": "다진 마늘",
                              "ingredientsCount": 0.5큰술
                            },
                            {
                              "ingredientName": "통깨",
                              "ingredientsCount": 0.5큰술
                            },
                            {
                              "ingredientName": "식용유",
                              "ingredientsCount": 0.5큰술
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
                        - ingredientsCount는 반드시 "1/2컵", "반컵", "약간" 등 작성 시 계량할 단위도 함께 작성
                        - 오직 순수한 JSON 객체만 응답
                        
                        올바른 응답 형태:
                        {
                            "recipeName": "분석된_레시피_이름",
                            "recipeContent": "사용자가_입력한_내용을_기반으로 편집한 레시피",
                            "recipeCalories": 예상_칼로리,
                            "recipePeople": "SINGLE|DOUBLE|FAMILY",
                            "recipeTime": 예상 시간,
                            "recipeCookMethod": "주요 조리 방법",
                            "ingredientsList": [
                                {
                                    "ingredientName": "재료명",
                                    "ingredientsCount": "재료_용량"
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

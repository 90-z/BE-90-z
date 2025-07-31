package com.be90z.external.gemini.service;

import com.be90z.external.gemini.dto.GeminiReqDTO;
import com.be90z.external.gemini.dto.GeminiResDTO;
import com.be90z.external.gemini.prompt.GeminiIngredientPrompt;
import com.be90z.external.gemini.prompt.GeminiRecipePrompt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/* Gemini API와 통신하는 외부 서비스
 * 외부 API 호출 로직을 담당 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    @Qualifier("geminiWebClient")
    private final WebClient geminiWebClient;

    @Qualifier("geminiApiKey")
    private final String geminiApiKey;

    private final GeminiRecipePrompt geminiRecipePrompt;
    private final GeminiIngredientPrompt geminiIngredientPrompt;

    //    레시피 분석
    public String analyzeRecipe(String recipeName, String recipeContent) {
        log.info("Gemini 레시피 분석 요청 - 제목: '{}', 내용 길이: {} 문자",
                recipeName != null ? recipeName : "(제목 없음)", recipeContent.length());

        try {
            if (!geminiRecipePrompt.isValidInput(recipeName, recipeContent)) {
                log.warn("레시피 입력이 유효하지 않음 - 내용 길이: {} 문자", recipeContent.length());
                throw new IllegalArgumentException("레시피 내용이 너무 짧거나 깁니다");
            }
//            프롬프트 생성
            String prompt = geminiRecipePrompt.createGeminiRecipePrompt(recipeName, recipeContent);

//            Gemini API 호출
            String responseGemini = callGeminiApi(prompt);

            if (responseGemini != null && !responseGemini.trim().isEmpty()) {
                log.info("Gemini 레시피 분석 성공");
                return responseGemini;
            } else {
                log.warn("Gemini api 응답 비어있음");
                return null;
            }
        } catch (IllegalArgumentException e) {
            log.error("입력 검증 실패: {} ", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Gemini 레시피 분석 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("AI 레시피 분석 실패", e);
        }

    }

    //    재료 추출
    public String extractIngredientsForTags(String recipeName, String recipeContent) {
        log.info("Gemini 태그용 재료 추출 요청 - 제목: '{}', 내용 길이: {} 문자",
                recipeName != null ? recipeName : "(제목 없음)", recipeContent.length());

        try {
            if (!geminiIngredientPrompt.isValidInput(recipeName, recipeContent)) {
                log.warn("재료 추출 입력이 유효하지 않음 - 내용 길이: {} 문자", recipeContent.length());
                throw new IllegalArgumentException("레시피 내용이 너무 짧거나 깁니다");
            }

            // 재료 추출용 프롬프트 생성
            String prompt = geminiIngredientPrompt.createIngredientExtractionPrompt(recipeName, recipeContent);

            // Gemini API 호출
            String responseGemini = callGeminiApi(prompt);

            if (responseGemini != null && !responseGemini.trim().isEmpty()) {
                log.info("Gemini 재료 추출 성공");
                return responseGemini;
            } else {
                log.warn("Gemini 재료 추출 응답 비어있음");
                return null;
            }
        } catch (IllegalArgumentException e) {
            log.error("재료 추출 입력 검증 실패: {} ", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Gemini 재료 추출 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("AI 재료 추출 실패", e);
        }
    }


    //    Gemini 호출
    private String callGeminiApi(String prompt) {
        try {
            log.debug("Gemini API 호출 시작 - prompt 길이: {} 문자", prompt.length());

            GeminiReqDTO geminiReqDTO = GeminiReqDTO.getGeminiReqDTO(prompt);

            GeminiResDTO geminiResDTO = geminiWebClient
                    .post()
                    .uri("?key=" + geminiApiKey)
                    .bodyValue(geminiReqDTO)
                    .retrieve()
                    .bodyToMono(GeminiResDTO.class)
                    .doOnNext(res -> log.info("Gemini API 응답 원문: {}", res))
                    .block();

            if (geminiResDTO != null && geminiResDTO.isValidResponse()) {
                String geminiResComplet = geminiResDTO.getFirstCandidateText();
                log.debug("Gemini API 호출 성공 - 응답 미리보기: {}"
                        , geminiResComplet.length() > 100 ? geminiResComplet.substring(0, 100) + "..." : geminiResComplet);
                return geminiResComplet;
            } else {
                log.warn("Gemini API 응답 유효하지 않음");
                return null;
            }

        } catch (WebClientResponseException e) {
            log.error("Gemini API HTTP 오류 - 상태코드: {}, 메시지: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("Gemini API 호출 중 예상치 못한 오류: {}", e.getMessage(), e);
            throw new RuntimeException("Gemini API 통신 실패", e);
        }
    }
//    Reactive 방식
    public Mono<String> analyzeRecipeAsync(String recipeName, String recipeContent) {
        log.info("Gemini 비동기 레시피 분석 요청 - 제목:{}, 내용길이:{}",
                recipeName != null ? recipeName : "(제목없음)", recipeContent.length());

        if(!geminiRecipePrompt.isValidInput(recipeName, recipeContent)) {
            return Mono.error(new IllegalArgumentException("레시피 내용이 너무 짧거나 깁니다."));
        }

        String prompt = geminiRecipePrompt.createGeminiRecipePrompt(recipeName, recipeContent);
        GeminiReqDTO geminiReqDTO = GeminiReqDTO.getGeminiReqDTO(prompt);

        return geminiWebClient
                .post()
                .uri("?key=" + geminiApiKey)
                .bodyValue(geminiReqDTO)
                .retrieve()
                .bodyToMono(GeminiResDTO.class)
                .map(response -> {
                    if(response != null && response.isValidResponse()) {
                        String result = response.getFirstCandidateText();
                        log.info("Gemini 비동기 레시피 분석 성공");
                        return result;
                    } else {
                        throw new RuntimeException("Gemini API 응답이 유효하지 않습니다.");
                    }
                })
                .doOnError(error -> log.error("Gemini 비동기 API 호출 실패 {}", error.getMessage()));
    }
}

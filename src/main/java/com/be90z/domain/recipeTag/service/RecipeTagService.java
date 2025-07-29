package com.be90z.domain.recipeTag.service;

import com.be90z.domain.recipe.entity.Recipe;
import com.be90z.domain.recipe.repository.RecipeRepository;
import com.be90z.domain.recipeTag.dto.RecipeIngredientAIResDTO;
import com.be90z.domain.recipeTag.dto.RecipeTagResDTO;
import com.be90z.domain.recipeTag.dto.RecipeTagSelectReqDTO;
import com.be90z.domain.recipeTag.entity.RecipeTag;
import com.be90z.domain.recipeTag.repository.RecipeTagRepository;
import com.be90z.external.gemini.service.GeminiResParser;
import com.be90z.external.gemini.service.GeminiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeTagService {

    private final RecipeTagRepository recipeTagRepository;
    private final RecipeRepository recipeRepository;
    private final GeminiService geminiService;
    private final GeminiResParser geminiResParser;
    private final ObjectMapper objectMapper;

//    AI 를 통한 관련 재료 15개 추출
    @Transactional(readOnly = true)
    public RecipeIngredientAIResDTO extractIngredientsFromRecipe(String recipeName, String recipeContent) {
        try {
            log.info("AI 재료 추출 요청 - 레시피명: {}", recipeName);
            String geminiResponse = geminiService.extractIngredientsForTags(recipeName, recipeContent);

//            Json 응답에서 재료 리스트 추출
            List<String> extractedIngredients = geminiResParser.parseReponse(geminiResponse, new TypeReference<List<String>>() {});

            log.info("AI 재료 추출 완료 - 추출된 재료 개수: {}", extractedIngredients.size());
            return new RecipeIngredientAIResDTO(extractedIngredients);

        } catch (Exception e) {
            log.error("AI 재료 추출 실패: {}", e.getMessage(), e);
            throw new RuntimeException("재료 추출에 실패했습니다: " + e.getMessage());
        }
    }

    // 선택된 재료 3개를 recipe_tag에 저장
    @Transactional
    public List<RecipeTagResDTO> saveSelectedTags(Long recipeCode, RecipeTagSelectReqDTO selectionReqDTO) {
        if (!selectionReqDTO.isValid()) {
            throw new IllegalArgumentException("1개 이상 3개 이하의 재료를 선택해야 합니다.");
        }
        Recipe recipe = recipeRepository.findById(recipeCode)
                .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다: " + recipeCode));

        // 기존 태그들 삭제
        recipeTagRepository.deleteByRecipeTagCode(recipeCode);

        // 새로운 태그들 저장
        List<RecipeTag> savedTags = selectionReqDTO.getSelectedRecipeTags().stream()
                .map(ingredient -> {
                    RecipeTag recipeTag = new RecipeTag(ingredient, recipe);
                    return recipeTagRepository.save(recipeTag);
                })
                .collect(Collectors.toList());

        log.info("레시피 태그 저장 완료 - 레시피코드: {}, 태그 개수: {}", recipeCode, savedTags.size());

        return savedTags.stream()
                .map(tag -> new RecipeTagResDTO(tag.getRecipeTagCode(), tag.getRecipeTagName()))
                .collect(Collectors.toList());
    }

    // 특정 레시피의 태그 조회
    @Transactional(readOnly = true)
    public List<RecipeTagResDTO> getRecipeTags(Long recipeCode) {
        List<RecipeTag> recipeTags = recipeTagRepository.findByRecipe_RecipeCode(recipeCode);

        return recipeTags.stream()
                .map(tag -> new RecipeTagResDTO(tag.getRecipeTagCode(), tag.getRecipeTagName()))
                .collect(Collectors.toList());
    }

    // 레시피 수정 시 태그 업데이트
    @Transactional
    public List<RecipeTagResDTO> updateRecipeTags(Long recipeCode, RecipeTagSelectReqDTO selectionReqDTO) {
        log.info("레시피 태그 수정 요청 - 레시피코드: {}", recipeCode);

        // 기존 저장 로직과 동일하게 처리 (삭제 후 재생성)
        return saveSelectedTags(recipeCode, selectionReqDTO);
    }

    // 레시피 삭제 시 관련 태그들도 삭제
    @Transactional
    public void deleteRecipeTags(Long recipeCode) {
        recipeTagRepository.deleteById(recipeCode);
        log.info("레시피 태그 삭제 완료 - 레시피코드: {}", recipeCode);
    }
}

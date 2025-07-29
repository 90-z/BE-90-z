package com.be90z.domain.recipeTag.dto;

import lombok.Data;
import java.util.List;

/**
 * 레시피 태그 관련 DTO 클래스들
 * 이 파일에는 3개의 DTO 클래스가 포함되어 있습니다:
 * 1. RecipeIngredientExtractionResDTO - AI 재료 추출 응답
 * 2. RecipeTagSelectionReqDTO - 사용자 재료 선택 요청
 * 3. RecipeTagResDTO - 레시피 태그 응답
 */

// Ai가 추출한 재료 15개 리스트 응답 DTO
@Data
public class RecipeIngredientAIResDTO {
    private List<String> IngredientWithAI;

    public RecipeIngredientAIResDTO(List<String> IngredientWithAI) {
        this.IngredientWithAI = IngredientWithAI;
    }
}

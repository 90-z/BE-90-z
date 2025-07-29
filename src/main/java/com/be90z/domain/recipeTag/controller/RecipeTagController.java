package com.be90z.domain.recipeTag.controller;

import com.be90z.domain.recipeTag.dto.RecipeIngredientAIResDTO;
import com.be90z.domain.recipeTag.dto.RecipeTagResDTO;
import com.be90z.domain.recipeTag.dto.RecipeTagSelectReqDTO;
import com.be90z.domain.recipeTag.service.RecipeTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "recipe-tag", description = "레시피 태그 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipe-tag")
public class RecipeTagController {

    private final RecipeTagService recipeTagService;

    @PostMapping("/ai-recipeTae")
    @Operation(summary = "AI 재료 추출", description = "레시피 내용을 분석하여 관련 재료 15개를 추출합니다")
    public ResponseEntity<RecipeIngredientAIResDTO> extractIngredients(
            @RequestParam String recipeName,
            @RequestParam String recipeContent) {

        RecipeIngredientAIResDTO response = recipeTagService.extractIngredientsFromRecipe(recipeName, recipeContent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{recipeCode}/select")
    @Operation(summary = "메인 재료 선택", description = "추출된 재료 중 최대 3개를 선택하여 레시피 태그로 저장합니다")
    public ResponseEntity<List<RecipeTagResDTO>> selectAndSaveTags(
            @PathVariable Long recipeCode,
            @RequestBody RecipeTagSelectReqDTO selectionReqDTO) {

        List<RecipeTagResDTO> savedTags = recipeTagService.saveSelectedTags(recipeCode, selectionReqDTO);
        return ResponseEntity.ok(savedTags);
    }

    @GetMapping("/{recipeCode}")
    @Operation(summary = "레시피 태그 조회", description = "특정 레시피의 모든 태그를 조회합니다")
    public ResponseEntity<List<RecipeTagResDTO>> getRecipeTags(@PathVariable Long recipeCode) {
        List<RecipeTagResDTO> recipeTags = recipeTagService.getRecipeTags(recipeCode);
        return ResponseEntity.ok(recipeTags);
    }

    @PutMapping("/{recipeCode}")
    @Operation(summary = "레시피 태그 수정", description = "레시피 수정 시 태그를 업데이트합니다")
    public ResponseEntity<List<RecipeTagResDTO>> updateRecipeTags(
            @PathVariable Long recipeCode,
            @RequestBody RecipeTagSelectReqDTO selectionReqDTO) {

        List<RecipeTagResDTO> updatedTags = recipeTagService.updateRecipeTags(recipeCode, selectionReqDTO);
        return ResponseEntity.ok(updatedTags);
    }

    @DeleteMapping("/{recipeCode}")
    @Operation(summary = "레시피 태그 삭제", description = "특정 레시피의 모든 태그를 삭제합니다")
    public ResponseEntity<String> deleteRecipeTags(@PathVariable Long recipeCode) {
        recipeTagService.deleteRecipeTags(recipeCode);
        return ResponseEntity.ok("레시피 태그가 성공적으로 삭제되었습니다.");
    }
}

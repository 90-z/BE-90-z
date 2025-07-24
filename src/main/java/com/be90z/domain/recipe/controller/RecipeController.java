package com.be90z.domain.recipe.controller;

import com.be90z.domain.recipe.dto.RecipeAiResDTO;
import com.be90z.domain.recipe.dto.RecipeCreateFreeDTO;
import com.be90z.domain.recipe.dto.RecipeResDTO;
import com.be90z.domain.recipe.dto.RecipeUpdateDTO;
import com.be90z.domain.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "recipe", description = "레시피 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/ai")
    @Operation(summary = " AI 레시피 분석", description = "제목과 내용을 AI로 분석하여 레시피 상세 내용을 생성합니다")
    public ResponseEntity<RecipeAiResDTO> createRecipeWithAi(
            @RequestBody RecipeCreateFreeDTO recipeCreateFreeDTO) throws IOException {
        RecipeAiResDTO recipeAiResDTO = recipeService.createRecipeWithAi(recipeCreateFreeDTO);
        return ResponseEntity.ok(recipeAiResDTO);
    }

    @PostMapping
    @Operation(summary = "레시피 최종 등록", description = "AI가 적용된 레시피를 등록합니다")
    public ResponseEntity<Void> createRecipe(
            @RequestBody RecipeAiResDTO recipeAiResDTO) throws IOException {
        recipeService.createRecipe(recipeAiResDTO);
        return ResponseEntity.ok().build();
    }


    @GetMapping
    @Operation(summary = "레시피 전체 조회", description = "등록된 모든 레시피를 조회합니다.")
    public ResponseEntity<List<RecipeResDTO>> getAllRecipe() {
        List<RecipeResDTO> recipeAllList = recipeService.getAllRecipe();
        return ResponseEntity.ok(recipeAllList);
    }

    @GetMapping("/{recipeCode}")
    @Operation(summary = "레시피 상세 조회", description = "특정 레시피를 상세 조회합니다.")
    public ResponseEntity<RecipeResDTO> getRecipe(@PathVariable Long recipeCode) {
        RecipeResDTO recipeResDTO = recipeService.getRecipe(recipeCode);
        return ResponseEntity.ok(recipeResDTO);
    }

    @PutMapping("/{recipeCode}")
    @Operation(summary = "레시피 수정", description = "기존 레시피를 수정합니다.")
    public ResponseEntity<Void> updateRecipe(@PathVariable Long recipeCode, @RequestBody RecipeUpdateDTO recipeUpdateDTO) {
        recipeService.updateRecipe(recipeCode, recipeUpdateDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{recipeCode}")
    @Operation(summary = "레시피 삭제", description = "특정 레시피를 삭제합니다.")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long recipeCode) {
        recipeService.deleteRecipe(recipeCode);
        return ResponseEntity.ok().build();
    }

}

package com.be90z.domain.recipe.controller;

import com.be90z.domain.recipe.dto.RecipeAiResDTO;
import com.be90z.domain.recipe.dto.RecipeCreateFreeDTO;
import com.be90z.domain.recipe.dto.RecipeResDTO;
import com.be90z.domain.recipe.dto.RecipeUpdateDTO;
import com.be90z.domain.recipe.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Tag(name = "recipe", description = "레시피 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipe")
public class RecipeController {

    private final RecipeService recipeService;
    private final ObjectMapper objectMapper;

//    @PostMapping("/ai")
//    @Operation(summary = " AI 레시피 분석", description = "제목과 내용을 AI로 분석하여 레시피 상세 내용을 생성합니다")
//    public ResponseEntity<RecipeAiResDTO> createRecipeWithAi(
//            @RequestBody RecipeCreateFreeDTO recipeCreateFreeDTO) throws IOException {
//        RecipeAiResDTO recipeAiResDTO = recipeService.createRecipeWithAi(recipeCreateFreeDTO);
//        return ResponseEntity.ok(recipeAiResDTO);
//    }

    @PostMapping("/ai")
    @Operation(summary = "AI 레시피 분석 - 비동기", description = "제목과 내용을 AI로 분석하여 레시피 상세 내용을 생성합니다.")
    public Mono<ResponseEntity<RecipeAiResDTO>> createRecipeWithAiAsync(
            @RequestBody RecipeCreateFreeDTO recipeCreateFreeDTO) throws IOException {
        return recipeService.createRecipeWithAiAsync(recipeCreateFreeDTO)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("비동기 AI 레시피 분석 완료"))
                .doOnError(error -> log.error("비동기 AI 레시피 분석 실패: ", error.getMessage()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "레시피 최종 등록",
            description = "AI가 적용된 레시피를 등록합니다, 최소 1개 이상의 이미지가 필요합니다."
    )
    public ResponseEntity<String> createRecipe(
            @Parameter(
                    description = "레시피 정보",
                    required = true,
                    schema = @Schema(implementation = RecipeAiResDTO.class)
            )
            @RequestPart(value = "recipe", required = true) String recipeJson,

            @Parameter(
                    description = "레시피 이미지 파일들 (최소 1개 필수)",
                    required = true
            )
            @RequestPart("images") List<MultipartFile> images,
            @RequestParam(value = "userId", required = false, defaultValue = "1") Long userId
    ) throws IOException {

        RecipeAiResDTO recipeAiResDTO = objectMapper.readValue(recipeJson, RecipeAiResDTO.class);

        recipeService.createRecipe(recipeAiResDTO, images);
        return ResponseEntity.ok().body("레시피가 성공적으로 등록되었습니다.");
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

    @PutMapping(value = "/{recipeCode}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "레시피 수정",
            description = "기존 레시피를 수정합니다."
    )
    public ResponseEntity<String> updateRecipe(
            @PathVariable Long recipeCode,
            @Parameter(
                    description = "수정할 레시피 정보 (JSON 문자열)",
                    required = true,
                    schema = @Schema(implementation = RecipeUpdateDTO.class)
            )
            @RequestPart(value = "recipe", required = true) String recipeUpdateJson,

            @Parameter(
                    description = "새로 추가할 이미지 파일들",
                    schema = @Schema(type = "array", format = "binary")
            )
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages,

            @Parameter(
                    description = "삭제할 이미지 ID들 (쉼표로 구분된 문자열, 예: '1,2,3')",
                    schema = @Schema(type = "string", example = "1,2,3")
            )
            @RequestParam(value = "deleteImageIds", required = false) String deleteImageIds) throws IOException {

        RecipeUpdateDTO recipeUpdateDTO = objectMapper.readValue(recipeUpdateJson, RecipeUpdateDTO.class);

        // 삭제할 이미지 ID 리스트 처리
        List<Long> deleteImageIdList = new ArrayList<>();
        if (deleteImageIds != null && !deleteImageIds.trim().isEmpty()) {
            String[] ids = deleteImageIds.split(",");
            for (String id : ids) {
                try {
                    deleteImageIdList.add(Long.parseLong(id.trim()));
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body("잘못된 이미지 ID 형식입니다: " + id);
                }
            }
        }

        recipeService.updateRecipe(recipeCode, recipeUpdateDTO, newImages, deleteImageIdList);
        return ResponseEntity.ok().body("레시피가 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{recipeCode}")
    @Operation(summary = "레시피 삭제", description = "특정 레시피를 삭제합니다.")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long recipeCode) {
        recipeService.deleteRecipe(recipeCode);
        return ResponseEntity.ok().body("레시피가 성공적으로 삭제되었습니다.");
    }
}
package com.be90z.domain.recipe.controller;

import com.be90z.domain.recipe.dto.RecipeAiResDTO;
import com.be90z.domain.recipe.dto.RecipeCreateFreeDTO;
import com.be90z.domain.recipe.dto.RecipeResDTO;
import com.be90z.domain.recipe.dto.RecipeUpdateDTO;
import com.be90z.domain.recipe.service.RecipeService;
import com.be90z.domain.user.dto.response.AuthErrorResDTO;
import com.be90z.domain.user.entity.User;
import com.be90z.global.util.AuthUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final AuthUtil authUtil;

    @PostMapping("/ai")
    @Operation(summary = "AI 레시피 분석 - 비동기 (회원 전용)", description = "제목과 내용을 AI로 분석하여 레시피 상세 내용을 생성합니다.")
    public Mono<ResponseEntity<?>> createRecipeWithAiAsync(
            @RequestBody RecipeCreateFreeDTO recipeCreateFreeDTO,
            @RequestHeader(value = "Authorization", required = false) String token) throws IOException {

//        회원 권한 체크
        if (!authUtil.isValidToken(token)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthErrorResDTO.unauthorized()));
        }

        return recipeService.createRecipeWithAiAsync(recipeCreateFreeDTO)
                .map(result -> ResponseEntity.ok((Object) result));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "레시피 최종 등록 (회원 전용)", description = "AI가 적용된 레시피를 등록합니다, 최소 1개 이상의 이미지가 필요합니다.")
    public ResponseEntity<?> createRecipe(
            @Parameter(description = "레시피 정보", required = true, schema = @Schema(implementation = RecipeAiResDTO.class))
            @RequestPart(value = "recipe", required = true) String recipeJson,
            @Parameter(description = "레시피 이미지 파일들 (최소 1개 필수)", required = true)
            @RequestPart("images") List<MultipartFile> images,
            @RequestHeader(value = "Authorization", required = false) String token
    ) throws IOException {

        //        회원 권한 체크
        User user = authUtil.getUserFromToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthErrorResDTO.unauthorized());
        }

        RecipeAiResDTO recipeAiResDTO = objectMapper.readValue(recipeJson, RecipeAiResDTO.class);

        recipeService.createRecipe(recipeAiResDTO, images, user);
        return ResponseEntity.ok().body("레시피가 성공적으로 등록되었습니다.");
    }

    @GetMapping("/search")
    @Operation(summary = "키워드와 재료명으로 레시피 검색", description = "키워드와 재료명으로 레시피를 검색합니다.")
    public ResponseEntity<List<RecipeResDTO>> searchRecipes(
            @Parameter(description = "검색 키워드 (레시피 제목, 내용에서 검색합니다)")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "검색 키워드(재료에서 검색합니다)")
            @RequestParam(required = false) String ingredient) {
        List<RecipeResDTO> recipeResDTOList = recipeService.searchRecipe(keyword, ingredient);
        return ResponseEntity.ok(recipeResDTOList);
    }

    @GetMapping("/search/keyword")
    @Operation(summary = "키워드 레시피 검색", description = "키워드로 레시피를 검색합니다.")
    public ResponseEntity<List<RecipeResDTO>> searchRecipesByKeyword(
            @Parameter(description = "검색할 키워드", required = true)
            @RequestParam(required = false) String keyword) {
        List<RecipeResDTO> recipeResDTOList = recipeService.searchRecipeByKeyword(keyword);
        return ResponseEntity.ok(recipeResDTOList);
    }

    @GetMapping("/search/ingredient")
    @Operation(summary = "재료 레시피 검색", description = "재료로 레시피를 검색합니다.")
    public ResponseEntity<List<RecipeResDTO>> searchRecipesByIngredient(
            @Parameter(description = "검색할 재료", required = true)
            @RequestParam(required = false) String Ingredient) {
        List<RecipeResDTO> recipeResDTOList = recipeService.searchRecipeByIngredient(Ingredient);
        return ResponseEntity.ok(recipeResDTOList);
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
    public ResponseEntity<?> updateRecipe(
            @PathVariable Long recipeCode,
            @Parameter(description = "수정할 레시피 정보 (JSON 문자열)", required = true, schema = @Schema(implementation = RecipeUpdateDTO.class))
            @RequestPart(value = "recipe", required = true) String recipeUpdateJson,
            @Parameter(description = "새로 추가할 이미지 파일들", schema = @Schema(type = "array", format = "binary"))
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages,
            @Parameter(description = "삭제할 이미지 ID들 (쉼표로 구분된 문자열, 예: '1,2,3')", schema = @Schema(type = "string", example = "1,2,3"))
            @RequestParam(value = "deleteImageIds", required = false) String deleteImageIds,
            @RequestHeader(value = "Authorization", required = false) String token) throws IOException {

        //        회원 권한 체크
        User user = authUtil.getUserFromToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthErrorResDTO.unauthorized());
        }
//        작성자 권한 체크
        if (!recipeService.isRecipeOwner(recipeCode, user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(AuthErrorResDTO.forbidden());
        }

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
    public ResponseEntity<?> deleteRecipe(@PathVariable Long recipeCode, @RequestHeader(value = "Authorization", required = false) String token) {

        //        회원 권한 체크
        User user = authUtil.getUserFromToken(token);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthErrorResDTO.unauthorized());
        }
//        작성자 권한 체크
        if(!recipeService.isRecipeOwner(recipeCode, user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(AuthErrorResDTO.forbidden());
        }
        recipeService.deleteRecipe(recipeCode);
        return ResponseEntity.ok().body("레시피가 성공적으로 삭제되었습니다.");
    }
}
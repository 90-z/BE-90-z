package com.be90z.domain.tag.controller;

import com.be90z.domain.recipe.dto.RecipeResDTO;
import com.be90z.domain.recipe.entity.Recipe;
import com.be90z.domain.recipe.service.RecipeService;
import com.be90z.domain.tag.dto.TagDTO;
import com.be90z.domain.tag.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "tag", description = "태그(요리방식) API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tag")
public class TagController {

    private final TagService tagService;
    private final RecipeService recipeService;

    @GetMapping
    @Operation(summary = "모든 요리방식 태그 조회", description = "등록된 모든 요리방식 태그를 조회합니다. (냄비, 프라이팬, 전자레인지, 오븐, 기타)")
    public ResponseEntity<List<TagDTO>> getAllTags() {
        List<TagDTO> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{tagName}/recipes")
    @Operation(summary = "요리 방식별 레시피 조회", description = "특정 요리방식으로 만든 모든 레시피를 조회합니다.")
    public ResponseEntity<List<RecipeResDTO>> getRecipesByTag(@PathVariable("tagName") String tagName) {
        List<Recipe> recipeEntities = tagService.getRecipeByTag(tagName);

        List<RecipeResDTO> recipeResDTOS = recipeEntities.stream()
                .map(recipeService::convertToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(recipeResDTOS);
    }
}

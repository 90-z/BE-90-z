package com.be90z.domain.recipeTag.dto;

import lombok.Data;

// 레시피 태그 응답 DTO
@Data
public class RecipeTagResDTO {
    private Long recipeTagCode;
    private String recipeTagName;

    public RecipeTagResDTO(Long recipeTagCode, String recipeTagName) {
        this.recipeTagCode = recipeTagCode;
        this.recipeTagName = recipeTagName;
    }
}

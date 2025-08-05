package com.be90z.domain.recipe.dto;

import lombok.Data;

@Data
public class RecipePopularResDTO {
    private Long recipeCode;
    private String recipeName;
    private String mainImgUrl;
    private Long bookmarkCount;

    public RecipePopularResDTO(Long recipeCode, String recipeName, String mainImgUrl, Long bookmarkCount) {
        this.recipeCode = recipeCode;
        this.recipeName = recipeName;
        this.mainImgUrl = mainImgUrl;
        this.bookmarkCount = bookmarkCount;
    }
}

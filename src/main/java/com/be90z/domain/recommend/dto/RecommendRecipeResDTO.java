package com.be90z.domain.recommend.dto;

import lombok.Data;

@Data
public class RecommendRecipeResDTO {
    private Long recipeCode;
    private String recipeName;
    private String mainImgUrl;
    private Long count;

    public RecommendRecipeResDTO(Long recipeCode, String recipeName, String mainImgUrl, Long count) {
        this.recipeCode = recipeCode;
        this.recipeName = recipeName;
        this.mainImgUrl = mainImgUrl;
        this.count = count;
    }
}

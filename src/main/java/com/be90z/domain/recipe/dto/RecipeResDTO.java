package com.be90z.domain.recipe.dto;

import com.be90z.domain.recipe.entity.RecipePeople;
import jakarta.persistence.Lob;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecipeResDTO {
    private Long recipeCode;
    private String recipeName;
    @Lob
    private String recipeContent;
    private Integer recipeCalories;
    private String recipeCookMethod;
    private RecipePeople recipePeople;
    private Integer recipeTime;
    private LocalDateTime createdAt;
    private List<IngredientsResDTO> ingredientsList;
    private List<ImageResDTO> imagesList;

    @Data
    public static class IngredientsResDTO {
        private Long ingredientsCode;
        private String ingredientsName;
        private Integer ingredientsCount;
    }

    @Data
    public static class ImageResDTO {
        private Long imgCode;
        private String imgName;
        private String imgS3url;
    }
}

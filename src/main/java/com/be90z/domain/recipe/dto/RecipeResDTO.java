package com.be90z.domain.recipe.dto;

import com.be90z.domain.recipe.entity.RecipePeople;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecipeResDTO {
    private Long recipeCode;
    private String recipeName;
    private String recipeContent;
    private Integer recipeCalories;
    private String recipeCookMethod;
    private RecipePeople recipePeople;
    private Integer recipeTime;
    private LocalDateTime createdAt;
    private List<IngredientsResDTO> ingredientsList;

    @Data
    public static class IngredientsResDTO {
        private Long ingredientsCode;
        private String ingredientsName;
        private Integer ingredientsCount;
    }
}

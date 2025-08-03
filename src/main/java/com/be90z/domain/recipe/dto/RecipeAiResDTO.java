package com.be90z.domain.recipe.dto;

import com.be90z.domain.recipe.entity.RecipePeople;
import jakarta.persistence.Lob;
import lombok.Data;

import java.util.List;

@Data
public class RecipeAiResDTO {
    private String recipeName;
    private List<String> suggestedIngredients; // AI가 추출한 15개 재료 리스트
    @Lob
    private String recipeContent;
    private Integer recipeCalories;
    private RecipePeople recipePeople;
    private Integer recipeTime;
    private String recipeCookMethod;

    private List<IngredientsDTO> ingredientsList;

    @Data
    public static class IngredientsDTO {
        private String ingredientName;
        private Integer ingredientsCount;
    }
}

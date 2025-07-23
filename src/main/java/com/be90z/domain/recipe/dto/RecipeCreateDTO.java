package com.be90z.domain.recipe.dto;

import com.be90z.domain.recipe.entity.RecipePeople;
import lombok.Data;

import java.util.List;

@Data
public class RecipeCreateDTO {
    private String recipeName;
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

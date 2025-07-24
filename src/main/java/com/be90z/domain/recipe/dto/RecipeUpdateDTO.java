package com.be90z.domain.recipe.dto;

import com.be90z.domain.recipe.entity.RecipePeople;
import lombok.Data;

import java.util.List;

@Data
public class RecipeUpdateDTO {
    private String recipeName;
    private String recipeContent;
    private Integer recipeCalories;
    private String recipeCookMethod;
    private RecipePeople recipePeople;
    private Integer recipeTime;
    private List<IngredientsDTO> ingredientsList;

    @Data
    public static class IngredientsDTO {
        private String ingredientsName;
        private Integer ingredientsCount;
    }
}

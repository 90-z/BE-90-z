package com.be90z.domain.recipe.dto;

import com.be90z.domain.recipe.entity.RecipePeople;
import jakarta.persistence.Lob;
import lombok.Data;

import java.util.List;

@Data
public class RecipeUpdateDTO {
    private String recipeName;
    private List<String> selectedTags; // 사용자가 선택한 태그
    @Lob
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

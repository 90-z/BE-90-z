package com.be90z.domain.recipe.dto;

import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class RecipeCreateFreeDTO {
    private String recipeName;
    @Lob
    private String recipeContent;
}

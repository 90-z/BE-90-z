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
    private List<RecipeTagResDTO> recipeTagList;
    private Long userId;
    private String authorNickname;


    @Lob
    private String recipeContent;
    private Integer recipeCalories;
    private String recipeCookMethod;
    private RecipePeople recipePeople;
    private Integer recipeTime;
    private LocalDateTime createdAt;
    private List<IngredientsResDTO> ingredientsList;
    private List<ImageResDTO> imagesList;

    public Long getUserId() {
        return userId;
    }

    public void setAuthorId(Long userId) {
        this.userId = userId;
    }

    public void setAuthorNickname(String nickname) {
        this.authorNickname = nickname;
    }

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

    @Data
    public static class RecipeTagResDTO {
        private Long recipeTagCode;
        private String tagName;

        public RecipeTagResDTO(Long recipeTagCode, String tagName) {
            this.recipeTagCode = recipeTagCode;
            this.tagName = tagName;
        }
    }
}

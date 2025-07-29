package com.be90z.domain.recipeTag.entity;

import com.be90z.domain.recipe.entity.Recipe;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name="recipe_tag")
public class RecipeTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_tag_code", nullable = false)
    private Long recipeTagCode;

    @Column(name = "recipe_tag_name")
    private String recipeTagName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_code")
    private Recipe recipe;

    public RecipeTag(String recipeTagName, Recipe recipe) {
        this.recipeTagName = recipeTagName;
        this.recipe = recipe;
    }

    public void updateTagName(String recipeTagName) {
        this.recipeTagName = recipeTagName;
    }
}

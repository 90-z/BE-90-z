package com.be90z.domain.recipe.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ingredients")
public class Ingredients {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ingredients_code", nullable = false)
    private Long ingredientsCode;

    @Column(name="ingredients_name", nullable = false)
    private String ingredientsName;

    @Column(name="ingredients_count", nullable = false)
    private Integer ingredientsCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="recipe_code")
    private Recipe recipe;

    public Ingredients(String ingredientsName, Integer ingredientsCount) {
        this.ingredientsName = ingredientsName;
        this.ingredientsCount = ingredientsCount;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}

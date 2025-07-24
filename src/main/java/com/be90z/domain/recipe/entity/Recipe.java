package com.be90z.domain.recipe.entity;

import com.be90z.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "recipe")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_code", nullable = false)
    private Long recipeCode;

    @Column(name = "recipe_name", nullable = false)
    private String recipeName;

    @Column(name = "recipe_content", nullable = false, columnDefinition = "TEXT")
    private String recipeContent;

    @Column(name = "recipe_calories", nullable = false)
    private Integer recipeCalories;

    @Column(name = "recipe_cook_method", nullable = false)
    private String recipeCookMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipe_people", nullable = false)
    private RecipePeople recipePeople;

    @Column(name = "recipe_time", nullable = false)
    private Integer recipeTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingredients> ingredients = new ArrayList<>();

    public Recipe(String recipeName, String recipeContent) {
        this.recipeName = recipeName;
        this.recipeContent = recipeContent;
        this.recipeCalories = recipeCalories;
        this.recipePeople = recipePeople;
        this.recipeTime = recipeTime;
        this.recipeCookMethod = recipeCookMethod;
        this.user = user;
    }

    // 새로 추가할 생성자 (User 없음) - test 버전
    public Recipe(String recipeName, String recipeContent, Integer recipeCalories,
                  String recipeCookMethod, RecipePeople recipePeople,
                  Integer recipeTime) {
        this.recipeName = recipeName;
        this.recipeContent = recipeContent;
        this.recipeCalories = recipeCalories;
        this.recipePeople = recipePeople;
        this.recipeTime = recipeTime;
        this.recipeCookMethod = recipeCookMethod;
        this.user = null;
    }

    //    재료 추가 메서드
    public void addIngredient(Ingredients ingredient) {
        ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }

//    레시피 수정 메서드
    public void updateRecipe(String recipeName, String recipeContent, Integer recipeCalories, String recipeCookMethod, RecipePeople recipePeople, Integer recipeTime) {
        this.recipeName = recipeName;
        this.recipeContent = recipeContent;
        this.recipeCalories = recipeCalories;
        this.recipePeople = recipePeople;
        this.recipeTime = recipeTime;
        this.recipeCookMethod = recipeCookMethod;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}

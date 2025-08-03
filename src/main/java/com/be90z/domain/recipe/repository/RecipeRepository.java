package com.be90z.domain.recipe.repository;

import com.be90z.domain.recipe.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByRecipeCookMethod(String recipeCookMethod);

    @Query("SELECT r FROM Recipe r WHERE LOWER(r.recipeCookMethod) = LOWER(:cookMethod)")
    List<Recipe> findByRecipeCookMethodIgnoreCase(@Param("cookMethod") String cookMethod);

//    키워드로 레시피 제목 및 내용 검색
    @Query("SELECT DISTINCT r from Recipe r " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(r.recipeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.recipeContent) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Recipe> searchByRecipeKeyword(@Param("keyword") String keyword);

//    재료명으로 검색
    @Query("SELECT DISTINCT r FROM Recipe r " +
            "JOIN r.ingredients i " +
            "WHERE (:ingredient IS NULL OR :ingredient = '' OR " +
            "LOWER(i.ingredientsName) LIKE LOWER(CONCAT('%', :ingredient, '%')))")
    List<Recipe> searchByIngredient(@Param("ingredient") String ingredient);

//    제목, 내용, 재료명으로 검색
    @Query("SELECT DISTINCT r from Recipe r " +
            "LEFT JOIN r.ingredients i " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "       LOWER(r.recipeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "       LOWER(r.recipeContent) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "AND (:ingredient IS NULL OR :ingredient = '' OR " +
            "LOWER(i.ingredientsName) LIKE LOWER(CONCAT('%', :ingredient, '%')))")
    List<Recipe> searchRecipeByKeywordAndIngredient(@Param("keyword") String keyword,
                                                    @Param("ingredient") String ingredient);

}

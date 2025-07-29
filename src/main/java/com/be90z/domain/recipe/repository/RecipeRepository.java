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
}

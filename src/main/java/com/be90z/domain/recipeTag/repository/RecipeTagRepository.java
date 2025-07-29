package com.be90z.domain.recipeTag.repository;

import com.be90z.domain.recipeTag.entity.RecipeTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeTagRepository extends JpaRepository<RecipeTag, Long> {
    void deleteByRecipeTagCode(Long recipeTagCode);
    List<RecipeTag> findByRecipe_RecipeCode(Long recipeCode);
}

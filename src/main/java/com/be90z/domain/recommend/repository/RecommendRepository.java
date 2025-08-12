package com.be90z.domain.recommend.repository;

import com.be90z.domain.recipe.entity.Recipe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecommendRepository extends JpaRepository<Recipe, Long> {
    //    사용자 검색 기록 기반 인기 레시피 조회
    @Query("""
            SELECT r.recipeCode, r.recipeName, COUNT(sl.searchCode) AS searchCount
                        FROM Recipe r
                        JOIN SearchLog sl ON (UPPER(r.recipeName) LIKE UPPER(CONCAT('%', sl.searchKeyword, '%')) OR
                        UPPER(r.recipeContent) LIKE UPPER(CONCAT('%', sl.searchKeyword, '%')))
                        WHERE  sl.user.userId = :userId
                        GROUP BY r.recipeCode, r.recipeName
                        ORDER BY searchCount DESC, r.createdAt DESC
            """)
    List<Object[]> findRecipeByUserSearchLog(@Param("userId") Long userId, Pageable pageable);

    //    사용자가 검색한 재료와 매칭되는 레시피 조회
    @Query("""
            SELECT r.recipeCode, r.recipeName, COUNT(sl.searchCode) AS searchCount
                        FROM Recipe r
                        JOIN r.ingredients i
                        JOIN SearchLog sl ON UPPER(i.ingredientsName) LIKE UPPER(CONCAT('%', sl.searchKeyword, '%'))
                        WHERE sl.user.userId = :userId
                        GROUP BY r.recipeCode, r.recipeName
                        ORDER BY searchCount DESC, r.createdAt DESC 
            """)
    List<Object[]> findRecipeByUserSearchIngredients(@Param("userId") Long userId, Pageable pageable);

}
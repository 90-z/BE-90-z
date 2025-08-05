package com.be90z.domain.bookmark.repository;

import com.be90z.domain.bookmark.entity.Bookmark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    //    북마크된 레시피 존재 확인
    @Query("SELECT COUNT(b) > 0 FROM Bookmark b WHERE b.user.userId = :userId AND b.recipe.recipeCode = :recipeCode")
    boolean existsByUserIdAndRecipeCode(@Param("userId") Long userId, @Param("recipeCode") Long recipeCode);

    //    북마크 해제
    @Modifying
    @Transactional
    @Query("DELETE FROM Bookmark b WHERE b.user.userId = :userId AND b.recipe.recipeCode = :recipeCode")
    void deleteByUserIdAndRecipeCode(@Param("userId") Long userId, @Param("recipeCode") Long recipeCode);

    //    북마크 목록 조회
    @Query("SELECT b FROM Bookmark b JOIN FETCH b.recipe WHERE b.user.userId = :userId ORDER BY b.createdAt DESC")
    List<Bookmark> findByUserIdWithRecipe(@Param("userId") Long userId);

//    인기 레시피 위한 북마크 카운트
    @Query("SELECT b.recipe.recipeCode, b.recipe.recipeName, COUNT(b) AS bookmarkCount " +
            "FROM Bookmark b " +
            "GROUP BY b.recipe.recipeCode, b.recipe.recipeName " +
            "ORDER BY COUNT(b) DESC ")
    List<Object[]> findTopRecipe(Pageable pageable);
}

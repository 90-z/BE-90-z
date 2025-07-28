package com.be90z.domain.recipe.repository;

import com.be90z.domain.recipe.entity.Image;
import com.be90z.domain.recipe.entity.ImageCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    // 메서드 선언만 하고 구현부 제거
    List<Image> findByRecipeRecipeCode(Long recipeCode);

    void deleteByRecipeRecipeCode(Long recipeCode);

    // 추가: 카테고리별 조회 (나중에 필요할 수 있음)
    List<Image> findByRecipeRecipeCodeAndImgCategory(Long recipeCode, ImageCategory imageCategory);

//    public List<Image> findByRecipeCode(Long recipeCode) {
//        return null;
//    }
//
//    public void deleteByRecipeCode(Long recipeCode) {
//    }
}

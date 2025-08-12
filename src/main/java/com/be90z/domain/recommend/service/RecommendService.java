package com.be90z.domain.recommend.service;

import com.be90z.domain.bookmark.repository.BookmarkRepository;
import com.be90z.domain.recipe.entity.Image;
import com.be90z.domain.recipe.service.ImageService;
import com.be90z.domain.recommend.dto.RecommendRecipeResDTO;
import com.be90z.domain.recommend.repository.RecommendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final BookmarkRepository bookmarkRepository;
    private final RecommendRepository recommendRepository;
    private final ImageService imageService;

    //    로그인 시
    @Transactional(readOnly = true)
    public List<RecommendRecipeResDTO> getRecommendRecipeByLog(Long userId) {
        Pageable logPageable = PageRequest.of(0, 3);
//        검색 기록 기반으로 레시피 추천
        List<Object[]> getRecommendRecipeByLog = recommendRepository.findRecipeByUserSearchLog(userId, logPageable);
//        검색 기록 존재하지 않거나 부족한 경우 북마크 기반으로 추천
        if (getRecommendRecipeByLog.size() < 3) {
            List<RecommendRecipeResDTO> searchRecipes = convertSearchResults(getRecommendRecipeByLog);
            List<RecommendRecipeResDTO> bookmarkRecipes = getRecommendRecipeByBookmark();

            return mergeBookmarkAndLog(searchRecipes, bookmarkRecipes, 3);
        }
        return convertSearchResults(getRecommendRecipeByLog);
    }

    //    북마크와 검색 결과를 병합하여 중복 제거 후 3개 노출
    private List<RecommendRecipeResDTO> mergeBookmarkAndLog(
            List<RecommendRecipeResDTO> searchRecipes,
            List<RecommendRecipeResDTO> bookmarkRecipes, int limit) {

//        검색 기반 레시피 먼저 추가
        List<RecommendRecipeResDTO> mergedRecipes = searchRecipes.stream().collect(Collectors.toList());

//        중복되지 않은 북마크 기반 레시피
        bookmarkRecipes.stream()
                .filter(bookmark -> searchRecipes.stream()
                        .noneMatch(search -> search.getRecipeCode().equals(bookmark.getRecipeCode())))
                .forEach(mergedRecipes::add);

        return mergedRecipes.stream().limit(limit).collect(Collectors.toList());
    }

    //    검색 기록 기반 결과를 DTO로 변환
    private List<RecommendRecipeResDTO> convertSearchResults(List<Object[]> getRecommendRecipeByLog) {
        return getRecommendRecipeByLog.stream().map(result -> {
            Long recipeCode = (Long) result[0];
            String recipeName = (String) result[1];
            Long count = (Long) result[2];

            String mainImgUrl = getMainImgUrl(recipeCode);

            return new RecommendRecipeResDTO(recipeCode, recipeName, mainImgUrl, count);
        }).collect(Collectors.toList());
    }

    //    비로그인 시
    @Transactional(readOnly = true)
    public List<RecommendRecipeResDTO> getRecommendRecipeByBookmark() {
        Pageable bookmarkPageable = PageRequest.of(0, 3);

        List<Object[]> getRecommendRecipeByBookmark = bookmarkRepository.findTopRecipe(bookmarkPageable);

        return getRecommendRecipeByBookmark.stream().map(result -> {
            Long recipeCode = (Long) result[0];
            String recipeName = (String) result[1];
            Long count = (Long) result[2];

//            첫번째 이미지 가져오기
            String mainImgUrl = getMainImgUrl(recipeCode);
            return new RecommendRecipeResDTO(recipeCode, recipeName, mainImgUrl, count);
        }).collect(Collectors.toList());
    }

    //    첫번째 이미지 Url 가져오기
    private String getMainImgUrl(Long recipeCode) {
        List<Image> images = imageService.getImagesByRecipe(recipeCode);
        return images != null && !images.isEmpty() ? images.get(0).getImgS3url() : null;
    }
}

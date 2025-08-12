package com.be90z.domain.recommend.controller;

import com.be90z.domain.recommend.dto.RecommendRecipeResDTO;
import com.be90z.domain.recommend.service.RecommendService;
import com.be90z.domain.user.entity.User;
import com.be90z.global.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "recommend", description = "인기 레시피 추천")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipe")
public class RecommendController {
    private final RecommendService recommendService;
    private final AuthUtil authUtil;

    @GetMapping("/recommend")
    @Operation(summary = "인기 레시피 추천",
            description = "비로그인: 북마크 수가 많은 인기 레시피 3개를 추천합니다, 로그인: 검색 기록 기반 유사 레시피 3개를 추천합니다.")
    public ResponseEntity<List<RecommendRecipeResDTO>> getRecommendRecipe(
            @RequestHeader(value = "Authorization", required = false) String token) {

        //        회원 권한 체크
        User user = authUtil.getUserFromToken(token);

        List<RecommendRecipeResDTO> recommendRecipeRes;

        if (user != null) {
//            로그인 시 유저 검색 기록 기반 레시피 추천
            recommendRecipeRes = recommendService.getRecommendRecipeByLog(user.getUserId());
        } else {
//            비로그인 시 북마크 기반 레시피 추천
            recommendRecipeRes = recommendService.getRecommendRecipeByBookmark();
        }
        return ResponseEntity.ok(recommendRecipeRes);
    }
}

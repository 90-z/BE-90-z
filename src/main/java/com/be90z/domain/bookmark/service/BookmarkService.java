package com.be90z.domain.bookmark.service;

import com.be90z.domain.bookmark.dto.BookmarkResDTO;
import com.be90z.domain.bookmark.entity.Bookmark;
import com.be90z.domain.bookmark.repository.BookmarkRepository;
import com.be90z.domain.recipe.entity.Image;
import com.be90z.domain.recipe.entity.Recipe;
import com.be90z.domain.recipe.repository.RecipeRepository;
import com.be90z.domain.recipe.service.ImageService;
import com.be90z.domain.recipe.service.RecipeService;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final ImageService imageService;

    //    북마크 등록
    @Transactional
    public void createBookmark(Long userId, Long recipeCode) {
        if (bookmarkRepository.existsByUserIdAndRecipeCode(userId, recipeCode)) {
            throw new IllegalArgumentException("이미 북마크된 레시피입니다.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Recipe recipe = recipeRepository.findById(recipeCode).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 레시피입니다."));

        Bookmark bookmark = new Bookmark(user, recipe);
        bookmarkRepository.save(bookmark);
    }

    //    북마크 해제
    @Transactional
    public void deleteBookmark(Long userId, Long recipeCode) {
        if (!bookmarkRepository.existsByUserIdAndRecipeCode(userId, recipeCode)) {
            return;
        }
        bookmarkRepository.deleteByUserIdAndRecipeCode(userId, recipeCode);
    }

    //    내 북마크 조회
    @Transactional(readOnly = true)
    public List<BookmarkResDTO> getMyBookmarks(Long userId) {
        List<Bookmark> bookmarks = bookmarkRepository.findByUserIdWithRecipe(userId);
        return bookmarks.stream().map(this::convertToBookmarkResDTO)
                .collect(Collectors.toList());
    }

    //    북마크 조회 시 북마크 DTO로 변환하는 메서드
    private BookmarkResDTO convertToBookmarkResDTO(Bookmark bookmark) {
        Recipe recipe = bookmark.getRecipe();
        BookmarkResDTO bookmarkResDTO = new BookmarkResDTO();

        bookmarkResDTO.setRecipeCode(recipe.getRecipeCode());
        bookmarkResDTO.setRecipeName(recipe.getRecipeName());
        bookmarkResDTO.setBookmarkDate(bookmark.getCreatedAt());

//        첫 번째 이미지 url 가져오기
        List<Image> images = imageService.getImagesByRecipe(recipe.getRecipeCode());
        if (images != null && !images.isEmpty()) {
            bookmarkResDTO.setMainImgUrl(images.get(0).getImgS3url());
        } else {
            bookmarkResDTO.setMainImgUrl(null);
        }
        return bookmarkResDTO;
    }
}

package com.be90z.domain.bookmark.controller;

import com.be90z.domain.bookmark.dto.BookmarkResDTO;
import com.be90z.domain.bookmark.service.BookmarkService;
import com.be90z.domain.recipe.dto.RecipeResDTO;
import com.be90z.domain.user.dto.response.AuthErrorResDTO;
import com.be90z.domain.user.entity.User;
import com.be90z.global.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@Tag(name = "bookmark", description = "북마크 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmark")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final AuthUtil authUtil;

    @PostMapping("/{recipeCode}")
    @Operation(summary = "북마크 저장", description = "레시피를 북마크로 저장합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> createBookmark(
            @Parameter(description = "북마크 지정할 레시피 코드", required = true)
            @PathVariable Long recipeCode,
            @RequestHeader(value = "Authorization", required = false) String token
    ) throws IOException {

        log.info("받은 토큰: {}", token);


        User user = authUtil.getUserFromToken(token);

        log.info("user 토큰: {}", token);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthErrorResDTO.unauthorized());
        }

        try {
            bookmarkService.createBookmark(user.getUserId(), recipeCode);
            return ResponseEntity.ok("북마크가 성공적으로 저장되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{recipeCode}")
    @Operation(summary = "북마크 해제", description = "레시피의 북마크를 해제합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deleteBookmark(
            @PathVariable Long recipeCode,
            @RequestHeader(value = "Authorization", required = false) String token) throws IOException {
        User user = authUtil.getUserFromToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthErrorResDTO.unauthorized());
        }
        try {
            bookmarkService.deleteBookmark(user.getUserId(), recipeCode);
            return ResponseEntity.ok("북마크가 해제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "내 북마크 레시피 조회", description = "현재 사용자의 북마크한 레시피를 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<BookmarkResDTO>> getAllBookmarks(
        @RequestHeader(value = "Authorization", required = false) String token) throws IOException {
        User user = authUtil.getUserFromToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<BookmarkResDTO> bookmarks = bookmarkService.getMyBookmarks(user.getUserId());
        return ResponseEntity.ok(bookmarks);
    }

}

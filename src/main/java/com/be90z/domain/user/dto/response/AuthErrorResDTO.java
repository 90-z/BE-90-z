package com.be90z.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 권한 체크 응답 DTO
@Getter
@AllArgsConstructor
public class AuthErrorResDTO {
    private String error;
    private String message;
    private int status;

    public static AuthErrorResDTO unauthorized() {
        return new AuthErrorResDTO("UNAUTHORIZED", "로그인이 필요합니다.", 401);
    }

    public static AuthErrorResDTO forbidden() {
        return new AuthErrorResDTO("FORBIDDEN", "권한이 없습니다.", 403);
    }

    public static AuthErrorResDTO notOwner() {
        return new AuthErrorResDTO("FORBIDDEN", "작성자만 수정/삭제할 수 있습니다.", 403);
    }
}

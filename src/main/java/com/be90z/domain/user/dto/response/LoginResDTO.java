package com.be90z.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

// 로그인 성공 시 클라이언트에게 보내는 응답 데이터

@Getter
@Builder
public class LoginResDTO {

    private String token;
    private Long userId;
    private String nickname;
    private String email;
    private String message;

//    로그인 성공 응답
    public static LoginResDTO success(String token, com.be90z.domain.user.entity.User user) {
        return LoginResDTO.builder()
                .token(token)
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .message("로그인 성공")
                .build();
    }

//    로그인 실패 응답
    public static LoginResDTO failure(String errorMessage) {
        return LoginResDTO.builder()
                .message(errorMessage)
                .build();
    }
}

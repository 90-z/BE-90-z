package com.be90z.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMypageResDTO {
    private final String userName;
    private final String email;
    
    public static UserMypageResDTO from(String userName, String email) {
        return UserMypageResDTO.builder()
                .userName(userName)
                .email(email)
                .build();
    }
}
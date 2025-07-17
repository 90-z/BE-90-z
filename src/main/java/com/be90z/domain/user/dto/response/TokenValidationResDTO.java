package com.be90z.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenValidationResDTO {
//    토큰 유효성 여부
    private boolean valid;
//    검증 결과 메세지
    private String message;
    public TokenValidationResDTO success() {
        return new TokenValidationResDTO(valid, message);
    }

    public TokenValidationResDTO fail(String message) {
        return new TokenValidationResDTO(false, message);
    }
}

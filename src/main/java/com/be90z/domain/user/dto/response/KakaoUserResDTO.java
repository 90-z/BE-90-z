package com.be90z.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoUserResDTO {
    private String id;
    private String nickname;
    private String email;
}

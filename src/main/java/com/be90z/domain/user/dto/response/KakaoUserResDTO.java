package com.be90z.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoUserResDTO {
    private String id;
    private String nickname;
    private String email;
    private String gender;
    private String birthday;

//    카카오 성별 정보 enum 변환
    public String getConvertedGender() {
        if ("female".equals(this.gender)) {
            return "WOMAN";
        } else if ("male".equals(this.gender)) {
            return "MAN";
        }
        return "MAN";
    }

//    생일 정보 출생년도만 추출
    public Integer getBirthYear() {
        if (birthday == null || birthday.isEmpty()) {
            return 2000; // 기본값: 정보가 없으면 2000년으로 설정
        }

        try {
            // 첫 4글자를 숫자로 변환 (년도 부분)
            return Integer.parseInt(birthday.substring(0, 4));
        } catch (Exception e) {
            // 변환에 실패하면 기본값 반환
            return 2000;
        }
    }
}

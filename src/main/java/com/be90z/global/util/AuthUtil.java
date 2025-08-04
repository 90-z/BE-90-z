package com.be90z.global.util;

import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

//1. JWT 에서 사용자 정보 추출하는 유틸리티 클래스
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

//    JWT 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return null;
            }
//            "Bearer" 접두사 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            if (!jwtUtil.validateToken(token)) {
                return null;
            }
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            log.error("토큰에서 사용자 ID 추출 실패: {}", e.getMessage());
            return null;
        }
    }

//    JWT 토큰에서 사용자 엔티티 조회
    public User getUserFromToken(String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }

//    토큰 유효성 검사
    public boolean isValidToken(String token) {
        return getUserIdFromToken(token) != null;
    }
}

package com.be90z.global.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key secretKey;
    private final Long expiration = 86400000L; //24시간

    public JwtUtil(@Value("${jwt.secret}") String secretString) {
        // 문자열을 암호화 키로 변환
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes());
    }

//    jwt 토큰 생성
    public String generateToken(Long userId) {
        Date now = new Date();  // 현재 시간
        Date expiryDate = new Date(now.getTime() + expiration);  // 만료 시간 = 현재시간 + 24시간

        return Jwts.builder()
                .setSubject(String.valueOf(userId))          // 토큰의 주인 (사용자 ID)
                .setIssuedAt(now)                           // 토큰 발급 시간
                .setExpiration(expiryDate)                  // 토큰 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256)  // 서명 (비밀키 + 알고리즘)
                .compact();                                 // 최종 토큰 문자열로 변환
    }

//    jwt 토큰에서 사용자 id 추출
    public Long getUserIdFromToken(String token) {
        String userIdString = Jwts.parserBuilder()
                .setSigningKey(secretKey)        // 서명 검증용 키 설정
                .build()
                .parseClaimsJws(token)          // 토큰 파싱 (서명도 함께 검증)
                .getBody()                      // payload 부분 가져오기
                .getSubject();                  // Subject(사용자 ID) 가져오기

        return Long.parseLong(userIdString);    // 문자열을 숫자로 변환
    }

//   jwt 토큰 유효 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);  // 파싱 시도 (실패하면 예외 발생)

            return true;  // 파싱 성공 = 유효한 토큰

        } catch (Exception e) {
            // 토큰이 만료되었거나, 서명이 틀리거나, 형식이 잘못된 경우
            System.out.println("토큰 검증 실패: " + e.getMessage());
            return false;
        }
    }
}

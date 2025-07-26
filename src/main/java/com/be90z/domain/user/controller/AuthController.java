//package com.be90z.domain.user.controller;
//
//import com.be90z.domain.user.dto.response.KakaoLoginUrlResDTO;
//import com.be90z.domain.user.dto.response.LoginResDTO;
//import com.be90z.domain.user.dto.response.TokenValidationResDTO;
//import com.be90z.domain.user.service.AuthUserService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Slf4j
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/auth")
//@Tag(name = "Authentication", description = "인증 관련 API")
//public class AuthController {
//
//    private final AuthUserService authUserService;
//
//
//    @GetMapping("/kakao/login-url")
//    @Operation(summary = "로그인 페이지")
//    public ResponseEntity<?> getKakaoLoginUrl() {
//        try {
//            log.info("카카오 로그인 URL 요청");
//
//            // authUserService에서 카카오 로그인 URL 생성
//            String loginUrl = authUserService.getKakaoLoginUrl();
//
//            log.info("카카오 로그인 URL 생성 완료: {}", loginUrl);
//
//            // JSON 형태로 응답 (프론트엔드에서 사용하기 쉽게)
//            return ResponseEntity.ok().body(new KakaoLoginUrlResDTO(loginUrl));
//
//        } catch (Exception e) {
//            log.error("카카오 로그인 URL 생성 실패: {}", e.getMessage());
//            return ResponseEntity.badRequest().body("로그인 URL 생성에 실패했습니다.");
//        }
//    }
//
//    @GetMapping("/kakao/callback")
//    @Operation(summary = "카카오 로그인 리다이렉트")
//    public ResponseEntity<LoginResDTO> kakaoCallback(@RequestParam String code) {
//        try {
//            log.info("카카오 콜백 처리 시작. 인증 코드: {}", code);
//
//            // AuthService에서 전체 로그인 처리
//            LoginResDTO loginResDTO = authUserService.processKakaoLogin(code);
//
//            // 로그인 성공 여부 확인
//            if (loginResDTO.getToken() != null) {
//                log.info("카카오 로그인 성공. 사용자: {}", loginResDTO.getNickname());
//                return ResponseEntity.ok(loginResDTO);
//            } else {
//                log.error("카카오 로그인 실패: {}", loginResDTO.getMessage());
//                return ResponseEntity.badRequest().body(loginResDTO);
//            }
//
//        } catch (Exception e) {
//            log.error("카카오 콜백 처리 중 오류: {}", e.getMessage());
//
//            // 실패 응답 생성
//            LoginResDTO errorResponse = LoginResDTO.failure("로그인 처리 중 오류가 발생했습니다.");
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//
//    //    JWT 토큰 검증 API - 선택적
//    @GetMapping("/validate")
//    public ResponseEntity<?> validateToken(@RequestParam String token) {
//        try {
//            log.info("토큰 검증 요청");
//
//            boolean isValid = authUserService.validateToken(token);
//
//            if (isValid) {
//                return ResponseEntity.ok().body(new TokenValidationResDTO(true, "유효한 토큰입니다."));
//            } else {
//                return ResponseEntity.badRequest().body(new TokenValidationResDTO(false, "유효하지 않은 토큰입니다."));
//            }
//
//        } catch (Exception e) {
//            log.error("토큰 검증 실패: {}", e.getMessage());
//            return ResponseEntity.badRequest().body(new TokenValidationResDTO(false, "토큰 검증 중 오류가 발생했습니다."));
//        }
//    }
//}

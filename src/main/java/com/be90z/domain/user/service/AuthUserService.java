package com.be90z.domain.user.service;

import com.be90z.domain.user.dto.response.KakaoUserResDTO;
import com.be90z.domain.user.dto.response.LoginResDTO;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.entity.UserAuthority;
import com.be90z.domain.user.repository.UserRepository;
import com.be90z.global.config.RestTemplateConfig;
import com.be90z.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplateConfig restTemplateConfig;

    @Value("${KAKAO_REST_API_KEY}")
    private String KakaoRestApiKey;

    @Value("${KAKAO_CLIENT_SECRET}")
    private String KakaoClientSecret;

    @Value("${KAKAO_LOGIN_REDIRECT_URI}")
    private String KakaoLoginRedirectUri;

    //    카카오 로그인 url 생성
    public String getKakaoLoginUrl() {
        return "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + KakaoRestApiKey +
                "&redirect_uri=" + KakaoLoginRedirectUri +
                "&response_type=code";
    }

    //    카카오 인증 코드로 액세스 토큰 받기
    private String getKakaoAccessToken(String authCode) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디 데이터 (form 형태)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");  // OAuth2 승인 타입
        params.add("client_id", KakaoRestApiKey);          // 우리 앱 ID
        params.add("client_secret", KakaoClientSecret);   // 우리 앱 시크릿
        params.add("redirect_uri", KakaoLoginRedirectUri);     // 리다이렉트 URI
        params.add("code", authCode);                    // 받은 인증 코드

        // HTTP 요청 객체 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            // POST 요청 전송
            ResponseEntity<Map> response = restTemplateConfig.restTemplate().postForEntity(tokenUrl, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            log.info("카카오 토큰 응답: {}", responseBody);

            // 응답에서 액세스 토큰 추출
            return (String) responseBody.get("access_token");

        } catch (Exception e) {
            log.error("카카오 액세스 토큰 요청 실패: {}", e.getMessage());
            throw new RuntimeException("카카오 로그인 중 오류가 발생했습니다.");
        }
    }

    //    엑세스 토큰으로 카카오 사용자 정보 받기
    private KakaoUserResDTO getkakaoUserResDTO(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        // HTTP 헤더에 액세스 토큰 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);  // Authorization: Bearer <토큰>

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            // GET 요청으로 사용자 정보 받기
            ResponseEntity<Map> response = restTemplateConfig.restTemplate().exchange(
                    userInfoUrl, HttpMethod.GET, request, Map.class);

            Map<String, Object> responseBody = response.getBody();
            log.info("카카오 사용자 정보: {}", responseBody);

            // 응답 데이터를 kakaoUserResDTO 객체로 변환
            return parsekakaoUserResDTO(responseBody);

        } catch (Exception e) {
            log.error("카카오 사용자 정보 요청 실패: {}", e.getMessage());
            throw new RuntimeException("사용자 정보를 가져오는데 실패했습니다.");
        }
    }

    //    카카오 api 응답을 kakaoUserResDTO 객체로 변환
    private KakaoUserResDTO parsekakaoUserResDTO(Map<String, Object> responseData) {
        // 카카오 사용자 ID
        String id = String.valueOf(responseData.get("id"));

        // properties에서 닉네임 추출
        Map<String, Object> properties = (Map<String, Object>) responseData.get("properties");
        String nickname = properties != null ? (String) properties.get("nickname") : null;

        // kakao_account에서 개인정보 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) responseData.get("kakao_account");
        String email = null;

        if (kakaoAccount != null) {
            email = (String) kakaoAccount.get("email");
        }

        // kakaoUserResDTO 객체 생성 후 반환
        return KakaoUserResDTO.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .build();
    }

    //    메인 로그인 처리 메서드
    public LoginResDTO processKakaoLogin(String authCode) {
        try {
            log.info("카카오 로그인 처리 시작. 인증 코드: {}", authCode);

            // 1단계: 인증 코드로 액세스 토큰 받기
            String accessToken = getKakaoAccessToken(authCode);
            log.info("액세스 토큰 획득 성공");

            // 2단계: 액세스 토큰으로 사용자 정보 받기
            KakaoUserResDTO kakaoUserResDTO = getkakaoUserResDTO(accessToken);
            log.info("사용자 정보 획득 성공: {}", kakaoUserResDTO.getNickname());

            // 3단계: 사용자 정보를 DB에 저장 또는 업데이트
            User user = saveOrUpdateUser(kakaoUserResDTO);
            log.info("사용자 DB 저장 완료: {}", user.getUserId());

            // 4단계: JWT 토큰 생성
            String jwtToken = jwtUtil.generateToken(user.getUserId());
            log.info("JWT 토큰 생성 완료");

            // 5단계: 성공 응답 반환
            return LoginResDTO.success(jwtToken, user);

        } catch (Exception e) {
            log.error("카카오 로그인 처리 실패: {}", e.getMessage());
            return LoginResDTO.failure("로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    //    사용자 정보를 DB에 저장하거나 업데이트
    private User saveOrUpdateUser(KakaoUserResDTO kakaoUserResDTO) {
        String kakaoId = kakaoUserResDTO.getId();

        // 기존 사용자 검색
        return userRepository.findByProvider(kakaoId)
                .map(existingUser -> {
                    // 기존 사용자 정보 업데이트
//                    log.info("기존 사용자 정보 업데이트: {}", existingUser.getNickname());

                    // 필요시 정보 업데이트 로직 추가
                    // existingUser.updateNickname(kakaoUserResDTO.getNickname());
                    // existingUser.updateEmail(kakaoUserResDTO.getEmail());

                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // 신규 사용자 생성
                    log.info("신규 사용자 생성: {}", kakaoUserResDTO.getNickname());

                    User newUser = User.builder()
                            .provider(kakaoId)
                            .nickname(kakaoUserResDTO.getNickname())
                            .email(kakaoUserResDTO.getEmail())
                            .auth(UserAuthority.USER)  // 기본값
                            .createdAt(LocalDateTime.now())
                            .build();

                    return userRepository.save(newUser);
                });
    }

    //    토큰 유효성 검증 메서드
    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            log.error("토큰 검증 중 오류: {}", e.getMessage());
            return false;
        }
    }
}
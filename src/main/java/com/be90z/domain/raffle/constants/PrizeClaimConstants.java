package com.be90z.domain.raffle.constants;

import java.util.Arrays;
import java.util.List;

/**
 * 상품 수령 관련 상수 클래스
 */
public final class PrizeClaimConstants {
    
    private PrizeClaimConstants() {
        // 유틸리티 클래스 - 인스턴스화 방지
    }
    
    // 수령 방법 상수
    public static final String CLAIM_METHOD_GIFT_CARD = "GIFT_CARD";
    public static final String CLAIM_METHOD_DIRECT_TRANSFER = "DIRECT_TRANSFER";
    
    public static final List<String> VALID_CLAIM_METHODS = Arrays.asList(
        CLAIM_METHOD_GIFT_CARD,
        CLAIM_METHOD_DIRECT_TRANSFER
    );
    
    // URL 상수
    public static final String DOWNLOAD_URL_BASE = "/api/raffle/winners";
    
    // 토큰 관련 상수
    public static final String TOKEN_DELIMITER = "-";
    public static final int TOKEN_EXPIRY_HOURS = 24;
    
    // 응답 메시지 상수
    public static final String MSG_WINNER_NOT_FOUND = "당첨 정보를 찾을 수 없습니다";
    public static final String MSG_USER_NOT_FOUND = "사용자를 찾을 수 없습니다";
    public static final String MSG_USER_MISMATCH = "당첨자와 수령 요청자가 일치하지 않습니다";
    public static final String MSG_ALREADY_CLAIMED = "이미 수령한 상품입니다";
    public static final String MSG_INVALID_CLAIM_METHOD = "지원하지 않는 수령 방법입니다";
    public static final String MSG_INVALID_TOKEN = "잘못된 다운로드 토큰입니다";
    public static final String MSG_EXPIRED_TOKEN = "다운로드 토큰이 만료되었습니다";
    
    // 파일 관련 상수
    public static final String GIFT_CARD_FILE_PREFIX = "giftcard_";
    public static final String GIFT_CARD_FILE_EXTENSION = ".png";
    public static final String CONTENT_TYPE_PNG = "image/png";
}
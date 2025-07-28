package com.be90z.domain.raffle.exception;

import com.be90z.domain.raffle.constants.PrizeClaimConstants;

/**
 * 상품 수령 관련 에러 코드 enum
 */
public enum PrizeClaimErrorCode {
    
    WINNER_NOT_FOUND("PRIZE_001", PrizeClaimConstants.MSG_WINNER_NOT_FOUND),
    USER_NOT_FOUND("PRIZE_002", PrizeClaimConstants.MSG_USER_NOT_FOUND),
    USER_MISMATCH("PRIZE_003", PrizeClaimConstants.MSG_USER_MISMATCH),
    ALREADY_CLAIMED("PRIZE_004", PrizeClaimConstants.MSG_ALREADY_CLAIMED),
    INVALID_CLAIM_METHOD("PRIZE_005", PrizeClaimConstants.MSG_INVALID_CLAIM_METHOD),
    INVALID_TOKEN("PRIZE_006", PrizeClaimConstants.MSG_INVALID_TOKEN),
    EXPIRED_TOKEN("PRIZE_007", PrizeClaimConstants.MSG_EXPIRED_TOKEN),
    PROCESSING_ERROR("PRIZE_999", "상품 수령 처리 중 오류가 발생했습니다");
    
    private final String code;
    private final String message;
    
    PrizeClaimErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
package com.be90z.domain.raffle.exception;

/**
 * 상품 수령 관련 커스텀 예외 클래스
 */
public class PrizeClaimException extends RuntimeException {
    
    private final PrizeClaimErrorCode errorCode;
    
    public PrizeClaimException(PrizeClaimErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public PrizeClaimException(PrizeClaimErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + ": " + additionalMessage);
        this.errorCode = errorCode;
    }
    
    public PrizeClaimException(PrizeClaimErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    public PrizeClaimErrorCode getErrorCode() {
        return errorCode;
    }
}
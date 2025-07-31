package com.be90z.domain.raffle.dto.request;

import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Getter
@Builder
public class PrizeClaimReqDTO {
    
    @NotNull(message = "당첨 코드는 필수입니다")
    private final Long winnerCode;
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private final Long userId;
    
    @NotBlank(message = "수령 방법은 필수입니다")
    private final String claimMethod;
    
    @NotBlank(message = "수령자 정보는 필수입니다")
    private final String recipientInfo;
}
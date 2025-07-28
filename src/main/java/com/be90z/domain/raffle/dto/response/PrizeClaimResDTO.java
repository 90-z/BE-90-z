package com.be90z.domain.raffle.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PrizeClaimResDTO {
    
    private final Long winnerCode;
    private final boolean claimed;
    private final LocalDateTime claimDate;
    private final String prizeName;
    private final String giftCardDownloadUrl;
    
    public static PrizeClaimResDTO of(Long winnerCode, boolean claimed, LocalDateTime claimDate, 
                                     String prizeName, String giftCardDownloadUrl) {
        return PrizeClaimResDTO.builder()
            .winnerCode(winnerCode)
            .claimed(claimed)
            .claimDate(claimDate)
            .prizeName(prizeName)
            .giftCardDownloadUrl(giftCardDownloadUrl)
            .build();
    }
}
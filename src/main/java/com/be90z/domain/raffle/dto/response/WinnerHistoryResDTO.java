package com.be90z.domain.raffle.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WinnerHistoryResDTO {
    
    private final Long winnerCode;
    private final String userName;
    private final String prizeName;
    private final LocalDateTime winDate;
    private final boolean claimed;
    private final LocalDateTime claimDate;
    
    public static WinnerHistoryResDTO of(Long winnerCode, String userName, String prizeName, 
                                        LocalDateTime winDate, boolean claimed, LocalDateTime claimDate) {
        return WinnerHistoryResDTO.builder()
            .winnerCode(winnerCode)
            .userName(userName)
            .prizeName(prizeName)
            .winDate(winDate)
            .claimed(claimed)
            .claimDate(claimDate)
            .build();
    }
}
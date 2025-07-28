package com.be90z.domain.raffle.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RaffleStatusResDTO {
    
    private boolean isParticipating;
    private int entryCount;
    private LocalDateTime lastEntryDate;
    private LocalDateTime nextDrawDate;
    
    public static RaffleStatusResDTO of(boolean isParticipating, int entryCount, 
                                       LocalDateTime lastEntryDate, LocalDateTime nextDrawDate) {
        return RaffleStatusResDTO.builder()
            .isParticipating(isParticipating)
            .entryCount(entryCount)
            .lastEntryDate(lastEntryDate)
            .nextDrawDate(nextDrawDate)
            .build();
    }
}
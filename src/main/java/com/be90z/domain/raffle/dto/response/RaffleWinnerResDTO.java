package com.be90z.domain.raffle.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RaffleWinnerResDTO {
    private Long winnerCode;
    private String winnerPrize;
    private Long raffleCode;
    private String raffleName;
    private Long userId;
    private String userNickname;
    private LocalDateTime createdAt;
}
package com.be90z.domain.raffle.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RaffleListResDTO {
    private Long raffleCode;
    private String raffleName;
    private String rafflePrizeCont;
    private Integer raffleWinner;
    private LocalDateTime raffleDate;
    private LocalDateTime createdAt;
}
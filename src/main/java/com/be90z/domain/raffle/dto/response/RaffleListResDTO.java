package com.be90z.domain.raffle.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RaffleListResDTO {
    private Long raffleCode;
    private Long participateCode;
    private String raffleName;
    private LocalDateTime raffleDate;
    private LocalDateTime createdAt;
}
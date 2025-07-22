package com.be90z.domain.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "챌린지 현황 응답 DTO")
public class ChallengeStatusResDTO {
    
    @Schema(description = "참여 중인 미션 수", example = "3")
    @Builder.Default
    private Integer participatingMissions = 0;
    
    @Schema(description = "래플 참여 횟수", example = "5")
    @Builder.Default
    private Integer raffleParticipationCount = 0;
    
    @Schema(description = "전체 래플 참여자 수", example = "150")
    @Builder.Default
    private Integer totalRaffleParticipants = 0;
    
    @Schema(description = "래플 당첨 횟수", example = "1")
    @Builder.Default
    private Integer raffleWinCount = 0;
}
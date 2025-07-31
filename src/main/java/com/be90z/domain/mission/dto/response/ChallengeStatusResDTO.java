package com.be90z.domain.mission.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChallengeStatusResDTO {
    private Long userId;
    private String nickname;
    private Integer participatingMissions;
    private Integer completedMissions;
    private Integer raffleEntries;
    private List<MissionStatusDTO> missionList;
    private List<RaffleWinnerStatusDTO> winnerHistory;

    @Getter
    @Builder
    public static class MissionStatusDTO {
        private Long missionCode;
        private String missionName;
        private String participateStatus;
        private String progress; // "1/3 진행 중" 등의 형태
    }

    @Getter
    @Builder
    public static class RaffleWinnerStatusDTO {
        private String raffleName;
        private String winnerPrize;
        private String createdAt;
        private Boolean isUsed;
    }
}
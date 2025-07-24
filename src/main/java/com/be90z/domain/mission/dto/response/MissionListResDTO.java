package com.be90z.domain.mission.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MissionListResDTO {
    
    private final Long missionCode;
    private final String missionContent;
    private final Integer missionGoalCount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Integer currentParticipants;
    
    @Builder
    public MissionListResDTO(Long missionCode, String missionContent, Integer missionGoalCount,
                           LocalDateTime startDate, LocalDateTime endDate, Integer currentParticipants) {
        this.missionCode = missionCode;
        this.missionContent = missionContent;
        this.missionGoalCount = missionGoalCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentParticipants = currentParticipants != null ? currentParticipants : 0;
    }
}
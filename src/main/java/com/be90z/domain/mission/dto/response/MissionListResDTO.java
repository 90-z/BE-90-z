package com.be90z.domain.mission.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MissionListResDTO {
    
    private final Long missionCode;
    private final String missionName;
    private final String missionContent;
    private final String missionStatus;
    private final Integer missionGoalCount;
    private final Integer maxParticipants;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Integer currentParticipants;
    
    @Builder
    public MissionListResDTO(Long missionCode, String missionName, String missionContent, String missionStatus,
                           Integer missionGoalCount, Integer maxParticipants, LocalDateTime startDate, LocalDateTime endDate, Integer currentParticipants) {
        this.missionCode = missionCode;
        this.missionName = missionName;
        this.missionContent = missionContent;
        this.missionStatus = missionStatus;
        this.missionGoalCount = missionGoalCount;
        this.maxParticipants = maxParticipants;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentParticipants = currentParticipants != null ? currentParticipants : 0;
    }
}
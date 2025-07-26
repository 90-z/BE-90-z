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
    private final Integer missionMax;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Integer currentParticipants;
    
    @Builder
    public MissionListResDTO(Long missionCode, String missionName, String missionContent,
                           String missionStatus, Integer missionMax, LocalDateTime startDate,
                           LocalDateTime endDate, Integer currentParticipants) {
        this.missionCode = missionCode;
        this.missionName = missionName;
        this.missionContent = missionContent;
        this.missionStatus = missionStatus;
        this.missionMax = missionMax;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentParticipants = currentParticipants != null ? currentParticipants : 0;
    }
}
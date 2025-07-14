package com.be90z.domain.mission.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MissionJoinReqDTO {
    
    private final Long userId;
    private final Long missionCode;
    
    @Builder
    public MissionJoinReqDTO(Long userId, Long missionCode) {
        this.userId = userId;
        this.missionCode = missionCode;
    }
}
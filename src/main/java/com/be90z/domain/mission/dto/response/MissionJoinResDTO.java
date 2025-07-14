package com.be90z.domain.mission.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MissionJoinResDTO {
    
    private final Long participateCode;
    private final String message;
    private final String participateStatus;
    
    @Builder
    public MissionJoinResDTO(Long participateCode, String message, String participateStatus) {
        this.participateCode = participateCode;
        this.message = message != null ? message : "미션 참여가 완료되었습니다.";
        this.participateStatus = participateStatus != null ? participateStatus : "PART_BEFORE";
    }
}
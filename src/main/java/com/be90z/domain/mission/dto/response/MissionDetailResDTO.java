package com.be90z.domain.mission.dto.response;

import com.be90z.domain.mission.entity.MissionStatus;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Getter
@Schema(description = "미션 상세 응답 DTO")
public class MissionDetailResDTO {
    
    @Schema(description = "미션 코드", example = "1")
    private final Long missionCode;
    
    @Schema(description = "미션명", example = "물 마시기 미션")
    private final String missionName;
    
    @Schema(description = "미션 내용", example = "매일 물 2L 마시기")
    private final String missionContent;
    
    @Schema(description = "미션 목표 횟수 (mission_goal_count)", example = "1")
    private final Integer missionGoalCount;
    
    @Schema(description = "미션 시작일", example = "2025-07-26T00:00:00")
    private final LocalDateTime startDate;
    
    @Schema(description = "미션 종료일", example = "2025-08-01T23:59:59")
    private final LocalDateTime endDate;
    
    @Schema(description = "현재 참여자 수", example = "45")
    private final Integer currentParticipants;
    
    @Schema(description = "참여 여부", example = "true")
    private final Boolean isParticipating;
    
    @Schema(description = "참여 상태", example = "PART_BEFORE")
    private final String participationStatus;
    
    @Builder
    public MissionDetailResDTO(Long missionCode, String missionName, String missionContent, Integer missionGoalCount,
                             LocalDateTime startDate, LocalDateTime endDate, Integer currentParticipants,
                             Boolean isParticipating, String participationStatus) {
        this.missionCode = missionCode;
        this.missionName = missionName;
        this.missionContent = missionContent;
        this.missionGoalCount = missionGoalCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentParticipants = currentParticipants != null ? currentParticipants : 0;
        this.isParticipating = isParticipating != null ? isParticipating : false;
        this.participationStatus = participationStatus;
    }
}
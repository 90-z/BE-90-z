package com.be90z.domain.mission.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "미션 수정 요청 DTO")
public class MissionUpdateReqDTO {
    
    @Schema(description = "미션명", example = "새로운 건강 미션")
    private String missionName;
    
    @Schema(description = "미션 내용", example = "매일 물 3L 마시기")
    private String missionContent;
    
    @Schema(description = "미션 시작일", example = "2025-07-26T00:00:00")
    private LocalDateTime startDate;
    
    @Schema(description = "미션 종료일", example = "2025-08-01T23:59:59")
    private LocalDateTime endDate;
    
    @Min(value = 1, message = "최대 참가자 수는 1 이상이어야 합니다")
    @Schema(description = "최대 참가자 수 (mission_max)", example = "200")
    private Integer missionMax;
    
    @Builder
    public MissionUpdateReqDTO(String missionName, String missionContent, 
                               LocalDateTime startDate, LocalDateTime endDate, Integer missionMax) {
        this.missionName = missionName;
        this.missionContent = missionContent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.missionMax = missionMax;
    }
}
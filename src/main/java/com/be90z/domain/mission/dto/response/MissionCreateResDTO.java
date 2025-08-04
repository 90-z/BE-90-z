package com.be90z.domain.mission.dto.response;

import com.be90z.domain.mission.entity.MissionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "미션 생성 응답 DTO")
public class MissionCreateResDTO {
    
    @Schema(description = "생성된 미션 코드", example = "1")
    private Long missionCode;
    
    @Schema(description = "미션 내용", example = "매일 물 2L 마시기")
    private String missionContent;
    
    @Schema(description = "미션 목표 횟수 (mission_goal_count)", example = "1")
    private Integer missionGoalCount;
    
    @Schema(description = "미션 시작일", example = "2025-07-26T00:00:00")
    private LocalDateTime startDate;
    
    @Schema(description = "미션 종료일", example = "2025-08-01T23:59:59")
    private LocalDateTime endDate;
    
    @Schema(description = "생성 일시", example = "2025-07-26T14:30:00")
    private LocalDateTime createdAt;
    
    @Builder
    public MissionCreateResDTO(Long missionCode, String missionContent, Integer missionGoalCount,
                               LocalDateTime startDate, LocalDateTime endDate, LocalDateTime createdAt) {
        this.missionCode = missionCode;
        this.missionContent = missionContent;
        this.missionGoalCount = missionGoalCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }
}
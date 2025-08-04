package com.be90z.domain.mission.dto.request;

import com.be90z.domain.mission.entity.MissionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Schema(description = "미션 생성 요청 DTO")
public class MissionCreateReqDTO {
    
    @NotBlank(message = "미션 내용은 필수입니다")
    @Schema(description = "미션 내용", example = "매일 물 2L 마시기")
    private String missionContent;
    
    @Min(value = 1, message = "미션 목표 횟수는 1 이상이어야 합니다")
    @Schema(description = "미션 목표 횟수 (mission_goal_count)", example = "1")
    private Integer missionGoalCount = 1;
    
    @NotNull(message = "시작일은 필수입니다")
    @Schema(description = "미션 시작일", example = "2025-07-26T00:00:00")
    private LocalDateTime startDate;
    
    @NotNull(message = "종료일은 필수입니다")
    @Schema(description = "미션 종료일", example = "2025-08-01T23:59:59")
    private LocalDateTime endDate;
    
    @Builder
    public MissionCreateReqDTO(String missionContent, Integer missionGoalCount, 
                               LocalDateTime startDate, LocalDateTime endDate) {
        this.missionContent = missionContent;
        this.missionGoalCount = missionGoalCount != null ? missionGoalCount : 1;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
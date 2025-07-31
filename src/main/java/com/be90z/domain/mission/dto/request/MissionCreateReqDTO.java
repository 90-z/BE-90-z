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
    
    @NotBlank(message = "미션명은 필수입니다")
    @Schema(description = "미션명", example = "건강한 하루 보내기")
    private String missionName;
    
    @NotBlank(message = "미션 내용은 필수입니다")
    @Schema(description = "미션 내용", example = "매일 물 2L 마시기")
    private String missionContent;
    
    @Schema(description = "미션 상태", example = "ACTIVE")
    private MissionStatus missionStatus = MissionStatus.ACTIVE;
    
    @Min(value = 1, message = "최대 참가자 수는 1 이상이어야 합니다")
    @Schema(description = "최대 참가자 수 (mission_max)", example = "100")
    private Integer missionMax;
    
    @NotNull(message = "시작일은 필수입니다")
    @Schema(description = "미션 시작일", example = "2025-07-26T00:00:00")
    private LocalDateTime startDate;
    
    @NotNull(message = "종료일은 필수입니다")
    @Schema(description = "미션 종료일", example = "2025-08-01T23:59:59")
    private LocalDateTime endDate;
    
    @Builder
    public MissionCreateReqDTO(String missionName, String missionContent, MissionStatus missionStatus,
                               Integer missionMax, LocalDateTime startDate, LocalDateTime endDate) {
        this.missionName = missionName;
        this.missionContent = missionContent;
        this.missionStatus = missionStatus != null ? missionStatus : MissionStatus.ACTIVE;
        this.missionMax = missionMax;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
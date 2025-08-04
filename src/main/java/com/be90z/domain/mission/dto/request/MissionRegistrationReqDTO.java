package com.be90z.domain.mission.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@Schema(description = "챌린지 등록 요청 DTO")
public class MissionRegistrationReqDTO {
    
    @NotBlank(message = "미션명은 필수입니다")
    @Schema(description = "미션명", example = "매일 운동하기")
    private String missionName;
    
    @NotBlank(message = "미션 내용은 필수입니다")
    @Schema(description = "미션 내용", example = "매일 30분씩 운동하는 챌린지")
    private String missionContent;
    
    @Builder
    public MissionRegistrationReqDTO(String missionName, String missionContent) {
        this.missionName = missionName;
        this.missionContent = missionContent;
    }
}
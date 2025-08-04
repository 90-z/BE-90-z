package com.be90z.domain.mission.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@Schema(description = "미션 수정 요청 DTO (명세서 준수)")
public class MissionUpdateReqDTO {
    
    @NotBlank(message = "미션명은 필수입니다")
    @Schema(description = "미션명", example = "수정된 미션 제목")
    private String missionName;
    
    @NotBlank(message = "미션 내용은 필수입니다")
    @Schema(description = "미션 내용", example = "수정된 미션 내용")
    private String missionContent;
    
    @Builder
    public MissionUpdateReqDTO(String missionName, String missionContent) {
        this.missionName = missionName;
        this.missionContent = missionContent;
    }
}
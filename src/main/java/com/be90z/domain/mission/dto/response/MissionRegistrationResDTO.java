package com.be90z.domain.mission.dto.response;

import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "챌린지 등록 응답 DTO")
public class MissionRegistrationResDTO {
    
    @Schema(description = "등록 ID", example = "1")
    private Long registrationId;
    
    @Schema(description = "미션 코드", example = "100")
    private Long missionCode;
    
    @Schema(description = "미션명", example = "매일 운동하기")
    private String missionName;
    
    @Schema(description = "미션 내용", example = "매일 30분씩 운동하는 챌린지")
    private String missionContent;
    
    @Schema(description = "생성일시", example = "2025-07-31T10:00:00")
    private LocalDateTime createdAt;
}
package com.be90z.domain.mission.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Schema(description = "미션 댓글 등록 요청 DTO")
public class MissionReplyReqDTO {
    
    @NotBlank(message = "미션명은 필수입니다.")
    @Size(max = 100, message = "미션명은 100자 이하여야 합니다.")
    @Schema(description = "미션명", example = "테스트 미션", required = true)
    private String missionName;
    
    @NotBlank(message = "미션 내용은 필수입니다.")
    @Size(max = 500, message = "미션 내용은 500자 이하여야 합니다.")
    @Schema(description = "미션 내용", example = "테스트 미션 내용입니다.", required = true)
    private String missionContent;
}
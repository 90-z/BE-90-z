package com.be90z.domain.mission.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
@Schema(description = "미션 답글 요청 DTO")
public class MissionReplyReqDTO {
    
    @NotBlank(message = "답글 내용은 필수입니다")
    @Schema(description = "답글 내용", example = "이 미션에 참여하고 싶습니다!")
    private String replyContent;
    
    @NotNull(message = "사용자 ID는 필수입니다")
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    
    @Builder
    public MissionReplyReqDTO(String replyContent, Long userId) {
        this.replyContent = replyContent;
        this.userId = userId;
    }
}
package com.be90z.domain.mission.dto.response;

import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "미션 답글 응답 DTO")
public class MissionReplyResDTO {
    
    @Schema(description = "답글 ID", example = "1")
    private Long replyId;
    
    @Schema(description = "미션 코드", example = "100")
    private Long missionCode;
    
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    
    @Schema(description = "답글 내용", example = "이 미션에 참여하고 싶습니다!")
    private String replyContent;
    
    @Schema(description = "생성일시", example = "2025-07-31T10:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "사용자 닉네임", example = "테스트유저")
    private String userNickname;
}
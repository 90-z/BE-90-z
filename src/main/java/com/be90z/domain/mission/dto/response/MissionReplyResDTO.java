package com.be90z.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Schema(description = "미션 댓글 등록 응답 DTO")
public class MissionReplyResDTO {
    
    @Schema(description = "댓글 고유 코드", example = "1")
    private Long replyCode;
    
    @Schema(description = "미션 고유 코드", example = "100")
    private Long missionCode;
    
    @Schema(description = "미션명", example = "테스트 미션")
    private String missionName;
    
    @Schema(description = "미션 내용", example = "테스트 미션 내용입니다.")
    private String missionContent;
    
    @Schema(description = "생성 일시", example = "2024-01-01T12:00:00")
    private LocalDateTime createdAt;
    
    /**
     * 정적 팩토리 메서드
     */
    public static MissionReplyResDTO of(Long replyCode, Long missionCode, String missionName, 
                                       String missionContent, LocalDateTime createdAt) {
        return MissionReplyResDTO.builder()
                .replyCode(replyCode)
                .missionCode(missionCode)
                .missionName(missionName)
                .missionContent(missionContent)
                .createdAt(createdAt)
                .build();
    }
}
package com.be90z.domain.mission.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MissionReplyResDTO 테스트")
class MissionReplyResDTOTest {

    @Test
    @DisplayName("미션 답글 응답 DTO 생성 테스트")
    void createMissionReplyResDTO() {
        // given
        Long replyId = 1L;
        Long missionCode = 100L;
        Long userId = 1L;
        String replyContent = "미션에 참여하고 싶습니다!";
        LocalDateTime createdAt = LocalDateTime.now();
        String userNickname = "테스트유저";

        // when
        MissionReplyResDTO dto = MissionReplyResDTO.builder()
                .replyId(replyId)
                .missionCode(missionCode)
                .userId(userId)
                .replyContent(replyContent)
                .createdAt(createdAt)
                .userNickname(userNickname)
                .build();

        // then
        assertThat(dto.getReplyId()).isEqualTo(replyId);
        assertThat(dto.getMissionCode()).isEqualTo(missionCode);
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getReplyContent()).isEqualTo(replyContent);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getUserNickname()).isEqualTo(userNickname);
    }

    @Test
    @DisplayName("Builder 패턴으로 일부 필드만 설정하여 DTO 생성")
    void createMissionReplyResDTOWithPartialFields() {
        // given
        Long replyId = 1L;
        String replyContent = "답글 내용";

        // when
        MissionReplyResDTO dto = MissionReplyResDTO.builder()
                .replyId(replyId)
                .replyContent(replyContent)
                .build();

        // then
        assertThat(dto.getReplyId()).isEqualTo(replyId);
        assertThat(dto.getReplyContent()).isEqualTo(replyContent);
        assertThat(dto.getMissionCode()).isNull();
        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getCreatedAt()).isNull();
        assertThat(dto.getUserNickname()).isNull();
    }
}
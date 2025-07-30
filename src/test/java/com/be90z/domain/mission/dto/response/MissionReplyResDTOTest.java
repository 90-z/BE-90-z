package com.be90z.domain.mission.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MissionReplyResDTO 테스트")
class MissionReplyResDTOTest {

    @Test
    @DisplayName("미션 댓글 응답 DTO를 정상적으로 생성할 수 있다")
    void shouldCreateMissionReplyResDTO() {
        // given
        Long replyCode = 1L;
        Long missionCode = 100L;
        String missionName = "테스트 미션";
        String missionContent = "테스트 미션 내용입니다.";
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        // when
        MissionReplyResDTO dto = MissionReplyResDTO.builder()
                .replyCode(replyCode)
                .missionCode(missionCode)
                .missionName(missionName)
                .missionContent(missionContent)
                .createdAt(createdAt)
                .build();

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getReplyCode()).isEqualTo(replyCode);
        assertThat(dto.getMissionCode()).isEqualTo(missionCode);
        assertThat(dto.getMissionName()).isEqualTo(missionName);
        assertThat(dto.getMissionContent()).isEqualTo(missionContent);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("정적 팩토리 메서드로 DTO를 생성할 수 있다")
    void shouldCreateDTOWithStaticFactory() {
        // given
        Long replyCode = 2L;
        Long missionCode = 200L;
        String missionName = "정적 팩토리 미션";
        String missionContent = "정적 팩토리로 생성된 미션 내용";
        LocalDateTime createdAt = LocalDateTime.now();

        // when
        MissionReplyResDTO dto = MissionReplyResDTO.of(replyCode, missionCode, missionName, missionContent, createdAt);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getReplyCode()).isEqualTo(replyCode);
        assertThat(dto.getMissionCode()).isEqualTo(missionCode);
        assertThat(dto.getMissionName()).isEqualTo(missionName);
        assertThat(dto.getMissionContent()).isEqualTo(missionContent);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("Builder 패턴으로 부분적 필드만 설정할 수 있다")
    void shouldCreateDTOWithPartialFields() {
        // given
        Long replyCode = 3L;
        String missionName = "부분 필드 미션";

        // when
        MissionReplyResDTO dto = MissionReplyResDTO.builder()
                .replyCode(replyCode)
                .missionName(missionName)
                .build();

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getReplyCode()).isEqualTo(replyCode);
        assertThat(dto.getMissionName()).isEqualTo(missionName);
        assertThat(dto.getMissionCode()).isNull();
        assertThat(dto.getMissionContent()).isNull();
        assertThat(dto.getCreatedAt()).isNull();
    }

    @Test
    @DisplayName("동일한 값으로 생성된 DTO는 equals와 hashCode가 일치한다")
    void shouldHaveConsistentEqualsAndHashCode() {
        // given
        Long replyCode = 4L;
        Long missionCode = 400L;
        String missionName = "동일성 테스트 미션";
        String missionContent = "동일성 테스트 내용";
        LocalDateTime createdAt = LocalDateTime.of(2024, 2, 1, 10, 30, 0);

        // when
        MissionReplyResDTO dto1 = MissionReplyResDTO.builder()
                .replyCode(replyCode)
                .missionCode(missionCode)
                .missionName(missionName)
                .missionContent(missionContent)
                .createdAt(createdAt)
                .build();

        MissionReplyResDTO dto2 = MissionReplyResDTO.builder()
                .replyCode(replyCode)
                .missionCode(missionCode)
                .missionName(missionName)
                .missionContent(missionContent)
                .createdAt(createdAt)
                .build();

        // then
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("toString 메서드가 모든 필드를 포함한다")
    void shouldIncludeAllFieldsInToString() {
        // given
        MissionReplyResDTO dto = MissionReplyResDTO.builder()
                .replyCode(5L)
                .missionCode(500L)
                .missionName("toString 테스트")
                .missionContent("toString 내용")
                .createdAt(LocalDateTime.of(2024, 3, 1, 15, 45, 30))
                .build();

        // when
        String toString = dto.toString();

        // then
        assertThat(toString).contains("replyCode=5");
        assertThat(toString).contains("missionCode=500");
        assertThat(toString).contains("missionName=toString 테스트");
        assertThat(toString).contains("missionContent=toString 내용");
        assertThat(toString).contains("createdAt=2024-03-01T15:45:30");
    }
}
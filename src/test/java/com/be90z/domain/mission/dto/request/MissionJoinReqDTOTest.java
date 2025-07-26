package com.be90z.domain.mission.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MissionJoinReqDTO 테스트")
class MissionJoinReqDTOTest {

    @Test
    @DisplayName("빌더 패턴으로 DTO 생성 - 정상 케이스")
    void builder_ValidData_CreatesDTO() {
        // given
        Long userId = 1L;
        Long missionCode = 2L;

        // when
        MissionJoinReqDTO dto = MissionJoinReqDTO.builder()
                .userId(userId)
                .missionCode(missionCode)
                .build();

        // then
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getMissionCode()).isEqualTo(missionCode);
    }

    @Test
    @DisplayName("필수 필드 검증")
    void builder_RequiredFields_NotNull() {
        // when
        MissionJoinReqDTO dto = MissionJoinReqDTO.builder()
                .userId(1L)
                .missionCode(2L)
                .build();

        // then
        assertThat(dto.getUserId()).isNotNull();
        assertThat(dto.getMissionCode()).isNotNull();
    }
}
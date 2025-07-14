package com.be90z.domain.mission.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MissionJoinResDTO 테스트")
class MissionJoinResDTOTest {

    @Test
    @DisplayName("빌더 패턴으로 DTO 생성 - 정상 케이스")
    void builder_ValidData_CreatesDTO() {
        // given
        Long participateCode = 123L;
        String message = "미션 참여가 완료되었습니다.";
        String participateStatus = "PART_BEFORE";

        // when
        MissionJoinResDTO dto = MissionJoinResDTO.builder()
                .participateCode(participateCode)
                .message(message)
                .participateStatus(participateStatus)
                .build();

        // then
        assertThat(dto.getParticipateCode()).isEqualTo(participateCode);
        assertThat(dto.getMessage()).isEqualTo(message);
        assertThat(dto.getParticipateStatus()).isEqualTo(participateStatus);
    }

    @Test
    @DisplayName("기본값 검증 - message와 participateStatus 기본값")
    void builder_DefaultValues_SetsCorrectDefaults() {
        // when
        MissionJoinResDTO dto = MissionJoinResDTO.builder()
                .participateCode(123L)
                .build();

        // then
        assertThat(dto.getMessage()).isEqualTo("미션 참여가 완료되었습니다.");
        assertThat(dto.getParticipateStatus()).isEqualTo("PART_BEFORE");
    }
}
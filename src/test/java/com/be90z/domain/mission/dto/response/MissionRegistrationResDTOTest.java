package com.be90z.domain.mission.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MissionRegistrationResDTO 테스트")
class MissionRegistrationResDTOTest {

    @Test
    @DisplayName("정상적인 챌린지 등록 응답 DTO 생성 성공")
    void createValidMissionRegistrationResDTO_Success() {
        // given
        LocalDateTime now = LocalDateTime.now();
        
        // when
        MissionRegistrationResDTO dto = MissionRegistrationResDTO.builder()
                .registrationId(1L)
                .missionCode(100L)
                .missionName("새로운 챌린지")
                .missionContent("매일 물 2L 마시기 챌린지")
                .createdAt(now)
                .build();

        // then
        assertThat(dto.getRegistrationId()).isEqualTo(1L);
        assertThat(dto.getMissionCode()).isEqualTo(100L);
        assertThat(dto.getMissionName()).isEqualTo("새로운 챌린지");
        assertThat(dto.getMissionContent()).isEqualTo("매일 물 2L 마시기 챌린지");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Builder 패턴으로 필드별 설정 성공")
    void buildMissionRegistrationResDTO_WithBuilderPattern_Success() {
        // given & when
        MissionRegistrationResDTO dto = MissionRegistrationResDTO.builder()
                .registrationId(2L)
                .missionCode(200L)
                .missionName("운동 챌린지")
                .missionContent("매일 30분 운동하기")
                .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();

        // then
        assertThat(dto.getRegistrationId()).isEqualTo(2L);
        assertThat(dto.getMissionCode()).isEqualTo(200L);
        assertThat(dto.getMissionName()).isEqualTo("운동 챌린지");
        assertThat(dto.getMissionContent()).isEqualTo("매일 30분 운동하기");
        assertThat(dto.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0));
    }
}
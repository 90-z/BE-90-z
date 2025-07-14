package com.be90z.domain.mission.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MissionListResDTO 테스트")
class MissionListResDTOTest {

    @Test
    @DisplayName("빌더 패턴으로 DTO 생성 - 정상 케이스")
    void builder_ValidData_CreatesDTO() {
        // given
        Long missionCode = 1L;
        String missionName = "매일 물 8잔 마시기";
        String missionContent = "하루에 물 8잔을 마시고 인증샷을 올려주세요";
        String missionStatus = "ACTIVE";
        Integer missionMax = 100;
        LocalDateTime startDate = LocalDateTime.of(2025, 7, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 7, 31, 23, 59);
        Integer currentParticipants = 45;

        // when
        MissionListResDTO dto = MissionListResDTO.builder()
                .missionCode(missionCode)
                .missionName(missionName)
                .missionContent(missionContent)
                .missionStatus(missionStatus)
                .missionMax(missionMax)
                .startDate(startDate)
                .endDate(endDate)
                .currentParticipants(currentParticipants)
                .build();

        // then
        assertThat(dto.getMissionCode()).isEqualTo(missionCode);
        assertThat(dto.getMissionName()).isEqualTo(missionName);
        assertThat(dto.getMissionContent()).isEqualTo(missionContent);
        assertThat(dto.getMissionStatus()).isEqualTo(missionStatus);
        assertThat(dto.getMissionMax()).isEqualTo(missionMax);
        assertThat(dto.getStartDate()).isEqualTo(startDate);
        assertThat(dto.getEndDate()).isEqualTo(endDate);
        assertThat(dto.getCurrentParticipants()).isEqualTo(currentParticipants);
    }

    @Test
    @DisplayName("기본값 검증 - currentParticipants는 0")
    void builder_DefaultValues_SetsCorrectDefaults() {
        // when
        MissionListResDTO dto = MissionListResDTO.builder()
                .missionCode(1L)
                .missionName("테스트 미션")
                .missionContent("테스트 내용")
                .missionStatus("ACTIVE")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build();

        // then
        assertThat(dto.getCurrentParticipants()).isEqualTo(0);
    }
}
package com.be90z.domain.mission.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Mission 엔티티 테스트")
class MissionTest {

    @Test
    @DisplayName("Mission 엔티티 생성 - 성공")
    void createMission_Success() {
        // given
        String missionContent = "하루 30분 이상 운동하기";
        Integer missionGoalCount = 100;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);

        // when
        Mission mission = Mission.builder()
                .missionName("테스트 미션명")
                .missionContent(missionContent)
                .missionGoalCount(missionGoalCount)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // then
        assertThat(mission.getMissionContent()).isEqualTo(missionContent);
        assertThat(mission.getMissionGoalCount()).isEqualTo(missionGoalCount);
        assertThat(mission.getStartDate()).isEqualTo(startDate);
        assertThat(mission.getEndDate()).isEqualTo(endDate);
        assertThat(mission.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Mission 생성 - missionContent null일 때 예외 발생")
    void createMission_MissionContentNull_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Mission.builder()
                .missionName("테스트 미션명")
                .missionContent(null)
                .missionGoalCount(100)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mission content cannot be null");
    }

    @Test
    @DisplayName("Mission 생성 - 기본값 테스트")
    void createMission_DefaultValues() {
        // given
        String missionContent = "기본값 테스트 미션";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);

        // when
        Mission mission = Mission.builder()
                .missionName("기본값 테스트 미션명")
                .missionContent(missionContent)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // then
        assertThat(mission.getMissionGoalCount()).isEqualTo(1); // 기본값
        assertThat(mission.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Mission 업데이트 테스트")
    void updateMission() {
        // given
        Mission mission = Mission.builder()
                .missionName("원래 미션명")
                .missionContent("원래 내용")
                .missionGoalCount(50)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build();
        
        String newMissionName = "수정된 미션명";
        String newMissionContent = "수정된 내용";
        
        // when - 새로운 시그니처 사용: updateMission(missionName, missionContent)
        mission.updateMission(newMissionName, newMissionContent);
        
        // then
        assertThat(mission.getMissionName()).isEqualTo(newMissionName);
        assertThat(mission.getMissionContent()).isEqualTo(newMissionContent);
    }
}
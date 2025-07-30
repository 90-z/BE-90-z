package com.be90z.domain.mission.entity;

import com.be90z.domain.challenge.entity.Challenge;
import com.be90z.domain.challenge.entity.ChallengeStatus;
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
        Challenge challenge = Challenge.builder()
                .challengeName("다이어트 챌린지")
                .challengeDescription("건강한 다이어트를 위한 챌린지")
                .challengeStatus(ChallengeStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();

        String missionName = "일일 운동하기";
        String missionContent = "하루 30분 이상 운동하기";
        MissionStatus missionStatus = MissionStatus.ACTIVE;
        Integer maxParticipants = 100;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);

        // when
        Mission mission = Mission.builder()
                .missionName(missionName)
                .missionContent(missionContent)
                .missionStatus(missionStatus)
                .challenge(challenge)
                .maxParticipants(maxParticipants)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // then
        assertThat(mission.getMissionName()).isEqualTo(missionName);
        assertThat(mission.getMissionContent()).isEqualTo(missionContent);
        assertThat(mission.getMissionStatus()).isEqualTo(missionStatus);
        assertThat(mission.getChallenge()).isEqualTo(challenge);
        assertThat(mission.getMaxParticipants()).isEqualTo(maxParticipants);
        assertThat(mission.getStartDate()).isEqualTo(startDate);
        assertThat(mission.getEndDate()).isEqualTo(endDate);
        assertThat(mission.getCreatedAt()).isNotNull();
        assertThat(mission.getMissionGoalCount()).isEqualTo(1); // 기본값
    }

    @Test
    @DisplayName("Mission 생성 - missionName null일 때 예외 발생")
    void createMission_MissionNameNull_ThrowsException() {
        // given
        Challenge challenge = Challenge.builder()
                .challengeName("테스트 챌린지")
                .challengeDescription("테스트 설명")
                .challengeStatus(ChallengeStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();

        // when & then
        assertThatThrownBy(() -> Mission.builder()
                .missionName(null)
                .missionContent("테스트 내용")
                .challenge(challenge)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mission name cannot be null");
    }

    @Test
    @DisplayName("Mission 생성 - missionContent null일 때 예외 발생")
    void createMission_MissionContentNull_ThrowsException() {
        // given
        Challenge challenge = Challenge.builder()
                .challengeName("테스트 챌린지")
                .challengeDescription("테스트 설명")
                .challengeStatus(ChallengeStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();

        // when & then
        assertThatThrownBy(() -> Mission.builder()
                .missionName("테스트 미션")
                .missionContent(null)
                .challenge(challenge)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mission content cannot be null");
    }

    @Test
    @DisplayName("Mission 업데이트 테스트")
    void updateMission() {
        // given
        Challenge challenge = Challenge.builder()
                .challengeName("테스트 챌린지")
                .challengeDescription("테스트 설명")
                .challengeStatus(ChallengeStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();

        Mission mission = Mission.builder()
                .missionName("원래 미션")
                .missionContent("원래 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .challenge(challenge)
                .maxParticipants(50)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build();
        
        String newMissionName = "수정된 미션";
        String newMissionContent = "수정된 내용";
        Integer newMaxParticipants = 100;
        
        // when
        mission.updateMission(newMissionName, newMissionContent, null, null, null, newMaxParticipants);
        
        // then
        assertThat(mission.getMissionName()).isEqualTo(newMissionName);
        assertThat(mission.getMissionContent()).isEqualTo(newMissionContent);
        assertThat(mission.getMaxParticipants()).isEqualTo(newMaxParticipants);
    }
}
package com.be90z.domain.mission.entity;

import com.be90z.domain.challenge.entity.Challenge;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

@DisplayName("Mission 엔티티 테스트")
class MissionTest {

    @Test
    @DisplayName("Mission 엔티티 생성 테스트")
    void createMission() {
        // given
        Challenge challenge = Challenge.builder()
                .challengeName("다이어트 챌린지")
                .challengeDescription("건강한 다이어트를 위한 챌린지")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();
        
        Long missionCode = 1L;
        String missionName = "일일 운동하기";
        String missionContent = "하루 30분 이상 운동하기";
        MissionStatus missionStatus = MissionStatus.ACTIVE;
        Integer missionMax = 100;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(30);
        LocalDateTime createdAt = LocalDateTime.now();

        // when
        Mission mission = Mission.builder()
                .missionCode(missionCode)
                .challenge(challenge)
                .missionName(missionName)
                .missionContent(missionContent)
                .missionStatus(missionStatus)
                .missionMax(missionMax)
                .startDate(startDate)
                .endDate(endDate)
                .createdAt(createdAt)
                .build();

        // then
        assertThat(mission.getMissionCode()).isEqualTo(missionCode);
        assertThat(mission.getChallenge()).isEqualTo(challenge);
        assertThat(mission.getMissionName()).isEqualTo(missionName);
        assertThat(mission.getMissionContent()).isEqualTo(missionContent);
        assertThat(mission.getMissionStatus()).isEqualTo(missionStatus);
        assertThat(mission.getMissionMax()).isEqualTo(missionMax);
        assertThat(mission.getStartDate()).isEqualTo(startDate);
        assertThat(mission.getEndDate()).isEqualTo(endDate);
        assertThat(mission.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("Mission 필수 필드 검증")
    void validateRequiredFields() {
        // given
        Challenge challenge = Challenge.builder()
                .challengeName("테스트 챌린지")
                .challengeDescription("테스트 설명")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();
        
        // when & then
        assertThatThrownBy(() -> {
            Mission.builder()
                    .challenge(challenge)
                    .missionContent("테스트 미션")
                    .missionStatus(MissionStatus.ACTIVE)
                    // missionName 누락
                    .build();
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Mission name cannot be null");
    }

    @Test
    @DisplayName("Mission과 Challenge 관계 테스트")
    void missionChallengeRelationship() {
        // given
        Challenge challenge = Challenge.builder()
                .challengeName("다이어트 챌린지")
                .challengeDescription("건강한 다이어트를 위한 챌린지")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();
        
        // when
        Mission mission = Mission.builder()
                .challenge(challenge)
                .missionName("물 8잔 마시기")
                .missionContent("하루에 물을 8잔 이상 마시기")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build();
        
        // then
        assertThat(mission.getChallenge()).isEqualTo(challenge);
    }

    @Test
    @DisplayName("챌린지가 null일 경우 예외가 발생한다")
    void createMissionWithNullChallenge() {
        // given & when & then
        assertThatThrownBy(() -> Mission.builder()
                .challenge(null)
                .missionName("미션명")
                .missionContent("내용")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Challenge cannot be null");
    }
}
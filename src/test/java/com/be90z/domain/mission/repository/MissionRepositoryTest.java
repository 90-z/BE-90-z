package com.be90z.domain.mission.repository;

import com.be90z.domain.challenge.entity.Challenge;
import com.be90z.domain.challenge.entity.ChallengeStatus;
import com.be90z.domain.challenge.repository.ChallengeRepository;
import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("MissionRepository 테스트")
class MissionRepositoryTest {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Test
    @DisplayName("MissionStatus로 미션 조회")
    void findByMissionStatus() {
        // given
        Challenge challenge = Challenge.builder()
                .challengeName("테스트 챌린지")
                .challengeDescription("테스트 설명")
                .challengeStatus(ChallengeStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();
        challenge = challengeRepository.save(challenge);

        Mission activeMission = Mission.builder()
                .missionName("활성 미션")
                .missionContent("활성 미션 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .challenge(challenge)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build();

        Mission completedMission = Mission.builder()
                .missionName("완료된 미션")
                .missionContent("완료된 미션 내용")
                .missionStatus(MissionStatus.COMPLETED)
                .challenge(challenge)
                .startDate(LocalDateTime.now().minusDays(7))
                .endDate(LocalDateTime.now())
                .build();

        missionRepository.save(activeMission);
        missionRepository.save(completedMission);

        // when
        List<Mission> activeMissions = missionRepository.findByMissionStatus(MissionStatus.ACTIVE);
        List<Mission> completedMissions = missionRepository.findByMissionStatus(MissionStatus.COMPLETED);

        // then
        assertThat(activeMissions).hasSize(1);
        assertThat(activeMissions.get(0).getMissionName()).isEqualTo("활성 미션");

        assertThat(completedMissions).hasSize(1);
        assertThat(completedMissions.get(0).getMissionName()).isEqualTo("완료된 미션");
    }

    @Test
    @DisplayName("MissionStatus로 미션 조회 - 생성일 내림차순")
    void findByMissionStatusOrderByCreatedAtDesc() {
        // given
        Challenge challenge = Challenge.builder()
                .challengeName("테스트 챌린지")
                .challengeDescription("테스트 설명")
                .challengeStatus(ChallengeStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();
        challenge = challengeRepository.save(challenge);

        Mission mission1 = Mission.builder()
                .missionName("첫 번째 미션")
                .missionContent("첫 번째 미션 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .challenge(challenge)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build();

        Mission mission2 = Mission.builder()
                .missionName("두 번째 미션")
                .missionContent("두 번째 미션 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .challenge(challenge)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .build();

        missionRepository.save(mission1);
        // 약간의 시간 차이를 두고 저장
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        missionRepository.save(mission2);

        // when
        List<Mission> missions = missionRepository.findByMissionStatusOrderByCreatedAtDesc(MissionStatus.ACTIVE);

        // then
        assertThat(missions).hasSize(2);
        // 최신 생성된 미션이 먼저 나와야 함
        assertThat(missions.get(0).getMissionName()).isEqualTo("두 번째 미션");
        assertThat(missions.get(1).getMissionName()).isEqualTo("첫 번째 미션");
    }
}
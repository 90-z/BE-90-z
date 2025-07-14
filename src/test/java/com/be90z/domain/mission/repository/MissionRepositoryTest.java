package com.be90z.domain.mission.repository;

import com.be90z.domain.challenge.entity.Challenge;
import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DisplayName("MissionRepository 테스트")
class MissionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MissionRepository missionRepository;

    @Test
    @DisplayName("미션 상태별 조회")
    void findByMissionStatus() {
        // given
        Challenge challenge = Challenge.builder()
                .challengeName("테스트 챌린지")
                .challengeDescription("테스트 챌린지 설명")
                .startDate(LocalDateTime.now().minusDays(5))
                .endDate(LocalDateTime.now().plusDays(25))
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();
        entityManager.persistAndFlush(challenge);

        Mission activeMission = Mission.builder()
                .missionCode(1L)
                .challenge(challenge)
                .missionName("활성 미션")
                .missionContent("활성 미션 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .build();

        Mission completedMission = Mission.builder()
                .missionCode(2L)
                .challenge(challenge)
                .missionName("완료된 미션")
                .missionContent("완료된 미션 내용")
                .missionStatus(MissionStatus.COMPLETED)
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now().minusDays(1))
                .createdAt(LocalDateTime.now().minusDays(30))
                .build();

        entityManager.persistAndFlush(activeMission);
        entityManager.persistAndFlush(completedMission);

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
    @DisplayName("현재 진행중인 미션 조회")
    void findActiveMissions() {
        // given
        LocalDateTime now = LocalDateTime.now();
        
        Challenge challenge2 = Challenge.builder()
                .challengeName("테스트 챌린지 2")
                .challengeDescription("테스트 챌린지 설명 2")
                .startDate(now.minusDays(10))
                .endDate(now.plusDays(10))
                .createdAt(now.minusDays(10))
                .build();
        entityManager.persistAndFlush(challenge2);
        
        Mission currentMission = Mission.builder()
                .missionCode(3L)
                .challenge(challenge2)
                .missionName("현재 진행중인 미션")
                .missionContent("현재 진행중인 미션 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(1))
                .createdAt(now.minusDays(1))
                .build();

        Mission expiredMission = Mission.builder()
                .missionCode(4L)
                .challenge(challenge2)
                .missionName("만료된 미션")
                .missionContent("만료된 미션 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .startDate(now.minusDays(10))
                .endDate(now.minusDays(1))
                .createdAt(now.minusDays(10))
                .build();

        entityManager.persistAndFlush(currentMission);
        entityManager.persistAndFlush(expiredMission);

        // when
        List<Mission> activeMissions = missionRepository.findActiveMissions(now);

        // then
        assertThat(activeMissions).hasSize(1);
        assertThat(activeMissions.get(0).getMissionName()).isEqualTo("현재 진행중인 미션");
    }
}
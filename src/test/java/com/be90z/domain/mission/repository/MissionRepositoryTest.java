package com.be90z.domain.mission.repository;

import com.be90z.domain.mission.entity.Mission;
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

    @Test
    @DisplayName("미션 조회 테스트")
    void findMissions() {
        // given
        Mission activeMission = Mission.builder()
                .missionName("활성 미션 제목")
                .missionContent("활성 미션 내용")
                .missionGoalCount(100)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();

        Mission completedMission = Mission.builder()
                .missionName("완료된 미션 제목")
                .missionContent("완료된 미션 내용")
                .missionGoalCount(50)
                .startDate(LocalDateTime.now().minusDays(7))
                .endDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        missionRepository.save(activeMission);
        missionRepository.save(completedMission);

        // when
        List<Mission> savedMissions = missionRepository.findAll();

        // then
        assertThat(savedMissions).hasSize(2);
        assertThat(savedMissions.get(0).getMissionContent()).contains("활성 미션 내용");
        assertThat(savedMissions.get(1).getMissionContent()).contains("완료된 미션 내용");
    }

    @Test
    @DisplayName("미션 조회 - 생성일 내림차순")
    void findMissionsOrderByCreatedAtDesc() {
        // given
        Mission mission1 = Mission.builder()
                .missionName("첫 번째 미션 제목")
                .missionContent("첫 번째 미션 내용")
                .missionGoalCount(100)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();

        Mission mission2 = Mission.builder()
                .missionName("두 번째 미션 제목")
                .missionContent("두 번째 미션 내용")
                .missionGoalCount(200)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now().plusNanos(1000000))  // 약간 다른 시간으로 생성일 구분
                .build();

        missionRepository.save(mission1);
        // 약간의 시간 차이를 두고 저장
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        missionRepository.save(mission2);

        // when
        List<Mission> missions = missionRepository.findAllByOrderByCreatedAtDesc();

        // then
        assertThat(missions).hasSize(2);
        // 최신 생성된 미션이 먼저 나와야 함 - 내용으로 확인
        assertThat(missions.get(0).getMissionContent()).contains("두 번째 미션");
        assertThat(missions.get(1).getMissionContent()).contains("첫 번째 미션");
    }
}
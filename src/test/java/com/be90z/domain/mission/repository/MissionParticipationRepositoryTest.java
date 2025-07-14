package com.be90z.domain.mission.repository;

import com.be90z.domain.challenge.entity.Challenge;
import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionParticipation;
import com.be90z.domain.mission.entity.MissionStatus;
import com.be90z.domain.mission.entity.ParticipateStatus;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.entity.UserAuthority;
import com.be90z.domain.user.entity.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DisplayName("MissionParticipationRepository 테스트")
class MissionParticipationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MissionParticipationRepository participationRepository;

    @Test
    @DisplayName("사용자별 미션 참여 조회")
    void findByUser() {
        // given
        User user = User.builder()
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .gender(Gender.WOMAN)
                .birth(1990)
                .build();
        entityManager.persistAndFlush(user);

        Challenge challenge = Challenge.builder()
                .challengeName("테스트 챌린지")
                .challengeDescription("테스트 챌린지 설명")
                .startDate(LocalDateTime.now().minusDays(5))
                .endDate(LocalDateTime.now().plusDays(25))
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();
        entityManager.persistAndFlush(challenge);

        Mission mission = Mission.builder()
                .missionCode(1L)
                .challenge(challenge)
                .missionName("테스트 미션")
                .missionContent("테스트 미션 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(mission);

        MissionParticipation participation = MissionParticipation.builder()
                .participateCode(1L)
                .participateStatus(ParticipateStatus.PART_COMPLETE)
                .user(user)
                .mission(mission)
                .build();
        entityManager.persistAndFlush(participation);

        // when
        List<MissionParticipation> participations = participationRepository.findByUser(user);

        // then
        assertThat(participations).hasSize(1);
        assertThat(participations.get(0).getUser()).isEqualTo(user);
        assertThat(participations.get(0).getMission()).isEqualTo(mission);
    }

    @Test
    @DisplayName("사용자와 미션으로 참여 정보 조회")
    void findByUserAndMission() {
        // given
        User user = User.builder()
                .provider("kakao")
                .nickname("테스트유저2")
                .email("test2@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .gender(Gender.MAN)
                .birth(1985)
                .build();
        entityManager.persistAndFlush(user);

        Challenge challenge2 = Challenge.builder()
                .challengeName("테스트 챌린지2")
                .challengeDescription("테스트 챌린지2 설명")
                .startDate(LocalDateTime.now().minusDays(5))
                .endDate(LocalDateTime.now().plusDays(25))
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();
        entityManager.persistAndFlush(challenge2);

        Mission mission = Mission.builder()
                .missionCode(2L)
                .challenge(challenge2)
                .missionName("테스트 미션2")
                .missionContent("테스트 미션2 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(mission);

        MissionParticipation participation = MissionParticipation.builder()
                .participateCode(2L)
                .participateStatus(ParticipateStatus.PART_BEFORE)
                .user(user)
                .mission(mission)
                .build();
        entityManager.persistAndFlush(participation);

        // when
        Optional<MissionParticipation> found = participationRepository.findByUserAndMission(user, mission);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getParticipateStatus()).isEqualTo(ParticipateStatus.PART_BEFORE);
    }

    @Test
    @DisplayName("사용자의 완료된 참여 조회")
    void findCompletedParticipationsByUser() {
        // given
        User user = User.builder()
                .provider("kakao")
                .nickname("완료유저")
                .email("complete@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .gender(Gender.WOMAN)
                .birth(1992)
                .build();
        entityManager.persistAndFlush(user);

        Challenge challenge3 = Challenge.builder()
                .challengeName("완료된 챌린지")
                .challengeDescription("완료된 챌린지 설명")
                .startDate(LocalDateTime.now().minusDays(35))
                .endDate(LocalDateTime.now().minusDays(5))
                .createdAt(LocalDateTime.now().minusDays(35))
                .build();
        entityManager.persistAndFlush(challenge3);

        Mission mission = Mission.builder()
                .missionCode(3L)
                .challenge(challenge3)
                .missionName("완료된 미션")
                .missionContent("완료된 미션 내용")
                .missionStatus(MissionStatus.COMPLETED)
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now().minusDays(1))
                .createdAt(LocalDateTime.now().minusDays(30))
                .build();
        entityManager.persistAndFlush(mission);

        MissionParticipation participation = MissionParticipation.builder()
                .participateCode(3L)
                .participateStatus(ParticipateStatus.PART_COMPLETE)
                .user(user)
                .mission(mission)
                .build();
        entityManager.persistAndFlush(participation);

        // when
        List<MissionParticipation> completedParticipations = 
                participationRepository.findCompletedParticipationsByUser(user);

        // then
        assertThat(completedParticipations).hasSize(1);
        assertThat(completedParticipations.get(0).getParticipateStatus())
                .isEqualTo(ParticipateStatus.PART_COMPLETE);
    }
}
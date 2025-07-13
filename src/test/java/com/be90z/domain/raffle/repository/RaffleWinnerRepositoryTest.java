package com.be90z.domain.raffle.repository;

import com.be90z.domain.raffle.entity.RaffleEntry;
import com.be90z.domain.raffle.entity.RaffleWinner;
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

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DisplayName("RaffleWinnerRepository 테스트")
class RaffleWinnerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RaffleWinnerRepository raffleWinnerRepository;

    @Test
    @DisplayName("래플 엔트리별 당첨자 조회")
    void findByRaffleEntry() {
        // given
        User user = User.builder()
                .provider("kakao")
                .nickname("당첨자")
                .email("winner@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .gender(Gender.WOMAN)
                .birth(1990)
                .build();
        entityManager.persistAndFlush(user);

        Mission mission = Mission.builder()
                .missionCode(1L)
                .missionName("당첨 미션")
                .missionContent("당첨 미션 내용")
                .missionStatus(MissionStatus.COMPLETED)
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now().minusDays(1))
                .createdAt(LocalDateTime.now().minusDays(30))
                .build();
        entityManager.persistAndFlush(mission);

        MissionParticipation participation = MissionParticipation.builder()
                .participateCode(1L)
                .participateStatus(ParticipateStatus.PART_COMPLETE)
                .user(user)
                .mission(mission)
                .build();
        entityManager.persistAndFlush(participation);

        RaffleEntry raffleEntry = RaffleEntry.builder()
                .raffleCode(202501L)
                .participation(participation)
                .raffleName("2025년 1월 래플")
                .rafflePrizeCont("스타벅스 기프티콘")
                .raffleWinner(3)
                .raffleDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(raffleEntry);

        RaffleWinner winner = RaffleWinner.builder()
                .winnerCode(1L)
                .winnerPrize("스타벅스 아메리카노 기프티콘")
                .raffleEntry(raffleEntry)
                .build();
        entityManager.persistAndFlush(winner);

        // when
        List<RaffleWinner> winners = raffleWinnerRepository.findByRaffleEntry(raffleEntry);

        // then
        assertThat(winners).hasSize(1);
        assertThat(winners.get(0).getWinnerPrize()).isEqualTo("스타벅스 아메리카노 기프티콘");
        assertThat(winners.get(0).getRaffleEntry()).isEqualTo(raffleEntry);
    }

    @Test
    @DisplayName("래플 코드별 당첨자 조회")
    void findByRaffleEntry_RaffleCode() {
        // given
        User user1 = User.builder()
                .provider("kakao")
                .nickname("당첨자1")
                .email("winner1@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .gender(Gender.WOMAN)
                .birth(1990)
                .build();
        entityManager.persistAndFlush(user1);

        User user2 = User.builder()
                .provider("kakao")
                .nickname("당첨자2")
                .email("winner2@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .gender(Gender.MAN)
                .birth(1985)
                .build();
        entityManager.persistAndFlush(user2);

        Mission mission = Mission.builder()
                .missionCode(2L)
                .missionName("당첨 미션2")
                .missionContent("당첨 미션2 내용")
                .missionStatus(MissionStatus.COMPLETED)
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now().minusDays(1))
                .createdAt(LocalDateTime.now().minusDays(30))
                .build();
        entityManager.persistAndFlush(mission);

        MissionParticipation participation1 = MissionParticipation.builder()
                .participateCode(2L)
                .participateStatus(ParticipateStatus.PART_COMPLETE)
                .user(user1)
                .mission(mission)
                .build();
        entityManager.persistAndFlush(participation1);

        MissionParticipation participation2 = MissionParticipation.builder()
                .participateCode(3L)
                .participateStatus(ParticipateStatus.PART_COMPLETE)
                .user(user2)
                .mission(mission)
                .build();
        entityManager.persistAndFlush(participation2);

        RaffleEntry raffleEntry1 = RaffleEntry.builder()
                .raffleCode(202502L)
                .participation(participation1)
                .raffleName("2025년 2월 래플")
                .rafflePrizeCont("올리브영 기프티콘")
                .raffleWinner(3)
                .raffleDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(raffleEntry1);

        RaffleEntry raffleEntry2 = RaffleEntry.builder()
                .raffleCode(202502L)
                .participation(participation2)
                .raffleName("2025년 2월 래플")
                .rafflePrizeCont("올리브영 기프티콘")
                .raffleWinner(3)
                .raffleDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(raffleEntry2);

        RaffleWinner winner1 = RaffleWinner.builder()
                .winnerCode(2L)
                .winnerPrize("올리브영 5만원 기프티콘")
                .raffleEntry(raffleEntry1)
                .build();
        entityManager.persistAndFlush(winner1);

        RaffleWinner winner2 = RaffleWinner.builder()
                .winnerCode(3L)
                .winnerPrize("올리브영 3만원 기프티콘")
                .raffleEntry(raffleEntry2)
                .build();
        entityManager.persistAndFlush(winner2);

        // when
        List<RaffleWinner> winners = raffleWinnerRepository.findByRaffleEntry_RaffleCode(202502L);

        // then
        assertThat(winners).hasSize(2);
        assertThat(winners).extracting(RaffleWinner::getWinnerPrize)
                .containsExactlyInAnyOrder("올리브영 5만원 기프티콘", "올리브영 3만원 기프티콘");
    }
}
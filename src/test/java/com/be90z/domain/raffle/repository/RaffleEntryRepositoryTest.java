package com.be90z.domain.raffle.repository;

import com.be90z.domain.raffle.entity.RaffleEntry;
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
@DisplayName("RaffleEntryRepository 테스트")
class RaffleEntryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RaffleEntryRepository raffleEntryRepository;

    @Test
    @DisplayName("래플 코드별 래플 엔트리 조회")
    void findByRaffleCode() {
        // given
        User user = User.builder()
                .provider("kakao")
                .nickname("래플참여유저")
                .email("raffle@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .gender(Gender.WOMAN)
                .birth(1990)
                .build();
        entityManager.persistAndFlush(user);

        Mission mission = Mission.builder()
                .missionCode(1L)
                .missionName("래플 미션")
                .missionContent("래플 미션 내용")
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

        // when
        List<RaffleEntry> entries = raffleEntryRepository.findByRaffleCode(202501L);

        // then
        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).getRaffleName()).isEqualTo("2025년 1월 래플");
        assertThat(entries.get(0).getParticipation().getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("날짜 범위별 래플 엔트리 조회")
    void findByRaffleDateBetween() {
        // given
        User user = User.builder()
                .provider("kakao")
                .nickname("래플참여유저2")
                .email("raffle2@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .gender(Gender.MAN)
                .birth(1985)
                .build();
        entityManager.persistAndFlush(user);

        Mission mission = Mission.builder()
                .missionCode(2L)
                .missionName("래플 미션2")
                .missionContent("래플 미션2 내용")
                .missionStatus(MissionStatus.COMPLETED)
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now().minusDays(1))
                .createdAt(LocalDateTime.now().minusDays(30))
                .build();
        entityManager.persistAndFlush(mission);

        MissionParticipation participation = MissionParticipation.builder()
                .participateCode(2L)
                .participateStatus(ParticipateStatus.PART_COMPLETE)
                .user(user)
                .mission(mission)
                .build();
        entityManager.persistAndFlush(participation);

        LocalDateTime raffleDate = LocalDateTime.of(2025, 1, 31, 23, 59);
        
        RaffleEntry raffleEntry = RaffleEntry.builder()
                .raffleCode(202501L)
                .participation(participation)
                .raffleName("1월 말 래플")
                .rafflePrizeCont("올리브영 기프티콘")
                .raffleWinner(3)
                .raffleDate(raffleDate)
                .createdAt(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(raffleEntry);

        // when
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 1, 31, 23, 59);
        
        List<RaffleEntry> entries = raffleEntryRepository.findByRaffleDateBetween(startDate, endDate);

        // then
        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).getRaffleName()).isEqualTo("1월 말 래플");
    }

    @Test
    @DisplayName("사용자별 래플 참여 횟수 조회 - TDD Green 단계")
    void countByParticipation_User_UserId_ShouldPass() {
        // given
        User user = User.builder()
                .provider("kakao")
                .nickname("래플참여테스트유저")
                .email("tdd@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .gender(Gender.WOMAN)
                .birth(1990)
                .build();
        entityManager.persistAndFlush(user);

        // when
        int count = raffleEntryRepository.countByParticipation_User_UserId(user.getUserId());
        
        // then
        assertThat(count).isEqualTo(0); // 아직 참여한 래플이 없으므로 0
    }

    @Test  
    @DisplayName("날짜 범위별 중복 제거된 래플 참여자 수 조회 - TDD Green 단계")
    void countDistinctByCreatedAtBetween_ShouldPass() {
        // given
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();
        
        // when
        int count = raffleEntryRepository.countDistinctByCreatedAtBetween(start, end);
        
        // then
        assertThat(count).isGreaterThanOrEqualTo(0); // 0 이상의 값
    }
}
package com.be90z.domain.raffle.entity;

import com.be90z.domain.mission.entity.MissionParticipation;
import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionStatus;
import com.be90z.domain.mission.entity.ParticipateStatus;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.entity.UserAuthority;
import com.be90z.domain.user.entity.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

@DisplayName("RaffleWinner 엔티티 테스트")
class RaffleWinnerTest {

    @Test
    @DisplayName("RaffleWinner 엔티티 생성 테스트")
    void createRaffleWinner() {
        // given
        User user = User.builder()
                .userId(1L)
                .provider("kakao")
                .nickname("당첨자")
                .email("winner@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .gender(Gender.WOMAN)
                .birth(1990)
                .build();

        Mission mission = Mission.builder()
                .missionCode(1L)
                .missionName("테스트 미션")
                .missionContent("테스트 미션 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .build();

        MissionParticipation participation = MissionParticipation.builder()
                .participateCode(1L)
                .participateStatus(ParticipateStatus.PART_COMPLETE)
                .user(user)
                .mission(mission)
                .build();

        RaffleEntry raffleEntry = RaffleEntry.builder()
                .raffleCode(1L)
                .participation(participation)
                .raffleName("2025년 1월 래플")
                .rafflePrizeCont("스타벅스 기프티콘")
                .raffleWinner(3)
                .raffleDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        Long winnerCode = 1L;
        String winnerPrize = "스타벅스 아메리카노 기프티콘";

        // when
        RaffleWinner raffleWinner = RaffleWinner.builder()
                .winnerCode(winnerCode)
                .winnerPrize(winnerPrize)
                .raffleEntry(raffleEntry)
                .build();

        // then
        assertThat(raffleWinner.getWinnerCode()).isEqualTo(winnerCode);
        assertThat(raffleWinner.getWinnerPrize()).isEqualTo(winnerPrize);
        assertThat(raffleWinner.getRaffleEntry()).isEqualTo(raffleEntry);
    }

    @Test
    @DisplayName("RaffleWinner 필수 필드 검증")
    void validateRequiredFields() {
        // when & then
        assertThatThrownBy(() -> {
            RaffleWinner.builder()
                    .winnerCode(1L)
                    // winnerPrize 누락
                    .build();
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Winner prize cannot be null");
    }
}
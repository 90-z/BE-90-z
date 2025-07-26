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

@DisplayName("RaffleEntry 엔티티 테스트")
class RaffleEntryTest {

    @Test
    @DisplayName("RaffleEntry 엔티티 생성 테스트")
    void createRaffleEntry() {
        // given
        User user = User.builder()
                .userId(1L)
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
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

        Long raffleCode = 1L;
        String raffleName = "2025년 1월 래플";
        String rafflePrizeCont = "스타벅스 기프티콘";
        Integer raffleWinner = 3;
        LocalDateTime raffleDate = LocalDateTime.now();
        LocalDateTime createdAt = LocalDateTime.now();

        // when
        RaffleEntry raffleEntry = RaffleEntry.builder()
                .raffleCode(raffleCode)
                .participation(participation)
                .raffleName(raffleName)
                .rafflePrizeCont(rafflePrizeCont)
                .raffleWinner(raffleWinner)
                .raffleDate(raffleDate)
                .createdAt(createdAt)
                .build();

        // then
        assertThat(raffleEntry.getRaffleCode()).isEqualTo(raffleCode);
        assertThat(raffleEntry.getParticipation()).isEqualTo(participation);
        assertThat(raffleEntry.getRaffleName()).isEqualTo(raffleName);
        assertThat(raffleEntry.getRafflePrizeCont()).isEqualTo(rafflePrizeCont);
        assertThat(raffleEntry.getRaffleWinner()).isEqualTo(raffleWinner);
        assertThat(raffleEntry.getRaffleDate()).isEqualTo(raffleDate);
        assertThat(raffleEntry.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("RaffleEntry 필수 필드 검증")
    void validateRequiredFields() {
        // given
        User user = User.builder()
                .userId(1L)
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .build();

        // when & then
        assertThatThrownBy(() -> {
            RaffleEntry.builder()
                    .raffleCode(1L)
                    .raffleName("테스트 래플")
                    // participation 누락
                    .build();
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Participation cannot be null");
    }
}
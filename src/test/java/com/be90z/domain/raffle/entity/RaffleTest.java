package com.be90z.domain.raffle.entity;

import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionParticipation;
import com.be90z.domain.mission.entity.ParticipateStatus;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.entity.UserAuthority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Raffle 엔티티 테스트")
class RaffleTest {

    @Test
    @DisplayName("Raffle 엔티티 생성 테스트")
    void createRaffle() {
        // given
        User user = User.builder()
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .build();

        Mission mission = Mission.builder()
                .missionName("테스트 미션 제목")
                .missionContent("테스트 미션")
                .missionGoalCount(1)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();

        MissionParticipation participation = MissionParticipation.builder()
                .participateStatus(ParticipateStatus.PART_COMPLETE)
                .participateCount(1)
                .user(user)
                .mission(mission)
                .build();

        Long raffleCode = 1L;
        String raffleName = "월간 래플";
        LocalDateTime raffleDate = LocalDateTime.now();

        // when
        Raffle raffle = Raffle.builder()
                .raffleCode(raffleCode)
                .participation(participation)
                .raffleName(raffleName)
                .raffleDate(raffleDate)
                .build();

        // then
        assertThat(raffle.getRaffleCode()).isEqualTo(raffleCode);
        assertThat(raffle.getParticipateCode()).isEqualTo(participation.getParticipateCode());
        assertThat(raffle.getRaffleName()).isEqualTo(raffleName);
        assertThat(raffle.getRaffleDate()).isEqualTo(raffleDate);
        assertThat(raffle.getCreatedAt()).isNotNull();
        assertThat(raffle.getParticipation()).isEqualTo(participation);
    }


    @Test
    @DisplayName("Raffle 엔티티 필수 필드 검증 - raffleName")
    void validateRaffleNameRequired() {
        // given
        User user = User.builder()
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .build();

        Mission mission = Mission.builder()
                .missionName("테스트 미션 제목")
                .missionContent("테스트 미션")
                .missionGoalCount(1)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();

        MissionParticipation participation = MissionParticipation.builder()
                .participateStatus(ParticipateStatus.PART_COMPLETE)
                .participateCount(1)
                .user(user)
                .mission(mission)
                .build();

        // when & then
        assertThatThrownBy(() -> {
            Raffle.builder()
                    .raffleCode(1L)
                    .participation(participation)
                    .raffleDate(LocalDateTime.now())
                    // raffleName 누락
                    .build();
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Raffle name cannot be null");
    }

    @Test
    @DisplayName("Raffle 엔티티 필수 필드 검증 - raffleDate")
    void validateRaffleDateRequired() {
        // given
        User user = User.builder()
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .build();

        Mission mission = Mission.builder()
                .missionName("테스트 미션 제목")
                .missionContent("테스트 미션")
                .missionGoalCount(1)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();

        MissionParticipation participation = MissionParticipation.builder()
                .participateStatus(ParticipateStatus.PART_COMPLETE)
                .participateCount(1)
                .user(user)
                .mission(mission)
                .build();

        // when & then
        assertThatThrownBy(() -> {
            Raffle.builder()
                    .raffleCode(1L)
                    .participation(participation)
                    .raffleName("월간 래플")
                    // raffleDate 누락
                    .build();
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Raffle date cannot be null");
    }
}
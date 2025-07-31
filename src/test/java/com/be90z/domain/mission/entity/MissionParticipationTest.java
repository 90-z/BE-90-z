package com.be90z.domain.mission.entity;

import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.entity.UserAuthority;
import com.be90z.domain.user.entity.Gender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

@DisplayName("MissionParticipation 엔티티 테스트")
class MissionParticipationTest {

    @Test
    @DisplayName("MissionParticipation 엔티티 생성 테스트")
    void createMissionParticipation() {
        // given
        User user = User.builder()
                .userId(1L)
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .gender(Gender.MAN)
                .birth(1990)
                .build();

        Mission mission = Mission.builder()
                .missionCode(1L)
                .missionName("테스트 미션")
                .missionContent("테스트 미션 내용")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(100)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .build();

        Long participateCode = 1L;
        ParticipateStatus participateStatus = ParticipateStatus.PART_BEFORE;

        // when
        MissionParticipation participation = MissionParticipation.builder()
                .participateCode(participateCode)
                .participateStatus(participateStatus)
                .user(user)
                .mission(mission)
                .build();

        // then
        assertThat(participation.getParticipateCode()).isEqualTo(participateCode);
        assertThat(participation.getParticipateStatus()).isEqualTo(participateStatus);
        assertThat(participation.getUser()).isEqualTo(user);
        assertThat(participation.getMission()).isEqualTo(mission);
    }

    @Test
    @DisplayName("MissionParticipation 필수 필드 검증")
    void validateRequiredFields() {
        // given
        User user = User.builder()
                .userId(1L)
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .gender(Gender.MAN)
                .birth(1990)
                .createdAt(LocalDateTime.now())
                .build();

        // when & then
        assertThatThrownBy(() -> {
            MissionParticipation.builder()
                    .participateCode(1L)
                    .user(user)
                    // mission 누락
                    .build();
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Mission cannot be null");
    }
}
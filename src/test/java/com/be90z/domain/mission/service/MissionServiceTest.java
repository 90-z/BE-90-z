package com.be90z.domain.mission.service;

import com.be90z.domain.challenge.entity.Challenge;
import com.be90z.domain.mission.dto.request.MissionJoinReqDTO;
import com.be90z.domain.mission.dto.response.MissionJoinResDTO;
import com.be90z.domain.mission.dto.response.MissionListResDTO;
import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionParticipation;
import com.be90z.domain.mission.entity.MissionStatus;
import com.be90z.domain.mission.entity.ParticipateStatus;
import com.be90z.domain.mission.repository.MissionParticipationRepository;
import com.be90z.domain.mission.repository.MissionRepository;
import com.be90z.domain.user.entity.Gender;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.entity.UserAuthority;
import com.be90z.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("MissionService 테스트")
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private MissionParticipationRepository missionParticipationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MissionService missionService;

    @Test
    @DisplayName("활성 미션 목록 조회 - 정상 케이스")
    void getActiveMissions_ActiveMissions_ReturnsDTO() {
        // given
        Challenge challenge = Challenge.builder()
                .challengeId(1L)
                .challengeName("물 마시기 챌린지")
                .challengeDescription("매일 물을 마시는 챌린지")
                .startDate(LocalDateTime.now().minusDays(5))
                .endDate(LocalDateTime.now().plusDays(25))
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();

        Mission mission1 = Mission.builder()
                .missionCode(1L)
                .challenge(challenge)
                .missionName("매일 물 8잔 마시기")
                .missionContent("하루에 물 8잔을 마시고 인증샷을 올려주세요")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(100)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(6))
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        Mission mission2 = Mission.builder()
                .missionCode(2L)
                .challenge(challenge)
                .missionName("운동하기")
                .missionContent("매일 30분 운동하기")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(50)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .build();

        List<Mission> missions = List.of(mission1, mission2);

        given(missionRepository.findByMissionStatusOrderByCreatedAtDesc(MissionStatus.ACTIVE))
                .willReturn(missions);
        given(missionParticipationRepository.countCompletedParticipationsByMission(mission1))
                .willReturn(45L);
        given(missionParticipationRepository.countCompletedParticipationsByMission(mission2))
                .willReturn(20L);

        // when
        List<MissionListResDTO> result = missionService.getActiveMissions();

        // then
        assertThat(result).hasSize(2);
        
        MissionListResDTO dto1 = result.get(0);
        assertThat(dto1.getMissionCode()).isEqualTo(1L);
        assertThat(dto1.getMissionName()).isEqualTo("매일 물 8잔 마시기");
        assertThat(dto1.getMissionStatus()).isEqualTo("ACTIVE");
        assertThat(dto1.getCurrentParticipants()).isEqualTo(45);

        MissionListResDTO dto2 = result.get(1);
        assertThat(dto2.getMissionCode()).isEqualTo(2L);
        assertThat(dto2.getMissionName()).isEqualTo("운동하기");
        assertThat(dto2.getCurrentParticipants()).isEqualTo(20);
    }

    @Test
    @DisplayName("미션 참여 - 정상 케이스")
    void joinMission_ValidRequest_ReturnsDTO() {
        // given
        Long userId = 1L;
        Long missionCode = 2L;
        
        User user = User.builder()
                .userId(userId)
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .gender(Gender.MAN)
                .birth(1990)
                .createdAt(LocalDateTime.now())
                .build();

        Challenge challenge = Challenge.builder()
                .challengeId(1L)
                .challengeName("물 마시기 챌린지")
                .challengeDescription("매일 물을 마시는 챌린지")
                .startDate(LocalDateTime.now().minusDays(5))
                .endDate(LocalDateTime.now().plusDays(25))
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();

        Mission mission = Mission.builder()
                .missionCode(missionCode)
                .challenge(challenge)
                .missionName("매일 물 8잔 마시기")
                .missionContent("하루에 물 8잔을 마시고 인증샷을 올려주세요")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(100)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(6))
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        MissionParticipation participation = MissionParticipation.builder()
                .participateCode(123L)
                .participateStatus(ParticipateStatus.PART_BEFORE)
                .user(user)
                .mission(mission)
                .build();

        MissionJoinReqDTO request = MissionJoinReqDTO.builder()
                .userId(userId)
                .missionCode(missionCode)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(missionRepository.findById(missionCode)).willReturn(Optional.of(mission));
        given(missionParticipationRepository.findByUserAndMission(user, mission)).willReturn(Optional.empty());
        given(missionParticipationRepository.save(any(MissionParticipation.class))).willReturn(participation);

        // when
        MissionJoinResDTO result = missionService.joinMission(request);

        // then
        assertThat(result.getParticipateCode()).isEqualTo(123L);
        assertThat(result.getMessage()).isEqualTo("미션 참여가 완료되었습니다.");
        assertThat(result.getParticipateStatus()).isEqualTo("PART_BEFORE");
        
        verify(missionParticipationRepository).save(any(MissionParticipation.class));
    }

    @Test
    @DisplayName("미션 참여 - 존재하지 않는 사용자")
    void joinMission_UserNotFound_ThrowsException() {
        // given
        MissionJoinReqDTO request = MissionJoinReqDTO.builder()
                .userId(999L)
                .missionCode(1L)
                .build();

        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> missionService.joinMission(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found with id: 999");
    }

    @Test
    @DisplayName("미션 참여 - 존재하지 않는 미션")
    void joinMission_MissionNotFound_ThrowsException() {
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

        MissionJoinReqDTO request = MissionJoinReqDTO.builder()
                .userId(1L)
                .missionCode(999L)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(missionRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> missionService.joinMission(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mission not found with code: 999");
    }

    @Test
    @DisplayName("미션 참여 - 이미 참여한 미션")
    void joinMission_AlreadyParticipated_ThrowsException() {
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

        Challenge challenge = Challenge.builder()
                .challengeId(1L)
                .challengeName("물 마시기 챌린지")
                .challengeDescription("매일 물을 마시는 챌린지")
                .startDate(LocalDateTime.now().minusDays(5))
                .endDate(LocalDateTime.now().plusDays(25))
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();

        Mission mission = Mission.builder()
                .missionCode(1L)
                .challenge(challenge)
                .missionName("매일 물 8잔 마시기")
                .missionContent("하루에 물 8잔을 마시고 인증샷을 올려주세요")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(100)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(6))
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        MissionParticipation existingParticipation = MissionParticipation.builder()
                .participateCode(100L)
                .participateStatus(ParticipateStatus.PART_BEFORE)
                .user(user)
                .mission(mission)
                .build();

        MissionJoinReqDTO request = MissionJoinReqDTO.builder()
                .userId(1L)
                .missionCode(1L)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(missionRepository.findById(1L)).willReturn(Optional.of(mission));
        given(missionParticipationRepository.findByUserAndMission(user, mission))
                .willReturn(Optional.of(existingParticipation));

        // when & then
        assertThatThrownBy(() -> missionService.joinMission(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User already participated in this mission");
    }

    @Test
    @DisplayName("미션 상태 변경 - PART_COMPLETE로 변경")
    void updateMissionStatus_ToComplete_ReturnsUpdatedStatus() {
        // given
        Long userId = 1L;
        Long missionCode = 2L;
        
        User user = User.builder()
                .userId(userId)
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .gender(Gender.MAN)
                .birth(1990)
                .createdAt(LocalDateTime.now())
                .build();

        Challenge challenge = Challenge.builder()
                .challengeId(1L)
                .challengeName("물 마시기 챌린지")
                .challengeDescription("매일 물을 마시는 챌린지")
                .startDate(LocalDateTime.now().minusDays(5))
                .endDate(LocalDateTime.now().plusDays(25))
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();

        Mission mission = Mission.builder()
                .missionCode(missionCode)
                .challenge(challenge)
                .missionName("매일 물 8잔 마시기")
                .missionContent("하루에 물 8잔을 마시고 인증샷을 올려주세요")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(100)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(6))
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        MissionParticipation participation = MissionParticipation.builder()
                .participateCode(123L)
                .participateStatus(ParticipateStatus.PART_BEFORE)
                .user(user)
                .mission(mission)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(missionRepository.findById(missionCode)).willReturn(Optional.of(mission));
        given(missionParticipationRepository.findByUserAndMission(user, mission))
                .willReturn(Optional.of(participation));

        // when
        String result = missionService.updateMissionStatus(userId, missionCode, "PART_COMPLETE");

        // then
        assertThat(result).isEqualTo("PART_COMPLETE");
        assertThat(participation.getParticipateStatus()).isEqualTo(ParticipateStatus.PART_COMPLETE);
    }

    @Test
    @DisplayName("미션 상태 변경 - 참여하지 않은 미션")
    void updateMissionStatus_NotParticipated_ThrowsException() {
        // given
        Long userId = 1L;
        Long missionCode = 2L;
        
        User user = User.builder()
                .userId(userId)
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .gender(Gender.MAN)
                .birth(1990)
                .createdAt(LocalDateTime.now())
                .build();

        Challenge challenge = Challenge.builder()
                .challengeId(1L)
                .challengeName("물 마시기 챌린지")
                .challengeDescription("매일 물을 마시는 챌린지")
                .startDate(LocalDateTime.now().minusDays(5))
                .endDate(LocalDateTime.now().plusDays(25))
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();

        Mission mission = Mission.builder()
                .missionCode(missionCode)
                .challenge(challenge)
                .missionName("매일 물 8잔 마시기")
                .missionContent("하루에 물 8잔을 마시고 인증샷을 올려주세요")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(100)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(6))
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(missionRepository.findById(missionCode)).willReturn(Optional.of(mission));
        given(missionParticipationRepository.findByUserAndMission(user, mission))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> missionService.updateMissionStatus(userId, missionCode, "PART_COMPLETE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not participated in this mission");
    }
}
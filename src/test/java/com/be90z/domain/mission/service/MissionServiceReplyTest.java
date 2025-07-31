package com.be90z.domain.mission.service;

import com.be90z.domain.mission.dto.request.MissionReplyReqDTO;
import com.be90z.domain.mission.dto.response.MissionReplyResDTO;
import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.repository.MissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MissionService 미션 댓글 기능 테스트")
class MissionServiceReplyTest {

    @Mock
    private MissionRepository missionRepository;

    @InjectMocks
    private MissionService missionService;

    @Test
    @DisplayName("미션 댓글을 성공적으로 생성할 수 있다")
    void shouldCreateMissionReplySuccessfully() {
        // given
        Long missionCode = 1L;
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .missionName("테스트 미션")
                .missionContent("테스트 미션 내용입니다.")
                .build();

        Mission existingMission = Mission.builder()
                .missionCode(missionCode)
                .missionName("기존 미션")
                .missionContent("기존 미션 내용")
                .missionGoalCount(5)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .maxParticipants(100)
                .build();

        when(missionRepository.findById(missionCode)).thenReturn(Optional.of(existingMission));

        // when
        MissionReplyResDTO result = missionService.replyToMission(missionCode, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getReplyCode()).isNotNull();
        assertThat(result.getMissionCode()).isEqualTo(missionCode);
        assertThat(result.getMissionName()).isEqualTo("테스트 미션");
        assertThat(result.getMissionContent()).isEqualTo("테스트 미션 내용입니다.");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("존재하지 않는 미션에 댓글을 달 때 예외가 발생한다")
    void shouldThrowExceptionWhenMissionNotFound() {
        // given
        Long nonExistentMissionCode = 999L;
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .missionName("테스트 미션")
                .missionContent("테스트 미션 내용")
                .build();

        when(missionRepository.findById(nonExistentMissionCode)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> missionService.replyToMission(nonExistentMissionCode, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Mission not found with code: " + nonExistentMissionCode);
    }

    @Test
    @DisplayName("미션 댓글 생성 시 고유한 replyCode가 생성된다")
    void shouldGenerateUniqueReplyCode() {
        // given
        Long missionCode = 1L;
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .missionName("테스트 미션")
                .missionContent("테스트 미션 내용")
                .build();

        Mission existingMission = Mission.builder()
                .missionCode(missionCode)
                .missionName("기존 미션")
                .missionContent("기존 미션 내용")
                .missionGoalCount(3)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(7))
                .maxParticipants(50)
                .build();

        when(missionRepository.findById(missionCode)).thenReturn(Optional.of(existingMission));

        // when
        MissionReplyResDTO result1 = missionService.replyToMission(missionCode, request);
        MissionReplyResDTO result2 = missionService.replyToMission(missionCode, request);

        // then
        assertThat(result1.getReplyCode()).isNotNull();
        assertThat(result2.getReplyCode()).isNotNull();
        assertThat(result1.getReplyCode()).isNotEqualTo(result2.getReplyCode());
    }

    @Test
    @DisplayName("미션 댓글 생성 시 요청 데이터가 정확히 반영된다")
    void shouldReflectRequestDataAccurately() {
        // given
        Long missionCode = 2L;
        String expectedMissionName = "특별한 미션";
        String expectedMissionContent = "이것은 특별한 미션 내용입니다.";
        
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .missionName(expectedMissionName)
                .missionContent(expectedMissionContent)
                .build();

        Mission existingMission = Mission.builder()
                .missionCode(missionCode)
                .missionName("원래 미션")
                .missionContent("원래 미션 내용")
                .missionGoalCount(10)
                .startDate(LocalDateTime.now().minusHours(2))
                .endDate(LocalDateTime.now().plusDays(14))
                .maxParticipants(200)
                .build();

        when(missionRepository.findById(missionCode)).thenReturn(Optional.of(existingMission));

        // when
        MissionReplyResDTO result = missionService.replyToMission(missionCode, request);

        // then
        assertThat(result.getMissionName()).isEqualTo(expectedMissionName);
        assertThat(result.getMissionContent()).isEqualTo(expectedMissionContent);
        assertThat(result.getMissionCode()).isEqualTo(missionCode);
    }

    @Test
    @DisplayName("미션 댓글 생성 시 createdAt이 현재 시간으로 설정된다")
    void shouldSetCreatedAtToCurrentTime() {
        // given
        Long missionCode = 3L;
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .missionName("시간 테스트 미션")
                .missionContent("시간 테스트 내용")
                .build();

        Mission existingMission = Mission.builder()
                .missionCode(missionCode)
                .missionName("시간 테스트용 미션")
                .missionContent("시간 테스트용 미션")
                .missionGoalCount(1)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .maxParticipants(10)
                .build();

        when(missionRepository.findById(missionCode)).thenReturn(Optional.of(existingMission));

        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

        // when
        MissionReplyResDTO result = missionService.replyToMission(missionCode, request);

        // then
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);
        assertThat(result.getCreatedAt()).isAfter(beforeCreation);
        assertThat(result.getCreatedAt()).isBefore(afterCreation);
    }

    @Test
    @DisplayName("null 파라미터로 요청 시 적절한 예외가 발생한다")
    void shouldThrowExceptionWhenParametersAreNull() {
        // given
        Long missionCode = 1L;
        MissionReplyReqDTO nullRequest = null;

        // when & then
        assertThatThrownBy(() -> missionService.replyToMission(missionCode, nullRequest))
                .isInstanceOf(IllegalArgumentException.class);

        // given
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .missionName("테스트")
                .missionContent("내용")
                .build();

        // when & then
        assertThatThrownBy(() -> missionService.replyToMission(null, request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
package com.be90z.domain.mission.service;

import com.be90z.domain.mission.dto.request.MissionReplyReqDTO;
import com.be90z.domain.mission.dto.response.MissionReplyResDTO;
import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.repository.MissionRepository;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.entity.UserAuthority;
import com.be90z.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("MissionService Reply 테스트")
class MissionServiceReplyTest {

    @Autowired
    private MissionService missionService;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Mission testMission;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = User.builder()
                .provider("kakao")
                .nickname("테스트유저")
                .email("test@example.com")
                .auth(UserAuthority.USER)
                .createdAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(testUser);

        // 테스트 미션 생성
        testMission = Mission.builder()
                .missionName("테스트 미션 제목")
                .missionContent("테스트 미션 내용")
                .missionGoalCount(100)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(6))
                .createdAt(LocalDateTime.now())
                .build();
        testMission = missionRepository.save(testMission);
    }

    @Test
    @DisplayName("미션 답글 등록 성공")
    void createMissionReply_Success() {
        // given
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .replyContent("이 미션에 참여하고 싶습니다!")
                .userId(testUser.getUserId())
                .build();

        // when
        MissionReplyResDTO response = missionService.createMissionReply(testMission.getMissionCode(), request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getReplyContent()).isEqualTo("이 미션에 참여하고 싶습니다!");
        assertThat(response.getMissionCode()).isEqualTo(testMission.getMissionCode());
        assertThat(response.getUserId()).isEqualTo(testUser.getUserId());
        assertThat(response.getUserNickname()).isEqualTo("테스트유저");
        assertThat(response.getReplyId()).isNotNull();
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 미션으로 답글 등록 실패")
    void createMissionReply_MissionNotFound() {
        // given
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .replyContent("답글 내용")
                .userId(testUser.getUserId())
                .build();

        // when & then
        assertThatThrownBy(() -> missionService.createMissionReply(999L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("미션을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 답글 등록 실패")
    void createMissionReply_UserNotFound() {
        // given
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .replyContent("답글 내용")
                .userId(999L)
                .build();

        // when & then
        assertThatThrownBy(() -> missionService.createMissionReply(testMission.getMissionCode(), request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("답글 내용이 null일 때 예외 발생")
    void createMissionReply_NullContent() {
        // given
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .replyContent(null)
                .userId(testUser.getUserId())
                .build();

        // when & then
        assertThatThrownBy(() -> missionService.createMissionReply(testMission.getMissionCode(), request))
                .isInstanceOf(RuntimeException.class);
    }
}
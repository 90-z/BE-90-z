package com.be90z.domain.mission.controller;

import com.be90z.domain.challenge.entity.Challenge;
import com.be90z.domain.challenge.repository.ChallengeRepository;
import com.be90z.domain.mission.dto.request.MissionJoinReqDTO;
import com.be90z.domain.mission.entity.Mission;
import com.be90z.domain.mission.entity.MissionStatus;
import com.be90z.domain.mission.repository.MissionRepository;
import com.be90z.domain.user.entity.Gender;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.user.entity.UserAuthority;
import com.be90z.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("MissionController 통합 테스트")
class MissionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private MissionRepository missionRepository;

    private User testUser;
    private Challenge testChallenge;
    private Mission testMission;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .provider("kakao")
                .nickname("통합테스트유저")
                .email("integration@example.com")
                .auth(UserAuthority.USER)
                .gender(Gender.MAN)
                .birth(1990)
                .createdAt(LocalDateTime.now())
                .build();
        testUser = userRepository.save(testUser);

        testChallenge = Challenge.builder()
                .challengeName("물 마시기 챌린지")
                .challengeDescription("매일 물을 마시는 챌린지")
                .startDate(LocalDateTime.now().minusDays(5))
                .endDate(LocalDateTime.now().plusDays(25))
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();
        testChallenge = challengeRepository.save(testChallenge);

        testMission = Mission.builder()
                .missionCode(1L)
                .challenge(testChallenge)
                .missionName("매일 물 8잔 마시기")
                .missionContent("하루에 물 8잔을 마시고 인증샷을 올려주세요")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(100)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(6))
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        testMission = missionRepository.save(testMission);
    }

    @Test
    @WithMockUser
    @DisplayName("실제 DB 연동 - 활성 미션 목록 조회 성공")
    void getActiveMissions_WithRealDB_ReturnsActiveMissions() throws Exception {
        // when & then
        mockMvc.perform(get("/api/mission/list")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].missionCode").value(testMission.getMissionCode()))
                .andExpect(jsonPath("$[0].missionName").value("매일 물 8잔 마시기"))
                .andExpect(jsonPath("$[0].missionStatus").value("ACTIVE"))
                .andExpect(jsonPath("$[0].currentParticipants").value(0));
    }

    @Test
    @WithMockUser
    @DisplayName("실제 DB 연동 - 미션 참여 성공")
    void joinMission_WithRealDB_ReturnsJoinResponse() throws Exception {
        // given
        MissionJoinReqDTO request = MissionJoinReqDTO.builder()
                .userId(testUser.getUserId())
                .missionCode(testMission.getMissionCode())
                .build();

        // when & then
        mockMvc.perform(post("/api/mission/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.participateCode").exists())
                .andExpect(jsonPath("$.message").value("미션 참여가 완료되었습니다."))
                .andExpect(jsonPath("$.participateStatus").value("PART_BEFORE"));
    }

    @Test
    @WithMockUser
    @DisplayName("실제 DB 연동 - 존재하지 않는 사용자로 미션 참여 시 400 에러")
    void joinMission_WithRealDB_UserNotFound() throws Exception {
        // given
        MissionJoinReqDTO request = MissionJoinReqDTO.builder()
                .userId(99999L)
                .missionCode(testMission.getMissionCode())
                .build();

        // when & then
        mockMvc.perform(post("/api/mission/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not found with id: 99999"))
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"));
    }

    @Test
    @WithMockUser
    @DisplayName("실제 DB 연동 - 존재하지 않는 미션 참여 시 400 에러")
    void joinMission_WithRealDB_MissionNotFound() throws Exception {
        // given
        MissionJoinReqDTO request = MissionJoinReqDTO.builder()
                .userId(testUser.getUserId())
                .missionCode(99999L)
                .build();

        // when & then
        mockMvc.perform(post("/api/mission/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Mission not found with code: 99999"))
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"));
    }

    @Test
    @WithMockUser
    @DisplayName("실제 DB 연동 - 이미 참여한 미션 재참여 시 400 에러")
    void joinMission_WithRealDB_AlreadyParticipated() throws Exception {
        // given - 먼저 미션에 참여
        MissionJoinReqDTO request = MissionJoinReqDTO.builder()
                .userId(testUser.getUserId())
                .missionCode(testMission.getMissionCode())
                .build();

        mockMvc.perform(post("/api/mission/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk());

        // when & then - 동일한 미션에 다시 참여 시도
        mockMvc.perform(post("/api/mission/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User already participated in this mission"))
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"));
    }

    @Test
    @WithMockUser
    @DisplayName("실제 DB 연동 - 미션 상태 변경 성공")
    void updateMissionStatus_WithRealDB_ReturnsUpdatedStatus() throws Exception {
        // given - 먼저 미션에 참여
        MissionJoinReqDTO joinRequest = MissionJoinReqDTO.builder()
                .userId(testUser.getUserId())
                .missionCode(testMission.getMissionCode())
                .build();

        mockMvc.perform(post("/api/mission/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest))
                        .with(csrf()))
                .andExpect(status().isOk());

        // when & then - 상태 변경
        mockMvc.perform(patch("/api/mission/status")
                        .param("userId", testUser.getUserId().toString())
                        .param("missionCode", testMission.getMissionCode().toString())
                        .param("status", "PART_COMPLETE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("PART_COMPLETE"));
    }

    @Test
    @WithMockUser
    @DisplayName("실제 DB 연동 - 참여하지 않은 미션 상태 변경 시 400 에러")
    void updateMissionStatus_WithRealDB_NotParticipated() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/mission/status")
                        .param("userId", testUser.getUserId().toString())
                        .param("missionCode", testMission.getMissionCode().toString())
                        .param("status", "PART_COMPLETE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not participated in this mission"))
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"));
    }

    @Test
    @WithMockUser
    @DisplayName("실제 DB 연동 - 필수 파라미터 누락으로 400 에러")
    void updateMissionStatus_WithRealDB_MissingParameter() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/mission/status")
                        .param("userId", testUser.getUserId().toString())
                        .param("missionCode", testMission.getMissionCode().toString())
                        // status 파라미터 누락
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Required parameter is missing: status"))
                .andExpect(jsonPath("$.code").value("MISSING_PARAMETER"));
    }
}
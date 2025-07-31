package com.be90z.domain.mission.controller;

import com.be90z.domain.mission.dto.request.MissionJoinReqDTO;
import com.be90z.domain.mission.dto.response.MissionJoinResDTO;
import com.be90z.domain.mission.dto.response.MissionListResDTO;
import com.be90z.domain.mission.entity.MissionStatus;
import com.be90z.domain.mission.service.MissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MissionController.class)
@DisplayName("MissionController 테스트")
class MissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MissionService missionService;

    @Test
    @WithMockUser
    @DisplayName("활성 미션 목록 조회 - 정상 케이스")
    void getActiveMissions_ReturnsActiveMissions() throws Exception {
        // given
        MissionListResDTO mission1 = MissionListResDTO.builder()
                .missionCode(1L)
                .missionName("매일 물 8잔 마시기")
                .missionContent("하루에 물 8잔을 마시고 인증샷을 올려주세요")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(100)
                .startDate(LocalDateTime.of(2025, 7, 1, 0, 0))
                .endDate(LocalDateTime.of(2025, 7, 31, 23, 59))
                .currentParticipants(45)
                .build();

        MissionListResDTO mission2 = MissionListResDTO.builder()
                .missionCode(2L)
                .missionName("운동하기")
                .missionContent("매일 30분 운동하기")
                .missionStatus(MissionStatus.ACTIVE)
                .missionMax(50)
                .startDate(LocalDateTime.of(2025, 7, 1, 0, 0))
                .endDate(LocalDateTime.of(2025, 7, 31, 23, 59))
                .currentParticipants(20)
                .build();

        List<MissionListResDTO> mockResponse = List.of(mission1, mission2);
        given(missionService.getAllActiveMissions()).willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/v1/mission")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].missionCode").value(1))
                .andExpect(jsonPath("$[0].missionName").value("매일 물 8잔 마시기"))
                .andExpect(jsonPath("$[0].currentParticipants").value(45))
                .andExpect(jsonPath("$[1].missionCode").value(2))
                .andExpect(jsonPath("$[1].missionName").value("운동하기"))
                .andExpect(jsonPath("$[1].currentParticipants").value(20));
    }

    @Test
    @WithMockUser
    @DisplayName("미션 참여 - 정상 케이스")
    void joinMission_ValidRequest_ReturnsJoinResponse() throws Exception {
        // given
        MissionJoinReqDTO request = MissionJoinReqDTO.builder()
                .userId(1L)
                .missionCode(2L)
                .build();

        MissionJoinResDTO mockResponse = MissionJoinResDTO.builder()
                .participateCode(123L)
                .message("미션 참여가 완료되었습니다.")
                .participateStatus("PART_BEFORE")
                .build();

        given(missionService.joinMission(any(MissionJoinReqDTO.class))).willReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/api/v1/mission/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.participateCode").value(123))
                .andExpect(jsonPath("$.message").value("미션 참여가 완료되었습니다."))
                .andExpect(jsonPath("$.participateStatus").value("PART_BEFORE"));
    }

    @Test
    @WithMockUser
    @DisplayName("미션 참여 - 존재하지 않는 사용자")
    void joinMission_UserNotFound_Returns400() throws Exception {
        // given
        MissionJoinReqDTO request = MissionJoinReqDTO.builder()
                .userId(999L)
                .missionCode(1L)
                .build();

        given(missionService.joinMission(any(MissionJoinReqDTO.class)))
                .willThrow(new IllegalArgumentException("User not found with id: 999"));

        // when & then
        mockMvc.perform(post("/api/v1/mission/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("미션 상태 변경 - 정상 케이스")
    void updateMissionStatus_ValidRequest_ReturnsUpdatedStatus() throws Exception {
        // given
        Long userId = 1L;
        Long missionCode = 2L;
        String newStatus = "PART_COMPLETE";

        given(missionService.updateMissionStatus(userId, missionCode, newStatus))
                .willReturn("PART_COMPLETE");

        // when & then
        mockMvc.perform(patch("/api/v1/mission/status")
                        .param("userId", userId.toString())
                        .param("missionCode", missionCode.toString())
                        .param("status", newStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("PART_COMPLETE"));
    }

    @Test
    @WithMockUser
    @DisplayName("미션 상태 변경 - 참여하지 않은 미션")
    void updateMissionStatus_NotParticipated_Returns400() throws Exception {
        // given
        Long userId = 1L;
        Long missionCode = 999L;
        String newStatus = "PART_COMPLETE";

        given(missionService.updateMissionStatus(userId, missionCode, newStatus))
                .willThrow(new IllegalArgumentException("User not participated in this mission"));

        // when & then
        mockMvc.perform(patch("/api/v1/mission/status")
                        .param("userId", userId.toString())
                        .param("missionCode", missionCode.toString())
                        .param("status", newStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("미션 상태 변경 - 필수 파라미터 누락")
    void updateMissionStatus_MissingParameter_Returns400() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/v1/mission/status")
                        .param("userId", "1")
                        .param("missionCode", "2")
                        // status 파라미터 누락
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
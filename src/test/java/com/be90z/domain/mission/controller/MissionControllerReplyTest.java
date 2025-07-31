package com.be90z.domain.mission.controller;

import com.be90z.domain.mission.dto.request.MissionReplyReqDTO;
import com.be90z.domain.mission.dto.response.MissionReplyResDTO;
import com.be90z.domain.mission.service.MissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MissionController.class)
@DisplayName("MissionController 미션 댓글 API 테스트")
class MissionControllerReplyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MissionService missionService;

    @Test
    @DisplayName("POST /api/v1/mission/{missionCode}/reply - 미션 댓글 등록 성공")
    void shouldReplyToMissionSuccessfully() throws Exception {
        // given
        Long missionCode = 1L;
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .missionName("테스트 미션")
                .missionContent("테스트 미션 내용입니다.")
                .build();

        MissionReplyResDTO response = MissionReplyResDTO.builder()
                .replyCode(100L)
                .missionCode(missionCode)
                .missionName("테스트 미션")
                .missionContent("테스트 미션 내용입니다.")
                .createdAt(LocalDateTime.of(2024, 1, 1, 12, 0, 0))
                .build();

        when(missionService.replyToMission(eq(missionCode), any(MissionReplyReqDTO.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/mission/{missionCode}/reply", missionCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.replyCode").value(100L))
                .andExpect(jsonPath("$.missionCode").value(missionCode))
                .andExpect(jsonPath("$.missionName").value("테스트 미션"))
                .andExpect(jsonPath("$.missionContent").value("테스트 미션 내용입니다."))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("POST /api/v1/mission/{missionCode}/reply - 잘못된 요청 시 400 에러")
    void shouldReturn400WhenInvalidRequest() throws Exception {
        // given
        Long missionCode = 1L;
        MissionReplyReqDTO invalidRequest = MissionReplyReqDTO.builder()
                .missionName("") // 빈 문자열 - 유효성 검증 실패
                .missionContent("테스트 미션 내용")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/mission/{missionCode}/reply", missionCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/mission/{missionCode}/reply - 미션을 찾을 수 없을 때 404 에러")
    void shouldReturn404WhenMissionNotFound() throws Exception {
        // given
        Long nonExistentMissionCode = 999L;
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .missionName("테스트 미션")
                .missionContent("테스트 미션 내용")
                .build();

        when(missionService.replyToMission(eq(nonExistentMissionCode), any(MissionReplyReqDTO.class)))
                .thenThrow(new IllegalArgumentException("Mission not found with code: " + nonExistentMissionCode));

        // when & then
        mockMvc.perform(post("/api/v1/mission/{missionCode}/reply", nonExistentMissionCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/mission/{missionCode}/reply - 서버 에러 시 500 에러")
    void shouldReturn500WhenInternalServerError() throws Exception {
        // given
        Long missionCode = 1L;
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .missionName("테스트 미션")
                .missionContent("테스트 미션 내용")
                .build();

        when(missionService.replyToMission(eq(missionCode), any(MissionReplyReqDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // when & then
        mockMvc.perform(post("/api/v1/mission/{missionCode}/reply", missionCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/v1/mission/{missionCode}/reply - Content-Type이 JSON이 아닐 때 415 에러")
    void shouldReturn415WhenContentTypeIsNotJson() throws Exception {
        // given
        Long missionCode = 1L;
        String requestBody = "missionName=테스트&missionContent=내용";

        // when & then
        mockMvc.perform(post("/api/v1/mission/{missionCode}/reply", missionCode)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(requestBody))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("POST /api/v1/mission/{missionCode}/reply - 요청 바디가 없을 때 400 에러")
    void shouldReturn400WhenRequestBodyIsEmpty() throws Exception {
        // given
        Long missionCode = 1L;

        // when & then
        mockMvc.perform(post("/api/v1/mission/{missionCode}/reply", missionCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/mission/{missionCode}/reply - 잘못된 JSON 형식일 때 400 에러")
    void shouldReturn400WhenInvalidJsonFormat() throws Exception {
        // given
        Long missionCode = 1L;
        String invalidJson = "{ \"missionName\": \"테스트\", \"missionContent\": }"; // 잘못된 JSON

        // when & then
        mockMvc.perform(post("/api/v1/mission/{missionCode}/reply", missionCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/mission/{missionCode}/reply - 미션명 길이 초과 시 400 에러")
    void shouldReturn400WhenMissionNameTooLong() throws Exception {
        // given
        Long missionCode = 1L;
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .missionName("a".repeat(101)) // 100자 초과
                .missionContent("테스트 미션 내용")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/mission/{missionCode}/reply", missionCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/mission/{missionCode}/reply - 미션 내용 길이 초과 시 400 에러")
    void shouldReturn400WhenMissionContentTooLong() throws Exception {
        // given
        Long missionCode = 1L;
        MissionReplyReqDTO request = MissionReplyReqDTO.builder()
                .missionName("테스트 미션")
                .missionContent("a".repeat(501)) // 500자 초과
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/mission/{missionCode}/reply", missionCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
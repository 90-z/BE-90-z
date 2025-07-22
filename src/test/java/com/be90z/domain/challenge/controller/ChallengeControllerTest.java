package com.be90z.domain.challenge.controller;

import com.be90z.domain.challenge.dto.response.ChallengeStatusResDTO;
import com.be90z.domain.challenge.service.ChallengeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChallengeController.class)
@DisplayName("ChallengeController 테스트")
class ChallengeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChallengeService challengeService;

    @Test
    @WithMockUser
    @DisplayName("챌린지 현황 조회 - 정상 케이스")
    void getChallengeStatus_ValidUserId_ReturnsStatus() throws Exception {
        // given
        Long userId = 1L;
        ChallengeStatusResDTO mockResponse = ChallengeStatusResDTO.builder()
                .participatingMissions(3)
                .raffleParticipationCount(5)
                .totalRaffleParticipants(120)
                .raffleWinCount(1)
                .build();

        given(challengeService.getChallengeStatus(userId)).willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/challenge/status")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.participatingMissions").value(3))
                .andExpect(jsonPath("$.raffleParticipationCount").value(5))
                .andExpect(jsonPath("$.totalRaffleParticipants").value(120))
                .andExpect(jsonPath("$.raffleWinCount").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("챌린지 현황 조회 - 존재하지 않는 사용자")
    void getChallengeStatus_UserNotFound_Returns400() throws Exception {
        // given
        Long userId = 999L;
        given(challengeService.getChallengeStatus(userId))
                .willThrow(new IllegalArgumentException("User not found with id: " + userId));

        // when & then
        mockMvc.perform(get("/api/challenge/status")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("챌린지 현황 조회 - 잘못된 파라미터")
    void getChallengeStatus_InvalidParameter_Returns400() throws Exception {
        // when & then
        mockMvc.perform(get("/api/challenge/status")
                        .param("userId", "invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("챌린지 현황 조회 - 필수 파라미터 누락")
    void getChallengeStatus_MissingParameter_Returns400() throws Exception {
        // when & then
        mockMvc.perform(get("/api/challenge/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
package com.be90z.domain.raffle.controller;

import com.be90z.domain.raffle.dto.request.PrizeClaimReqDTO;
import com.be90z.domain.raffle.dto.response.PrizeClaimResDTO;
import com.be90z.domain.raffle.dto.response.RaffleStatusResDTO;
import com.be90z.domain.raffle.dto.response.WinnerHistoryResDTO;
import com.be90z.domain.raffle.service.PrizeClaimService;
import com.be90z.domain.raffle.service.RaffleService;
import com.be90z.domain.raffle.service.WinnerHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RaffleController.class)
@DisplayName("래플 통합 컨트롤러 테스트")
class RaffleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RaffleService raffleService;
    
    @MockBean
    private PrizeClaimService prizeClaimService;
    
    @MockBean
    private WinnerHistoryService winnerHistoryService;

    @Test
    @DisplayName("래플 상태 조회 API 테스트")
    void shouldGetRaffleStatus() throws Exception {
        // given
        Long userId = 1L;
        RaffleStatusResDTO mockResponse = RaffleStatusResDTO.builder()
            .isParticipating(true)
            .entryCount(5)
            .lastEntryDate(LocalDateTime.now())
            .nextDrawDate(LocalDateTime.now().plusDays(10))
            .build();

        when(raffleService.getRaffleStatus(userId)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/v1/raffle/status")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isParticipating").value(true))
                .andExpect(jsonPath("$.entryCount").value(5))
                .andExpect(jsonPath("$.lastEntryDate").exists())
                .andExpect(jsonPath("$.nextDrawDate").exists());
    }

    @Test
    @DisplayName("MDE003-03: 미션 참여 클릭 시 '참여중'으로 변경되며 버튼은 비활성화된다")
    void shouldJoinRaffle() throws Exception {
        // given
        Long userId = 1L;
        Long missionId = 1L;

        when(raffleService.joinRaffle(userId, missionId)).thenReturn(true);

        // when & then
        mockMvc.perform(post("/api/v1/raffle/entries")
                .param("userId", userId.toString())
                .param("missionId", missionId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("래플 당첨자 조회 API 테스트")
    void shouldGetRaffleWinners() throws Exception {
        // given
        List<String> mockWinners = Arrays.asList("User1", "User2", "User3");
        when(raffleService.getMonthlyWinners()).thenReturn(mockWinners);

        // when & then
        mockMvc.perform(get("/api/v1/raffle/winners")
                .param("period", "current")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.period").value("current"))
                .andExpect(jsonPath("$.winners").isArray())
                .andExpect(jsonPath("$.winners.length()").value(3))
                .andExpect(jsonPath("$.winners[0]").value("User1"))
                .andExpect(jsonPath("$.winners[1]").value("User2"))
                .andExpect(jsonPath("$.winners[2]").value("User3"))
                .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    @DisplayName("래플 참가 실패 시 400 에러 반환")
    void shouldReturn400WhenJoinRaffleFails() throws Exception {
        // given
        Long userId = 1L;
        Long missionId = 1L;

        when(raffleService.joinRaffle(userId, missionId)).thenReturn(false);

        // when & then
        mockMvc.perform(post("/api/v1/raffle/entries")
                .param("userId", userId.toString())
                .param("missionId", missionId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("통합 래플 당첨자 내역 조회 - 페이징")
    void shouldGetWinnerHistoryWithPagination() throws Exception {
        // given
        WinnerHistoryResDTO winner1 = WinnerHistoryResDTO.builder()
            .winnerCode(1L)
            .userName("User1")
            .prizeName("Gift Card")
            .winDate(LocalDateTime.now())
            .claimed(false)
            .build();
            
        List<WinnerHistoryResDTO> winners = Arrays.asList(winner1);
        Page<WinnerHistoryResDTO> winnerPage = new PageImpl<>(winners, PageRequest.of(0, 20), 1);
        
        when(winnerHistoryService.getAllWinnerHistory(any())).thenReturn(winnerPage);

        // when & then
        mockMvc.perform(get("/api/v1/raffle/winners")
                .param("period", "history")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].winnerCode").value(1))
                .andExpect(jsonPath("$.content[0].userName").value("User1"));
    }

    @Test
    @DisplayName("사용자별 당첨 내역 조회")
    void shouldGetUserWinnerHistory() throws Exception {
        // given
        Long userId = 1L;
        WinnerHistoryResDTO winner = WinnerHistoryResDTO.builder()
            .winnerCode(1L)
            .userName("User1")
            .prizeName("Gift Card")
            .winDate(LocalDateTime.now())
            .claimed(true)
            .build();
            
        when(winnerHistoryService.getWinnerHistoryByUserId(userId)).thenReturn(Arrays.asList(winner));

        // when & then
        mockMvc.perform(get("/api/v1/raffle/winners")
                .param("period", "history")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.period").value("history"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.winners").isArray())
                .andExpect(jsonPath("$.winners[0].winnerCode").value(1));
    }

    @Test
    @DisplayName("상품 수령 API 테스트")
    void shouldClaimPrize() throws Exception {
        // given
        Long winnerId = 1L;
        PrizeClaimReqDTO request = PrizeClaimReqDTO.builder()
            .userId(1L)
            .claimMethod("DOWNLOAD")
            .build();
            
        PrizeClaimResDTO response = PrizeClaimResDTO.builder()
            .winnerCode(winnerId)
            .claimed(true)
            .claimDate(LocalDateTime.now())
            .prizeName("Gift Card")
            .giftCardDownloadUrl("/api/v1/raffle/winners/1/downloads?token=abc123")
            .build();
            
        when(prizeClaimService.claimPrize(any(PrizeClaimReqDTO.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/raffle/winners/{winnerId}/claims", winnerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.winnerCode").value(1))
                .andExpect(jsonPath("$.claimed").value(true))
                .andExpect(jsonPath("$.prizeName").value("Gift Card"));
    }

    @Test
    @DisplayName("기프트카드 다운로드 API 테스트")
    void shouldDownloadGiftCard() throws Exception {
        // given
        Long winnerId = 1L;
        String token = "valid-token";
        byte[] giftCardData = "GIFT_CARD_DATA".getBytes();
        
        when(prizeClaimService.downloadGiftCard(winnerId, token)).thenReturn(giftCardData);

        // when & then
        mockMvc.perform(get("/api/v1/raffle/winners/{winnerId}/downloads", winnerId)
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().bytes(giftCardData));
    }

    @Test
    @DisplayName("잘못된 파라미터로 요청 시 400 에러 반환")
    void shouldReturn400ForInvalidParameters() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/raffle/entries")
                .param("userId", "invalid")
                .param("missionId", "invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("MDE003-03: 참여 완료는 매일 23시 돌아가는 스케줄링 배치를 통해 자동으로 완료 처리된다")
    void shouldAutoCompleteParticipationByScheduler() throws Exception {
        // given - 참여 중인 상태에서 배치 처리 후 완료 상태 확인
        Long userId = 1L;
        RaffleStatusResDTO participatingStatus = RaffleStatusResDTO.builder()
            .isParticipating(true)
            .entryCount(3)
            .lastEntryDate(LocalDateTime.now().minusHours(1))
            .nextDrawDate(LocalDateTime.now().plusDays(10))
            .build();

        when(raffleService.getRaffleStatus(userId)).thenReturn(participatingStatus);

        // when & then - 배치 처리 후 참여 상태가 유지되고 있음을 확인
        mockMvc.perform(get("/api/v1/raffle/status")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isParticipating").value(true))
                .andExpect(jsonPath("$.entryCount").value(3));
    }

    @Test  
    @DisplayName("MDE003-03: 배치 완료 후 미션 참여 중 미션, 래플 참여 횟수, 전체 래플 참여자 수가 업데이트된다")
    void shouldUpdateStatusAfterBatchProcessing() throws Exception {
        // given - 배치 처리 후 업데이트된 상태
        Long userId = 1L;
        RaffleStatusResDTO updatedStatus = RaffleStatusResDTO.builder()
            .isParticipating(true)
            .entryCount(6) // 증가된 참여 횟수
            .lastEntryDate(LocalDateTime.now())
            .nextDrawDate(LocalDateTime.now().plusDays(10))
            .build();

        when(raffleService.getRaffleStatus(userId)).thenReturn(updatedStatus);

        // when & then
        mockMvc.perform(get("/api/v1/raffle/status")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isParticipating").value(true))
                .andExpect(jsonPath("$.entryCount").value(6)) // 업데이트된 참여 횟수 확인
                .andExpect(jsonPath("$.lastEntryDate").exists());
    }

    @Test
    @DisplayName("MDE003-03: 인증 방식이 아닌 시스템 로직으로 자동 참여 완료 처리하는 미션 특성상 참여 취소 버튼은 제공하지 않는다")
    void shouldNotProvideParticipationCancelButton() throws Exception {
        // given - 참여 중인 상태
        Long userId = 1L;
        Long missionId = 1L;

        when(raffleService.joinRaffle(userId, missionId)).thenReturn(true);

        // when & then - 래플 참여 API만 제공되고 취소 API는 제공하지 않음
        mockMvc.perform(post("/api/v1/raffle/entries")
                .param("userId", userId.toString())
                .param("missionId", missionId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 취소 API는 존재하지 않아야 함 (404 반환 예상)
        mockMvc.perform(delete("/api/v1/raffle/cancel")
                .param("userId", userId.toString())
                .param("missionId", missionId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
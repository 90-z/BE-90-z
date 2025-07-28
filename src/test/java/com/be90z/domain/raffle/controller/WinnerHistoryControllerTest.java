package com.be90z.domain.raffle.controller;

import com.be90z.domain.raffle.dto.response.WinnerHistoryResDTO;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WinnerHistoryController.class)
@DisplayName("래플 당첨내역 조회 컨트롤러 테스트")
class WinnerHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WinnerHistoryService winnerHistoryService;

    @Test
    @DisplayName("전체 당첨내역을 페이징하여 조회할 수 있다")
    void shouldGetAllWinnerHistoryWithPaging() throws Exception {
        // given
        List<WinnerHistoryResDTO> winners = Arrays.asList(
            createWinnerHistoryDTO(1L, "User1", "기프트카드 5000원", false),
            createWinnerHistoryDTO(2L, "User2", "기프트카드 10000원", true)
        );
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<WinnerHistoryResDTO> winnerPage = new PageImpl<>(winners, pageable, 2);
        
        when(winnerHistoryService.getAllWinnerHistory(any(Pageable.class)))
            .thenReturn(winnerPage);

        // when & then
        mockMvc.perform(get("/api/raffle/winners")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].winnerCode").value(1))
                .andExpect(jsonPath("$.content[0].userName").value("User1"))
                .andExpect(jsonPath("$.content[0].prizeName").value("기프트카드 5000원"))
                .andExpect(jsonPath("$.content[0].claimed").value(false))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("특정 사용자의 당첨내역을 조회할 수 있다")
    void shouldGetWinnerHistoryByUserId() throws Exception {
        // given
        Long userId = 1L;
        List<WinnerHistoryResDTO> userWinners = Arrays.asList(
            createWinnerHistoryDTO(1L, "User1", "기프트카드 5000원", false),
            createWinnerHistoryDTO(3L, "User1", "기프트카드 3000원", true)
        );

        when(winnerHistoryService.getWinnerHistoryByUserId(eq(userId)))
            .thenReturn(userWinners);

        // when & then
        mockMvc.perform(get("/api/raffle/winners")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].winnerCode").value(1))
                .andExpect(jsonPath("$[0].userName").value("User1"))
                .andExpect(jsonPath("$[1].winnerCode").value(3))
                .andExpect(jsonPath("$[1].claimed").value(true));
    }

    @Test
    @DisplayName("존재하지 않는 사용자ID로 조회 시 빈 목록을 반환한다")
    void shouldReturnEmptyListForNonExistentUser() throws Exception {
        // given
        Long nonExistentUserId = 999L;
        when(winnerHistoryService.getWinnerHistoryByUserId(eq(nonExistentUserId)))
            .thenReturn(Arrays.asList());

        // when & then
        mockMvc.perform(get("/api/raffle/winners")
                .param("userId", nonExistentUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("잘못된 페이지 파라미터로 요청 시 기본값을 사용한다")
    void shouldUseDefaultPageParametersForInvalidInput() throws Exception {
        // given
        List<WinnerHistoryResDTO> winners = Arrays.asList(
            createWinnerHistoryDTO(1L, "User1", "기프트카드 5000원", false)
        );
        
        Pageable defaultPageable = PageRequest.of(0, 20);
        Page<WinnerHistoryResDTO> winnerPage = new PageImpl<>(winners, defaultPageable, 1);
        
        when(winnerHistoryService.getAllWinnerHistory(any(Pageable.class)))
            .thenReturn(winnerPage);

        // when & then
        mockMvc.perform(get("/api/raffle/winners")
                .param("page", "-1")
                .param("size", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("당첨내역 조회 시 서비스 예외 발생하면 500 에러를 반환한다")
    void shouldReturn500WhenServiceThrowsException() throws Exception {
        // given
        when(winnerHistoryService.getAllWinnerHistory(any(Pageable.class)))
            .thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        // when & then
        mockMvc.perform(get("/api/raffle/winners")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("MDE003-05: 당첨내역에 필수 정보가 모두 포함되어야 한다 (당첨 상품명, 당첨 일시, 보기 버튼)")
    void shouldIncludeAllRequiredWinnerHistoryFields() throws Exception {
        // given - 명세 요구사항: 당첨 상품명, 당첨 일시, 보기 버튼
        LocalDateTime winDate = LocalDateTime.of(2025, 1, 15, 23, 59, 0);
        List<WinnerHistoryResDTO> winners = Arrays.asList(
            WinnerHistoryResDTO.builder()
                .winnerCode(1L)
                .userName("테스트사용자")
                .prizeName("Monthly Raffle Gift Card") // 당첨 상품명
                .winDate(winDate) // 당첨 일시
                .claimed(false) // 보기 버튼 활성화 상태
                .claimDate(null)
                .build()
        );
        
        when(winnerHistoryService.getWinnerHistoryByUserId(eq(1L)))
            .thenReturn(winners);

        // when & then
        mockMvc.perform(get("/api/raffle/winners")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].prizeName").value("Monthly Raffle Gift Card")) // 당첨 상품명 확인
                .andExpect(jsonPath("$[0].winDate").value("2025-01-15T23:59:00")) // 당첨 일시 확인
                .andExpect(jsonPath("$[0].claimed").value(false)) // 보기 버튼 상태 확인
                .andExpect(jsonPath("$[0].winnerCode").exists()); // 당첨 코드로 보기 버튼 구현 가능
    }

    @Test
    @DisplayName("MDE003-05: 당첨되지 않은 사용자에게는 당첨 내역이 보이지 않아야 한다")
    void shouldNotShowWinnerHistoryForNonWinners() throws Exception {
        // given - 명세: 래플에 당첨되지 않은 경우, 당첨 내역이 보이지 않음
        Long nonWinnerUserId = 999L;
        when(winnerHistoryService.getWinnerHistoryByUserId(eq(nonWinnerUserId)))
            .thenReturn(Arrays.asList()); // 빈 목록

        // when & then
        mockMvc.perform(get("/api/raffle/winners")
                .param("userId", nonWinnerUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0)); // 당첨 내역 없음
    }

    @Test
    @DisplayName("MDE003-05: 당첨 내역은 리스트 형태로 제공되어야 한다")
    void shouldProvideWinnerHistoryAsListFormat() throws Exception {
        // given - 명세: 당첨내역 리스트를 보여준다
        List<WinnerHistoryResDTO> multipleWinners = Arrays.asList(
            createWinnerHistoryDTO(1L, "User1", "1월 당첨", false),
            createWinnerHistoryDTO(2L, "User1", "2월 당첨", true),
            createWinnerHistoryDTO(3L, "User1", "3월 당첨", false)
        );
        
        when(winnerHistoryService.getWinnerHistoryByUserId(eq(1L)))
            .thenReturn(multipleWinners);

        // when & then
        mockMvc.perform(get("/api/raffle/winners")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3)) // 리스트 형태
                .andExpect(jsonPath("$[0].prizeName").value("1월 당첨"))
                .andExpect(jsonPath("$[1].prizeName").value("2월 당첨"))
                .andExpect(jsonPath("$[2].prizeName").value("3월 당첨"));
    }

    private WinnerHistoryResDTO createWinnerHistoryDTO(Long winnerCode, String userName, 
                                                      String prizeName, boolean claimed) {
        return WinnerHistoryResDTO.builder()
            .winnerCode(winnerCode)
            .userName(userName)
            .prizeName(prizeName)
            .winDate(LocalDateTime.now())
            .claimed(claimed)
            .claimDate(claimed ? LocalDateTime.now().minusDays(1) : null)
            .build();
    }
}
package com.be90z.domain.raffle.service;

import com.be90z.domain.raffle.dto.response.WinnerHistoryResDTO;
import com.be90z.domain.raffle.entity.RaffleEntry;
import com.be90z.domain.raffle.entity.RaffleWinner;
import com.be90z.domain.raffle.repository.RaffleWinnerRepository;
import com.be90z.domain.user.entity.User;
import com.be90z.domain.mission.entity.MissionParticipation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("당첨내역 조회 서비스 테스트")
class WinnerHistoryServiceTest {

    @Mock
    private RaffleWinnerRepository raffleWinnerRepository;

    @InjectMocks
    private WinnerHistoryService winnerHistoryService;

    private User testUser1;
    private User testUser2;
    private RaffleWinner testWinner1;
    private RaffleWinner testWinner2;
    private RaffleEntry testRaffleEntry1;
    private RaffleEntry testRaffleEntry2;

    @BeforeEach
    void setUp() {
        testUser1 = User.builder()
            .userId(1L)
            .nickname("TestUser1")
            .email("test1@example.com")
            .build();

        testUser2 = User.builder()
            .userId(2L)
            .nickname("TestUser2")
            .email("test2@example.com")
            .build();

        MissionParticipation participation1 = MissionParticipation.builder()
            .user(testUser1)
            .build();

        MissionParticipation participation2 = MissionParticipation.builder()
            .user(testUser2)
            .build();

        testRaffleEntry1 = RaffleEntry.builder()
            .raffleCode(100L)
            .participation(participation1)
            .raffleName("Monthly Raffle 2024-01")
            .rafflePrizeCont("기프트카드 5000원")
            .raffleWinner(2)
            .raffleDate(LocalDateTime.now().plusDays(1))
            .createdAt(LocalDateTime.now().minusDays(7))
            .build();

        testRaffleEntry2 = RaffleEntry.builder()
            .raffleCode(200L)
            .participation(participation2)
            .raffleName("Monthly Raffle 2024-01")
            .rafflePrizeCont("기프트카드 10000원")
            .raffleWinner(2)
            .raffleDate(LocalDateTime.now().plusDays(1))
            .createdAt(LocalDateTime.now().minusDays(5))
            .build();

        testWinner1 = RaffleWinner.builder()
            .winnerCode(1L)
            .winnerPrize("기프트카드 5000원")
            .raffleEntry(testRaffleEntry1)
            .build();

        testWinner2 = RaffleWinner.builder()
            .winnerCode(2L)
            .winnerPrize("기프트카드 10000원")
            .raffleEntry(testRaffleEntry2)
            .build();
    }

    @Test
    @DisplayName("전체 당첨내역을 페이징하여 조회할 수 있다")
    void shouldGetAllWinnerHistoryWithPaging() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<RaffleWinner> winners = Arrays.asList(testWinner1, testWinner2);
        Page<RaffleWinner> winnerPage = new PageImpl<>(winners, pageable, 2);

        when(raffleWinnerRepository.findAllOrderByRaffleDateDesc(any(Pageable.class)))
            .thenReturn(winnerPage);

        // when
        Page<WinnerHistoryResDTO> result = winnerHistoryService.getAllWinnerHistory(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);

        WinnerHistoryResDTO firstWinner = result.getContent().get(0);
        assertThat(firstWinner.getWinnerCode()).isEqualTo(1L);
        assertThat(firstWinner.getUserName()).isEqualTo("TestUser1");
        assertThat(firstWinner.getPrizeName()).isEqualTo("기프트카드 5000원");
        assertThat(firstWinner.isClaimed()).isFalse();
    }

    @Test
    @DisplayName("특정 사용자의 당첨내역을 조회할 수 있다")
    void shouldGetWinnerHistoryByUserId() {
        // given
        Long userId = 1L;
        List<RaffleWinner> userWinners = Arrays.asList(testWinner1);

        when(raffleWinnerRepository.findByRaffleEntry_Participation_User_UserIdOrderByCreatedAtDesc(eq(userId)))
            .thenReturn(userWinners);

        // when
        List<WinnerHistoryResDTO> result = winnerHistoryService.getWinnerHistoryByUserId(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        WinnerHistoryResDTO winnerHistory = result.get(0);
        assertThat(winnerHistory.getWinnerCode()).isEqualTo(1L);
        assertThat(winnerHistory.getUserName()).isEqualTo("TestUser1");
        assertThat(winnerHistory.getPrizeName()).isEqualTo("기프트카드 5000원");
        assertThat(winnerHistory.isClaimed()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 사용자ID로 조회 시 빈 목록을 반환한다")
    void shouldReturnEmptyListForNonExistentUser() {
        // given
        Long nonExistentUserId = 999L;
        when(raffleWinnerRepository.findByRaffleEntry_Participation_User_UserIdOrderByCreatedAtDesc(eq(nonExistentUserId)))
            .thenReturn(Arrays.asList());

        // when
        List<WinnerHistoryResDTO> result = winnerHistoryService.getWinnerHistoryByUserId(nonExistentUserId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("당첨내역 DTO 변환이 올바르게 수행되어야 한다")
    void shouldCorrectlyConvertToWinnerHistoryDTO() {
        // given
        Pageable pageable = PageRequest.of(0, 1);
        List<RaffleWinner> winners = Arrays.asList(testWinner1);
        Page<RaffleWinner> winnerPage = new PageImpl<>(winners, pageable, 1);

        when(raffleWinnerRepository.findAllOrderByRaffleDateDesc(any(Pageable.class)))
            .thenReturn(winnerPage);

        // when
        Page<WinnerHistoryResDTO> result = winnerHistoryService.getAllWinnerHistory(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        
        WinnerHistoryResDTO dto = result.getContent().get(0);
        assertThat(dto.getWinnerCode()).isEqualTo(testWinner1.getWinnerCode());
        assertThat(dto.getUserName()).isEqualTo(testUser1.getNickname());
        assertThat(dto.getPrizeName()).isEqualTo(testWinner1.getWinnerPrize());
        assertThat(dto.getWinDate()).isNotNull();
        assertThat(dto.isClaimed()).isFalse(); // 기본값
        assertThat(dto.getClaimDate()).isNull(); // 미수령 상태
    }

    @Test
    @DisplayName("빈 당첨자 목록 조회 시 빈 페이지를 반환한다")
    void shouldReturnEmptyPageWhenNoWinners() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<RaffleWinner> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(raffleWinnerRepository.findAllOrderByRaffleDateDesc(any(Pageable.class)))
            .thenReturn(emptyPage);

        // when
        Page<WinnerHistoryResDTO> result = winnerHistoryService.getAllWinnerHistory(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
    }

    @Test
    @DisplayName("당첨내역 조회 시 날짜순 정렬이 적용되어야 한다")
    void shouldReturnWinnersOrderedByDate() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        // testWinner2가 더 최근 (minusDays(5)) testWinner1이 더 과거 (minusDays(7))
        List<RaffleWinner> orderedWinners = Arrays.asList(testWinner2, testWinner1);
        Page<RaffleWinner> winnerPage = new PageImpl<>(orderedWinners, pageable, 2);

        when(raffleWinnerRepository.findAllOrderByRaffleDateDesc(any(Pageable.class)))
            .thenReturn(winnerPage);

        // when
        Page<WinnerHistoryResDTO> result = winnerHistoryService.getAllWinnerHistory(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        
        // 첫 번째가 더 최근 당첨자
        assertThat(result.getContent().get(0).getWinnerCode()).isEqualTo(2L);
        assertThat(result.getContent().get(0).getUserName()).isEqualTo("TestUser2");
        
        // 두 번째가 더 과거 당첨자
        assertThat(result.getContent().get(1).getWinnerCode()).isEqualTo(1L);
        assertThat(result.getContent().get(1).getUserName()).isEqualTo("TestUser1");
    }
}
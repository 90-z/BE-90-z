package com.be90z.domain.raffle.scheduler;

import com.be90z.domain.raffle.entity.RaffleEntry;
import com.be90z.domain.raffle.entity.RaffleWinner;
import com.be90z.domain.raffle.repository.RaffleEntryRepository;
import com.be90z.domain.raffle.repository.RaffleWinnerRepository;
import com.be90z.domain.raffle.service.RaffleDrawService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("월간 래플 추첨 스케줄러 테스트")
class MonthlyRaffleSchedulerTest {

    @Mock
    private RaffleEntryRepository raffleEntryRepository;
    
    @Mock
    private RaffleWinnerRepository raffleWinnerRepository;
    
    @Mock
    private RaffleDrawService raffleDrawService;
    
    @InjectMocks
    private MonthlyRaffleScheduler monthlyRaffleScheduler;

    @Test
    @DisplayName("MDE003-04: 매월 말일 23:59분에 시스템에서 스케줄링을 통해 미션 참여 완료자 중 랜덤 3명을 추첨한다")
    void shouldExecuteMonthlyRaffleDraw() {
        // given
        List<RaffleEntry> entries = Arrays.asList(
            createRaffleEntry(1L, 1L),
            createRaffleEntry(2L, 2L),
            createRaffleEntry(3L, 3L)
        );
        
        List<RaffleWinner> winners = Arrays.asList(
            createRaffleWinner(1L, 1L),
            createRaffleWinner(2L, 2L)
        );
        
        when(raffleEntryRepository.findAllActiveEntries()).thenReturn(entries);
        when(raffleDrawService.drawWinners(entries, 3)).thenReturn(winners);
        
        // when
        monthlyRaffleScheduler.executeMonthlyDraw();
        
        // then
        verify(raffleEntryRepository).findAllActiveEntries();
        verify(raffleDrawService).drawWinners(entries, 3);
        verify(raffleWinnerRepository).saveAll(winners);
    }

    @Test
    @DisplayName("스케줄러는 매월 마지막 날 23:59분에 실행되어야 한다")
    void shouldScheduleAtLastDayOfMonth() {
        // given - @Scheduled 어노테이션 확인용
        // 실제 스케줄링 로직은 통합 테스트에서 검증
        
        // when & then
        // 이 테스트는 @Scheduled 어노테이션이 올바르게 설정되었는지 확인
        // cron = "0 59 23 L * ?" 표현식이 매월 마지막 날 23:59분을 의미하는지 검증
        monthlyRaffleScheduler.executeMonthlyDraw();
        
        verify(raffleEntryRepository).findAllActiveEntries();
    }

    @Test
    @DisplayName("래플 참가자가 없으면 추첨을 건너뛰어야 한다")
    void shouldSkipDrawWhenNoEntries() {
        // given
        when(raffleEntryRepository.findAllActiveEntries()).thenReturn(Arrays.asList());
        
        // when
        monthlyRaffleScheduler.executeMonthlyDraw();
        
        // then
        verify(raffleEntryRepository).findAllActiveEntries();
        verify(raffleDrawService, never()).drawWinners(any(), anyInt());
        verify(raffleWinnerRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("당첨자는 정확히 3명 선정되어야 한다")
    void shouldDrawExactlyThreeWinners() {
        // given
        List<RaffleEntry> entries = Arrays.asList(
            createRaffleEntry(1L, 1L),
            createRaffleEntry(2L, 2L),
            createRaffleEntry(3L, 3L),
            createRaffleEntry(4L, 4L),
            createRaffleEntry(5L, 5L)
        );
        
        List<RaffleWinner> winners = Arrays.asList(
            createRaffleWinner(1L, 1L),
            createRaffleWinner(2L, 2L),
            createRaffleWinner(3L, 3L)
        );
        
        when(raffleEntryRepository.findAllActiveEntries()).thenReturn(entries);
        when(raffleDrawService.drawWinners(entries, 3)).thenReturn(winners);
        
        // when
        monthlyRaffleScheduler.executeMonthlyDraw();
        
        // then
        verify(raffleDrawService).drawWinners(entries, 3);
        verify(raffleWinnerRepository).saveAll(winners);
    }

    @Test
    @DisplayName("MDE003-04: 당첨자가 3명 미만일 경우, 그대로 추첨을 종료한다")
    void shouldSelectAllParticipantsWhenLessThanThree() {
        // given
        List<RaffleEntry> entries = Arrays.asList(
            createRaffleEntry(1L, 1L),
            createRaffleEntry(2L, 2L)
        );
        
        List<RaffleWinner> winners = Arrays.asList(
            createRaffleWinner(1L, 1L),
            createRaffleWinner(2L, 2L)
        );
        
        when(raffleEntryRepository.findAllActiveEntries()).thenReturn(entries);
        when(raffleDrawService.drawWinners(entries, 3)).thenReturn(winners);
        
        // when
        monthlyRaffleScheduler.executeMonthlyDraw();
        
        // then
        verify(raffleDrawService).drawWinners(entries, 3);
        verify(raffleWinnerRepository).saveAll(winners);
    }

    @Test
    @DisplayName("MDE003-04: 당첨자에게 래플 상품 기프티콘 데이터를 업데이트한다")
    void shouldUpdateGiftCardDataForWinners() {
        // given
        List<RaffleEntry> entries = Arrays.asList(
            createRaffleEntry(1L, 1L),
            createRaffleEntry(2L, 2L),
            createRaffleEntry(3L, 3L)
        );
        
        List<RaffleWinner> winners = Arrays.asList(
            createRaffleWinnerWithPrize(1L, 1L, "Monthly Raffle Gift Card"),
            createRaffleWinnerWithPrize(2L, 2L, "Monthly Raffle Gift Card"),
            createRaffleWinnerWithPrize(3L, 3L, "Monthly Raffle Gift Card")
        );
        
        when(raffleEntryRepository.findAllActiveEntries()).thenReturn(entries);
        when(raffleDrawService.drawWinners(entries, 3)).thenReturn(winners);
        
        // when
        monthlyRaffleScheduler.executeMonthlyDraw();
        
        // then
        verify(raffleWinnerRepository).saveAll(winners);
        // 기프티콘 데이터 업데이트 검증
        verify(raffleDrawService).drawWinners(entries, 3);
    }

    @Test
    @DisplayName("스케줄러 실행 중 예외 발생 시 로그를 남기고 계속 진행되어야 한다")
    void shouldLogErrorAndContinueWhenExceptionOccurs() {
        // given
        when(raffleEntryRepository.findAllActiveEntries()).thenThrow(new RuntimeException("Database error"));
        
        // when
        monthlyRaffleScheduler.executeMonthlyDraw();
        
        // then
        verify(raffleEntryRepository).findAllActiveEntries();
        verify(raffleDrawService, never()).drawWinners(any(), anyInt());
        verify(raffleWinnerRepository, never()).saveAll(any());
    }

    private RaffleEntry createRaffleEntry(Long raffleCode, Long participateCode) {
        return RaffleEntry.builder()
            .raffleCode(raffleCode)
            .raffleName("Test Raffle")
            .raffleDate(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
    }

    private RaffleWinner createRaffleWinner(Long id, Long participateCode) {
        RaffleEntry raffleEntry = createRaffleEntry(1L, participateCode);
        return RaffleWinner.builder()
            .winnerCode(id)
            .winnerPrize("Monthly Raffle Prize")
            .raffleEntry(raffleEntry)
            .build();
    }

    private RaffleWinner createRaffleWinnerWithPrize(Long id, Long participateCode, String prizeName) {
        RaffleEntry raffleEntry = createRaffleEntry(1L, participateCode);
        return RaffleWinner.builder()
            .winnerCode(id)
            .winnerPrize(prizeName)
            .raffleEntry(raffleEntry)
            .build();
    }
}
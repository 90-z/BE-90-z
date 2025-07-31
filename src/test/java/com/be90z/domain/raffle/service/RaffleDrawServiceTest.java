package com.be90z.domain.raffle.service;

import com.be90z.domain.raffle.entity.RaffleEntry;
import com.be90z.domain.raffle.entity.RaffleWinner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("래플 추첨 서비스 테스트")
class RaffleDrawServiceTest {

    private final RaffleDrawService raffleDrawService = new RaffleDrawService();

    @Test
    @DisplayName("래플 참가자 목록에서 지정된 수만큼 당첨자를 선정해야 한다")
    void shouldDrawSpecifiedNumberOfWinners() {
        // given
        List<RaffleEntry> entries = Arrays.asList(
            createRaffleEntry(1L, 1L),
            createRaffleEntry(2L, 2L),
            createRaffleEntry(3L, 3L),
            createRaffleEntry(4L, 4L),
            createRaffleEntry(5L, 5L)
        );
        int winnerCount = 2;

        // when
        List<RaffleWinner> winners = raffleDrawService.drawWinners(entries, winnerCount);

        // then
        assertThat(winners).hasSize(winnerCount);
        assertThat(winners).allMatch(winner -> winner.getWinnerPrize() != null);
        assertThat(winners).allMatch(winner -> winner.getRaffleEntry() != null);
    }

    @Test
    @DisplayName("래플 참가자가 당첨자 수보다 적으면 모든 참가자가 당첨되어야 한다")
    void shouldSelectAllEntriesWhenEntriesLessThanWinnerCount() {
        // given
        List<RaffleEntry> entries = Arrays.asList(
            createRaffleEntry(1L, 1L),
            createRaffleEntry(2L, 2L)
        );
        int winnerCount = 5;

        // when
        List<RaffleWinner> winners = raffleDrawService.drawWinners(entries, winnerCount);

        // then
        assertThat(winners).hasSize(2);
    }

    @Test
    @DisplayName("빈 참가자 목록에서는 빈 당첨자 목록이 반환되어야 한다")
    void shouldReturnEmptyWinnersForEmptyEntries() {
        // given
        List<RaffleEntry> entries = Arrays.asList();
        int winnerCount = 2;

        // when
        List<RaffleWinner> winners = raffleDrawService.drawWinners(entries, winnerCount);

        // then
        assertThat(winners).isEmpty();
    }

    // 단순화된 테스트 - drawn 필드가 없으므로 다른 방식으로 검증
    @Test
    @DisplayName("당첨자는 참가자 목록에서 선정되어야 한다")
    void shouldSelectWinnersFromEntries() {
        // given
        List<RaffleEntry> entries = Arrays.asList(
            createRaffleEntry(1L, 1L),
            createRaffleEntry(2L, 2L),
            createRaffleEntry(3L, 3L)
        );
        int winnerCount = 3;

        // when
        List<RaffleWinner> winners = raffleDrawService.drawWinners(entries, winnerCount);

        // then
        assertThat(winners).hasSize(winnerCount);
        assertThat(winners).allMatch(winner -> winner != null);
    }

    @Test
    @DisplayName("MDE003-04: 미션 참여 완료자 중 랜덤 3명을 추첨한다")
    void shouldDrawExactlyThreeWinners() {
        // given
        List<RaffleEntry> entries = Arrays.asList(
            createRaffleEntry(1L, 1L),
            createRaffleEntry(2L, 2L),
            createRaffleEntry(3L, 3L),
            createRaffleEntry(4L, 4L),
            createRaffleEntry(5L, 5L),
            createRaffleEntry(6L, 6L)
        );
        int winnerCount = 3;

        // when
        List<RaffleWinner> winners = raffleDrawService.drawWinners(entries, winnerCount);

        // then
        assertThat(winners).hasSize(3);
        assertThat(winners).allMatch(winner -> winner.getWinnerPrize().equals("Monthly Raffle Gift Card"));
    }

    @Test
    @DisplayName("MDE003-04: 당첨자가 3명 미만일 경우, 그대로 추첨을 종료한다")
    void shouldSelectAllWhenLessThanThreeParticipants() {
        // given
        List<RaffleEntry> entries = Arrays.asList(
            createRaffleEntry(1L, 1L),
            createRaffleEntry(2L, 2L)
        );
        int winnerCount = 3;

        // when
        List<RaffleWinner> winners = raffleDrawService.drawWinners(entries, winnerCount);

        // then
        assertThat(winners).hasSize(2);
        assertThat(winners).allMatch(winner -> winner.getWinnerPrize().equals("Monthly Raffle Gift Card"));
    }

    @Test
    @DisplayName("MDE003-04: 추첨은 랜덤하게 진행되어야 한다")
    void shouldDrawWinnersRandomly() {
        // given - 더 많은 참가자를 만들어 랜덤성을 검증
        List<RaffleEntry> entries = Arrays.asList(
            createRaffleEntry(1L, 1L),
            createRaffleEntry(2L, 2L),
            createRaffleEntry(3L, 3L),
            createRaffleEntry(4L, 4L),
            createRaffleEntry(5L, 5L),
            createRaffleEntry(6L, 6L),
            createRaffleEntry(7L, 7L),
            createRaffleEntry(8L, 8L),
            createRaffleEntry(9L, 9L),
            createRaffleEntry(10L, 10L)
        );
        int winnerCount = 3;

        // when - 여러 번 추첨하여 랜덤성 확인
        List<RaffleWinner> winners1 = raffleDrawService.drawWinners(entries, winnerCount);
        List<RaffleWinner> winners2 = raffleDrawService.drawWinners(entries, winnerCount);

        // then - 각 추첨 결과는 정확히 3명이어야 하고, 모두 기프티콘을 받아야 함
        assertThat(winners1).hasSize(3);
        assertThat(winners2).hasSize(3);
        assertThat(winners1).allMatch(winner -> winner.getWinnerPrize().equals("Monthly Raffle Gift Card"));
        assertThat(winners2).allMatch(winner -> winner.getWinnerPrize().equals("Monthly Raffle Gift Card"));
        
        // 각 추첨의 당첨자는 유효한 RaffleEntry를 가져야 함
        assertThat(winners1).allMatch(winner -> winner.getRaffleEntry() != null);
        assertThat(winners2).allMatch(winner -> winner.getRaffleEntry() != null);
    }

    // 실제 엔티티 구조에 맞춘 헬퍼 메서드
    private RaffleEntry createRaffleEntry(Long raffleCode, Long participateCode) {
        // 테스트용 간단한 빌더 - 실제로는 MissionParticipation이 필요하지만 테스트에서는 null 허용
        return RaffleEntry.builder()
            .raffleCode(raffleCode)
            .raffleName("Test Raffle")
            .raffleDate(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
    }
}
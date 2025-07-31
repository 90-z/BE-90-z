package com.be90z.domain.raffle.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SimpleRaffle 엔티티 테스트")
class SimpleRaffleTest {

    @Test
    @DisplayName("SimpleRaffle 엔티티 생성 테스트")
    void createSimpleRaffle() {
        // given
        String raffleName = "월간 래플";
        String rafflePrizeCont = "스타벅스 아메리카노";
        Integer raffleWinner = 3;
        LocalDateTime raffleDate = LocalDateTime.now();

        // when
        SimpleRaffle raffle = SimpleRaffle.builder()
                .raffleName(raffleName)
                .rafflePrizeCont(rafflePrizeCont)
                .raffleWinner(raffleWinner)
                .raffleDate(raffleDate)
                .build();

        // then
        assertThat(raffle.getRaffleName()).isEqualTo(raffleName);
        assertThat(raffle.getRafflePrizeCont()).isEqualTo(rafflePrizeCont);
        assertThat(raffle.getRaffleWinner()).isEqualTo(raffleWinner);
        assertThat(raffle.getRaffleDate()).isEqualTo(raffleDate);
        assertThat(raffle.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("SimpleRaffle 엔티티 기본값 테스트")
    void createSimpleRaffleWithDefaults() {
        // given
        String raffleName = "월간 래플";
        LocalDateTime raffleDate = LocalDateTime.now();

        // when
        SimpleRaffle raffle = SimpleRaffle.builder()
                .raffleName(raffleName)
                .raffleDate(raffleDate)
                .build();

        // then
        assertThat(raffle.getRaffleWinner()).isEqualTo(1); // 기본값
        assertThat(raffle.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("SimpleRaffle 엔티티 필수 필드 검증 - raffleName")
    void validateRaffleNameRequired() {
        // when & then
        assertThatThrownBy(() -> {
            SimpleRaffle.builder()
                    .raffleDate(LocalDateTime.now())
                    // raffleName 누락
                    .build();
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Raffle name cannot be null");
    }

    @Test
    @DisplayName("SimpleRaffle 엔티티 필수 필드 검증 - raffleDate")
    void validateRaffleDateRequired() {
        // when & then
        assertThatThrownBy(() -> {
            SimpleRaffle.builder()
                    .raffleName("월간 래플")
                    // raffleDate 누락
                    .build();
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Raffle date cannot be null");
    }
}
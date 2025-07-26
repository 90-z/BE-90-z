package com.be90z.domain.challenge.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class ChallengeTest {

    @Test
    @DisplayName("챌린지 생성 시 필수 값이 설정되어야 한다")
    void createChallenge() {
        // given
        String challengeName = "다이어트 챌린지";
        String challengeDescription = "건강한 다이어트를 위한 챌린지";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(30);
        
        // when
        Challenge challenge = Challenge.builder()
                .challengeName(challengeName)
                .challengeDescription(challengeDescription)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        
        // then
        assertThat(challenge.getChallengeName()).isEqualTo(challengeName);
        assertThat(challenge.getChallengeDescription()).isEqualTo(challengeDescription);
        assertThat(challenge.getStartDate()).isEqualTo(startDate);
        assertThat(challenge.getEndDate()).isEqualTo(endDate);
        assertThat(challenge.getChallengeStatus()).isEqualTo(ChallengeStatus.ACTIVE);
    }

    @Test
    @DisplayName("챌린지명이 null일 경우 예외가 발생한다")
    void createChallengeWithNullName() {
        // given & when & then
        assertThatThrownBy(() -> Challenge.builder()
                .challengeName(null)
                .challengeDescription("설명")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Challenge name cannot be null");
    }

    @Test
    @DisplayName("챌린지 설명이 null일 경우 예외가 발생한다")
    void createChallengeWithNullDescription() {
        // given & when & then
        assertThatThrownBy(() -> Challenge.builder()
                .challengeName("챌린지명")
                .challengeDescription(null)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Challenge description cannot be null");
    }

    @Test
    @DisplayName("시작일이 null일 경우 예외가 발생한다")
    void createChallengeWithNullStartDate() {
        // given & when & then
        assertThatThrownBy(() -> Challenge.builder()
                .challengeName("챌린지명")
                .challengeDescription("설명")
                .startDate(null)
                .endDate(LocalDateTime.now().plusDays(30))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start date cannot be null");
    }

    @Test
    @DisplayName("종료일이 null일 경우 예외가 발생한다")
    void createChallengeWithNullEndDate() {
        // given & when & then
        assertThatThrownBy(() -> Challenge.builder()
                .challengeName("챌린지명")
                .challengeDescription("설명")
                .startDate(LocalDateTime.now())
                .endDate(null)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("End date cannot be null");
    }
}
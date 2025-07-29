//package com.be90z.domain.challenge.dto.response;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import static org.assertj.core.api.Assertions.*;
//
//class ChallengeStatusResDTOTest {
//
//    @Test
//    @DisplayName("챌린지 현황 응답 DTO 생성 테스트")
//    void createChallengeStatusResDTO() {
//        // given
//        int participatingMissions = 3;
//        int raffleParticipationCount = 5;
//        int totalRaffleParticipants = 150;
//        int raffleWinCount = 1;
//
//        // when
//        ChallengeStatusResDTO dto = ChallengeStatusResDTO.builder()
//                .participatingMissions(participatingMissions)
//                .raffleParticipationCount(raffleParticipationCount)
//                .totalRaffleParticipants(totalRaffleParticipants)
//                .raffleWinCount(raffleWinCount)
//                .build();
//
//        // then
//        assertThat(dto.getParticipatingMissions()).isEqualTo(participatingMissions);
//        assertThat(dto.getRaffleParticipationCount()).isEqualTo(raffleParticipationCount);
//        assertThat(dto.getTotalRaffleParticipants()).isEqualTo(totalRaffleParticipants);
//        assertThat(dto.getRaffleWinCount()).isEqualTo(raffleWinCount);
//    }
//
//    @Test
//    @DisplayName("챌린지 현황 DTO 기본값 테스트")
//    void createChallengeStatusResDTOWithDefaults() {
//        // given & when
//        ChallengeStatusResDTO dto = ChallengeStatusResDTO.builder().build();
//
//        // then
//        assertThat(dto.getParticipatingMissions()).isEqualTo(0);
//        assertThat(dto.getRaffleParticipationCount()).isEqualTo(0);
//        assertThat(dto.getTotalRaffleParticipants()).isEqualTo(0);
//        assertThat(dto.getRaffleWinCount()).isEqualTo(0);
//    }
//}
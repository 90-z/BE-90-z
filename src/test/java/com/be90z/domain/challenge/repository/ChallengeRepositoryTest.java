package com.be90z.domain.challenge.repository;

import com.be90z.domain.challenge.entity.Challenge;
import com.be90z.domain.challenge.entity.ChallengeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ChallengeRepositoryTest {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Test
    @DisplayName("챌린지 상태로 조회")
    void findByChallengeStatus() {
        // given
        Challenge activeChallenge = Challenge.builder()
                .challengeName("활성 챌린지")
                .challengeDescription("활성 상태의 챌린지")
                .challengeStatus(ChallengeStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();
        
        Challenge completedChallenge = Challenge.builder()
                .challengeName("완료 챌린지")
                .challengeDescription("완료된 챌린지")
                .challengeStatus(ChallengeStatus.COMPLETED)
                .startDate(LocalDateTime.now().minusDays(30))
                .endDate(LocalDateTime.now().minusDays(1))
                .build();
        
        challengeRepository.save(activeChallenge);
        challengeRepository.save(completedChallenge);
        
        // when
        List<Challenge> activeChallenges = challengeRepository.findByChallengeStatus(ChallengeStatus.ACTIVE);
        
        // then
        assertThat(activeChallenges).hasSize(1);
        assertThat(activeChallenges.get(0).getChallengeName()).isEqualTo("활성 챌린지");
    }

    @Test
    @DisplayName("현재 진행 중인 활성 챌린지 조회")
    void findActiveChallengesAtDate() {
        // given
        LocalDateTime now = LocalDateTime.now();
        
        Challenge currentChallenge = Challenge.builder()
                .challengeName("현재 진행 중 챌린지")
                .challengeDescription("현재 진행 중인 챌린지")
                .challengeStatus(ChallengeStatus.ACTIVE)
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(29))
                .build();
        
        Challenge futureChallenge = Challenge.builder()
                .challengeName("미래 챌린지")
                .challengeDescription("미래의 챌린지")
                .challengeStatus(ChallengeStatus.ACTIVE)
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(31))
                .build();
        
        challengeRepository.save(currentChallenge);
        challengeRepository.save(futureChallenge);
        
        // when
        List<Challenge> activeChallenges = challengeRepository.findActiveChallengesAtDate(now, ChallengeStatus.ACTIVE);
        
        // then
        assertThat(activeChallenges).hasSize(1);
        assertThat(activeChallenges.get(0).getChallengeName()).isEqualTo("현재 진행 중 챌린지");
    }
}
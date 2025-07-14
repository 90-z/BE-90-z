package com.be90z.domain.challenge.repository;

import com.be90z.domain.challenge.entity.Challenge;
import com.be90z.domain.challenge.entity.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    
    List<Challenge> findByChallengeStatus(ChallengeStatus status);
    
    @Query("SELECT c FROM Challenge c WHERE c.startDate <= :now AND c.endDate >= :now AND c.challengeStatus = :status")
    List<Challenge> findActiveChallengesAtDate(LocalDateTime now, ChallengeStatus status);
    
    @Query("SELECT c FROM Challenge c WHERE c.endDate < :now AND c.challengeStatus = 'ACTIVE'")
    List<Challenge> findExpiredActiveChallenges(LocalDateTime now);
}
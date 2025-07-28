package com.be90z.domain.raffle.repository;

import com.be90z.domain.raffle.entity.RaffleEntry;
import com.be90z.domain.raffle.entity.RaffleEntryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RaffleEntryRepository extends JpaRepository<RaffleEntry, RaffleEntryId> {
    
    List<RaffleEntry> findByRaffleCode(Long raffleCode);
    
    @Query("SELECT re FROM RaffleEntry re WHERE re.raffleDate BETWEEN :startDate AND :endDate")
    List<RaffleEntry> findByRaffleDateBetween(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT re FROM RaffleEntry re ORDER BY re.raffleDate DESC")
    List<RaffleEntry> findAllOrderByRaffleDateDesc();
    
    // TDD - 사용자별 래플 참여 횟수 조회
    @Query("SELECT COUNT(re) FROM RaffleEntry re WHERE re.participation.user.userId = :userId")
    int countByParticipation_User_UserId(@Param("userId") Long userId);
    
    // TDD - 날짜 범위별 중복 제거된 래플 참여자 수 조회
    @Query("SELECT COUNT(DISTINCT re.participation.user.userId) FROM RaffleEntry re WHERE re.createdAt BETWEEN :startDate AND :endDate")
    int countDistinctByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // 월간 래플 추첨을 위한 활성 참가자 조회
    @Query("SELECT re FROM RaffleEntry re WHERE re.raffleDate <= CURRENT_TIMESTAMP")
    List<RaffleEntry> findAllActiveEntries();
    
    
    // 사용자의 마지막 래플 참가 정보 조회
    Optional<RaffleEntry> findTopByParticipation_User_UserIdOrderByCreatedAtDesc(Long userId);
}
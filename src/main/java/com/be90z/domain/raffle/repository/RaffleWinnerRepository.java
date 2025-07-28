package com.be90z.domain.raffle.repository;

import com.be90z.domain.raffle.entity.RaffleWinner;
import com.be90z.domain.raffle.entity.RaffleEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaffleWinnerRepository extends JpaRepository<RaffleWinner, Long> {
    
    List<RaffleWinner> findByRaffleEntry(RaffleEntry raffleEntry);
    
    @Query("SELECT rw FROM RaffleWinner rw WHERE rw.raffleEntry.raffleCode = :raffleCode")
    List<RaffleWinner> findByRaffleEntry_RaffleCode(@Param("raffleCode") Long raffleCode);
    
    @Query("SELECT rw FROM RaffleWinner rw ORDER BY rw.raffleEntry.raffleDate DESC")
    List<RaffleWinner> findAllOrderByRaffleDateDesc();
    
    // TDD - 사용자별 래플 당첨 횟수 조회
    @Query("SELECT COUNT(rw) FROM RaffleWinner rw WHERE rw.raffleEntry.participation.user.userId = :userId")
    int countByRaffleEntry_Participation_User_UserId(@Param("userId") Long userId);
    
    // 이번 달 당첨자 조회
    @Query("SELECT rw FROM RaffleWinner rw WHERE YEAR(rw.raffleEntry.createdAt) = YEAR(CURRENT_DATE) AND MONTH(rw.raffleEntry.createdAt) = MONTH(CURRENT_DATE)")
    List<RaffleWinner> findThisMonthWinners();
    
    // 페이징된 전체 당첨자 조회 (날짜순 정렬)
    @Query("SELECT rw FROM RaffleWinner rw ORDER BY rw.raffleEntry.raffleDate DESC")
    Page<RaffleWinner> findAllOrderByRaffleDateDesc(Pageable pageable);
    
    // 특정 사용자의 당첨내역 조회 (생성일자순 정렬)
    @Query("SELECT rw FROM RaffleWinner rw WHERE rw.raffleEntry.participation.user.userId = :userId ORDER BY rw.raffleEntry.createdAt DESC")
    List<RaffleWinner> findByRaffleEntry_Participation_User_UserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
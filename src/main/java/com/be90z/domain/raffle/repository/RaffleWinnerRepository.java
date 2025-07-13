package com.be90z.domain.raffle.repository;

import com.be90z.domain.raffle.entity.RaffleWinner;
import com.be90z.domain.raffle.entity.RaffleEntry;
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
}
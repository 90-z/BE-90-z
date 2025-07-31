package com.be90z.domain.raffle.repository;

import com.be90z.domain.raffle.entity.Raffle;
import com.be90z.domain.raffle.entity.RaffleWinner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaffleWinnerRepository extends JpaRepository<RaffleWinner, Long> {
    
    List<RaffleWinner> findByRaffle(Raffle raffle);
    
    @Query("SELECT rw FROM RaffleWinner rw WHERE rw.raffle.raffleCode = :raffleCode")
    List<RaffleWinner> findByRaffleCode(@Param("raffleCode") Long raffleCode);
    
    @Query("SELECT rw FROM RaffleWinner rw WHERE rw.raffle.participation.user.userId = :userId")
    List<RaffleWinner> findByUserId(@Param("userId") Long userId);
}
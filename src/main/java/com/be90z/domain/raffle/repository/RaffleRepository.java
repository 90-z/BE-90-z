package com.be90z.domain.raffle.repository;

import com.be90z.domain.raffle.entity.Raffle;
import com.be90z.domain.raffle.entity.RaffleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RaffleRepository extends JpaRepository<Raffle, RaffleId> {
    
    List<Raffle> findByRaffleCode(Long raffleCode);
    
    @Query("SELECT r FROM Raffle r WHERE r.raffleDate BETWEEN :startDate AND :endDate")
    List<Raffle> findByRaffleDateBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(r) FROM Raffle r WHERE r.participation.user.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
package com.be90z.domain.raffle.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "raffle_winner")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RaffleWinner {
    
    @Id
    @Column(name = "winner_code")
    private Long winnerCode;
    
    @Column(name = "winner_prize", nullable = false)
    private String winnerPrize;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "raffle_code", referencedColumnName = "raffle_code"),
        @JoinColumn(name = "participate_code", referencedColumnName = "participate_code")
    })
    private RaffleEntry raffleEntry;
    
    @Builder
    public RaffleWinner(Long winnerCode, String winnerPrize, RaffleEntry raffleEntry) {
        if (winnerPrize == null) {
            throw new IllegalArgumentException("Winner prize cannot be null");
        }
        
        this.winnerCode = winnerCode;
        this.winnerPrize = winnerPrize;
        this.raffleEntry = raffleEntry;
    }
}
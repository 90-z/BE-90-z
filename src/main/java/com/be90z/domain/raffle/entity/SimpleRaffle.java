package com.be90z.domain.raffle.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDateTime;

@Entity
@Table(name = "simple_raffle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SimpleRaffle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "raffle_name", nullable = false)
    private String raffleName;

    @Column(name = "raffle_prize_cont")
    private String rafflePrizeCont;

    @Column(name = "raffle_winner", nullable = false)
    private Integer raffleWinner = 1;

    @Column(name = "raffle_date", nullable = false)
    private LocalDateTime raffleDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public SimpleRaffle(String raffleName, String rafflePrizeCont, Integer raffleWinner, 
                        LocalDateTime raffleDate, LocalDateTime createdAt) {
        if (raffleName == null) {
            throw new IllegalArgumentException("Raffle name cannot be null");
        }
        if (raffleDate == null) {
            throw new IllegalArgumentException("Raffle date cannot be null");
        }

        this.raffleName = raffleName;
        this.rafflePrizeCont = rafflePrizeCont;
        this.raffleWinner = raffleWinner != null ? raffleWinner : 1;
        this.raffleDate = raffleDate;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
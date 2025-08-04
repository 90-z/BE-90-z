package com.be90z.domain.raffle.entity;

import com.be90z.domain.mission.entity.MissionParticipation;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDateTime;

@Entity
@Table(name = "raffle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(RaffleId.class)
public class Raffle {

    @Id
    @Column(name = "raffle_code")
    private Long raffleCode;

    @Id
    @Column(name = "participate_code")
    private Long participateCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participate_code", insertable = false, updatable = false)
    private MissionParticipation participation;

    @Column(name = "raffle_name", nullable = false)
    private String raffleName;

    @Column(name = "raffle_date", nullable = false)
    private LocalDateTime raffleDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Raffle(Long raffleCode, MissionParticipation participation, String raffleName, 
                  LocalDateTime raffleDate, LocalDateTime createdAt) {
        if (raffleCode == null) {
            throw new IllegalArgumentException("Raffle code cannot be null");
        }
        if (participation == null) {
            throw new IllegalArgumentException("Participation cannot be null");
        }
        if (raffleName == null) {
            throw new IllegalArgumentException("Raffle name cannot be null");
        }
        if (raffleDate == null) {
            throw new IllegalArgumentException("Raffle date cannot be null");
        }

        this.raffleCode = raffleCode;
        this.participateCode = participation.getParticipateCode();
        this.participation = participation;
        this.raffleName = raffleName;
        this.raffleDate = raffleDate;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
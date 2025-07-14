package com.be90z.domain.challenge.entity;

import com.be90z.domain.mission.entity.Mission;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Challenge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long challengeId;
    
    @Column(name = "challenge_name", nullable = false)
    private String challengeName;
    
    @Column(name = "challenge_description", nullable = false, columnDefinition = "TEXT")
    private String challengeDescription;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "challenge_status", nullable = false)
    private ChallengeStatus challengeStatus;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mission> missions = new ArrayList<>();
    
    @Builder
    public Challenge(Long challengeId, String challengeName, String challengeDescription,
                    ChallengeStatus challengeStatus, LocalDateTime startDate, 
                    LocalDateTime endDate, LocalDateTime createdAt) {
        if (challengeName == null) {
            throw new IllegalArgumentException("Challenge name cannot be null");
        }
        if (challengeDescription == null) {
            throw new IllegalArgumentException("Challenge description cannot be null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        
        this.challengeId = challengeId;
        this.challengeName = challengeName;
        this.challengeDescription = challengeDescription;
        this.challengeStatus = challengeStatus != null ? challengeStatus : ChallengeStatus.ACTIVE;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
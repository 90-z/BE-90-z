package com.be90z.domain.mission.entity;

import com.be90z.domain.challenge.entity.Challenge;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDateTime;

@Entity
@Table(name = "mission")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mission {
    
    @Id
    @Column(name = "mission_code")
    private Long missionCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;
    
    @Column(name = "mission_name", nullable = false)
    private String missionName;
    
    @Column(name = "mission_content", nullable = false, columnDefinition = "TEXT")
    private String missionContent;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "mission_status", nullable = false)
    private MissionStatus missionStatus;
    
    @Column(name = "mission_max")
    private Integer missionMax;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Builder
    public Mission(Long missionCode, Challenge challenge, String missionName, String missionContent,
                   MissionStatus missionStatus, Integer missionMax, 
                   LocalDateTime startDate, LocalDateTime endDate, LocalDateTime createdAt) {
        if (challenge == null) {
            throw new IllegalArgumentException("Challenge cannot be null");
        }
        if (missionName == null) {
            throw new IllegalArgumentException("Mission name cannot be null");
        }
        if (missionContent == null) {
            throw new IllegalArgumentException("Mission content cannot be null");
        }
        
        this.missionCode = missionCode;
        this.challenge = challenge;
        this.missionName = missionName;
        this.missionContent = missionContent;
        this.missionStatus = missionStatus != null ? missionStatus : MissionStatus.ACTIVE;
        this.missionMax = missionMax;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
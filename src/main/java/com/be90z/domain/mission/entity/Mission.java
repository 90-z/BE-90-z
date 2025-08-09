package com.be90z.domain.mission.entity;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_code")
    private Long missionCode;

    @Column(name = "mission_name", nullable = false)
    private String missionName;
    
    @Column(name = "mission_content", nullable = false, columnDefinition = "TEXT")
    private String missionContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_status", nullable = false)
    private MissionStatus missionStatus = MissionStatus.ACTIVE;
    
    @Column(name = "mission_goal_count")
    private Integer missionGoalCount;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Builder
    public Mission(Long missionCode, String missionName, String missionContent,
                   MissionStatus missionStatus, Integer missionGoalCount, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime createdAt) {
        if (missionName == null) {
            throw new IllegalArgumentException("Mission name cannot be null");
        }
        if (missionContent == null) {
            throw new IllegalArgumentException("Mission content cannot be null");
        }
        
        this.missionCode = missionCode;
        this.missionName = missionName;
        this.missionContent = missionContent;
        this.missionStatus = missionStatus != null ? missionStatus : MissionStatus.ACTIVE;
        this.missionGoalCount = missionGoalCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
    
    public void updateMission(String missionName, String missionContent, 
                             LocalDateTime startDate, LocalDateTime endDate, Integer missionGoalCount) {
        if (missionName != null) {
            this.missionName = missionName;
        }
        if (missionContent != null) {
            this.missionContent = missionContent;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
        if (missionGoalCount != null) {
            this.missionGoalCount = missionGoalCount;
        }
    }
}
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
    
    @Column(name = "mission_content", nullable = false, columnDefinition = "TEXT")
    private String missionContent;
    
    @Column(name = "mission_goal_count", nullable = false)
    private Integer missionGoalCount = 1;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Builder
    public Mission(Long missionCode, String missionContent, Integer missionGoalCount,
                   LocalDateTime startDate, LocalDateTime endDate, LocalDateTime createdAt) {
        if (missionContent == null) {
            throw new IllegalArgumentException("Mission content cannot be null");
        }
        
        this.missionCode = missionCode;
        this.missionContent = missionContent;
        this.missionGoalCount = missionGoalCount != null ? missionGoalCount : 1;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
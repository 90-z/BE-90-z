package com.be90z.domain.mission.entity;

import com.be90z.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "participate")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionParticipation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participate_code")
    private Long participateCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "participate_status", nullable = false)
    private ParticipateStatus participateStatus;
    
    @Column(name = "participate_count", nullable = false)
    private Integer participateCount = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_code", nullable = false)
    private Mission mission;
    
    @Builder
    public MissionParticipation(Long participateCode, ParticipateStatus participateStatus,
                               Integer participateCount, User user, Mission mission) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (mission == null) {
            throw new IllegalArgumentException("Mission cannot be null");
        }
        
        this.participateCode = participateCode;
        this.participateStatus = participateStatus != null ? participateStatus : ParticipateStatus.PART_BEFORE;
        this.participateCount = participateCount != null ? participateCount : 0;
        this.user = user;
        this.mission = mission;
    }
    
    public void updateStatus(ParticipateStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.participateStatus = newStatus;
    }
}
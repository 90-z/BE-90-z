package com.be90z.domain.mission.entity;

import com.be90z.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDateTime;

@Entity
@Table(name = "mission_reply")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionReply {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long replyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_code", nullable = false)
    private Mission mission;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "reply_content", nullable = false, columnDefinition = "TEXT")
    private String replyContent;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Builder
    public MissionReply(Mission mission, User user, String replyContent, LocalDateTime createdAt) {
        if (mission == null) {
            throw new IllegalArgumentException("Mission cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (replyContent == null || replyContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Reply content cannot be null or empty");
        }
        
        this.mission = mission;
        this.user = user;
        this.replyContent = replyContent;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
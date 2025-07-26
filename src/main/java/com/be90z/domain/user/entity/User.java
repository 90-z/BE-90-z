package com.be90z.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
  
    @Column(nullable = false)
    private String provider;
    
    @Column(nullable = false)
    private String nickname;
    
    @Column(nullable = false)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserAuthority auth = UserAuthority.USER;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Builder
    public User(Long userId, String provider, String nickname, String email, 
                UserAuthority auth, LocalDateTime createdAt) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
        if (nickname == null) {
            throw new IllegalArgumentException("Nickname cannot be null");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        
        this.userId = userId;
        this.provider = provider;
        this.nickname = nickname;
        this.email = email;
        this.auth = auth != null ? auth : UserAuthority.USER;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}

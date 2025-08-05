package com.be90z.domain.bookmark.entity;

import com.be90z.domain.recipe.entity.Recipe;
import com.be90z.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name="bookmark")
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="bookmark_code", nullable = false)
    private Long bookmarkCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_code")
    private Recipe recipe;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Bookmark(User user, Recipe recipe) {
        this.user = user;
        this.recipe = recipe;
    }
}

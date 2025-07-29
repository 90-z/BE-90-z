package com.be90z.domain.recipe.entity;

import com.be90z.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_code")
    private Long imgCode;

    @Column(name = "img_name")
    private String imgName;

    @Column(name = "img_type")
    private String imgType;

    @Column(name = "img_size")
    private String imgSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "img_category")
    private ImageCategory imgCategory;

    @Column(name = "img_s3url")
    private String imgS3url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_code")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Image(String imgName, String imgType, String imgSize, ImageCategory imgCategory, String imgS3url, Recipe recipe) {
        this.imgName = imgName;
        this.imgType = imgType;
        this.imgSize = imgSize;
        this.imgCategory = imgCategory;
        this.imgS3url = imgS3url;
        this.recipe = recipe;
    }
}

package com.be90z.domain.tag.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_code", nullable = false)
    private Long tagCode;

    @Column(name = "tag_name", nullable = false, length = 20)
    private String tagName;

    public Tag(String tagName) {
        this.tagName = tagName;
    }
}

package com.be90z.domain.bookmark.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookmarkResDTO {
    private Long recipeCode;
    private String recipeName;
    private String mainImgUrl;
    private LocalDateTime bookmarkDate;
}

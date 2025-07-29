package com.be90z.domain.recipe.controller;

import com.be90z.domain.recipe.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "image", description = "이미지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageController {

    private final ImageService imageService;

    @DeleteMapping("/{imgCode}")
    @Operation(summary = "개별 이미지 삭제", description = "특정 이미지를 개별적으로 삭제")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imgCode) {
        imageService.deleteImage(imgCode);
        return ResponseEntity.ok().build();
    }
}

package com.be90z.domain.recipe.service;

import com.be90z.domain.recipe.entity.Image;
import com.be90z.domain.recipe.entity.ImageCategory;
import com.be90z.domain.recipe.entity.Recipe;
import com.be90z.domain.recipe.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    //    이미지 업로드 - S3 + DB 저장
    @Transactional
    public Image uploadImage(MultipartFile file, Recipe recipe, ImageCategory imageCategory) throws IOException {
//        파일 검증
        validateFile(file);

//        S3에 업로드할 파일명 생성(중복 방지)
        String fileName = generateFileName(file.getOriginalFilename());
        String s3Key = "recipe/" + recipe.getRecipeCode() + "/" + fileName;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

//            s3 url 생성
            String s3Url = generateS3Url(s3Key);

//            DB에 이미지 정보 저장
            Image image = new Image(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    String.valueOf(file.getSize()),
                    imageCategory,
                    s3Url,
                    recipe
            );

            return imageRepository.save(image);

        } catch (Exception e) {
            log.error("이미지 업로드 실패: {} ", e.getMessage());
            throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
        }
    }


    //    이미지 삭제(S3 + DB)
    @Transactional
    public void deleteImage(Long imgCode) {
        Image image = imageRepository.findById(imgCode)
                .orElseThrow(() -> new RuntimeException("삭제할 이미지를 찾을 수 없습니다."));

        try {
//            S3에서 파일 삭제
            String s3Key = extractS3KeyFromUrl(image.getImgS3url());
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

//            DB에서 이미지 정보 삭제
            imageRepository.delete(image);
        } catch (Exception e) {
            log.error("이미지 삭제 실패: {} ", e.getMessage());
            throw new RuntimeException("이미지 삭제에 실패했습니다.", e);
        }
    }

    // 특정 레시피의 특정 이미지들을 일괄 삭제
    @Transactional
    public void deleteImagesByIds(List<Long> imageIds, Long recipeCode) {
        for (Long imageId : imageIds) {
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException("삭제할 이미지를 찾을 수 없습니다: " + imageId));

            // 해당 이미지가 지정된 레시피의 것인지 검증
            if (!image.getRecipe().getRecipeCode().equals(recipeCode)) {
                throw new RuntimeException("해당 레시피의 이미지가 아닙니다: " + imageId);
            }

            deleteImage(imageId);
        }
    }

    //특정 이미지가 특정 레시피에 속하는지 검증
    @Transactional(readOnly = true)
    public boolean isImageBelongsToRecipe(Long imageId, Long recipeCode) {
        return imageRepository.findById(imageId)
                .map(image -> image.getRecipe().getRecipeCode().equals(recipeCode))
                .orElse(false);
    }

    // 레시피의 이미지 개수 조회
    @Transactional(readOnly = true)
    public int getImageCountByRecipe(Long recipeCode) {
        return imageRepository.findByRecipeRecipeCode(recipeCode).size();
    }

    //    레시피 모든 이미지 조회
    @Transactional(readOnly = true)
    public List<Image> getImagesByRecipe(Long recipeCode) {
        return imageRepository.findByRecipeRecipeCode(recipeCode);
    }

    //    레시피 삭제 시 모든 이미지 삭제
    @Transactional
    public void deleteAllImagesByRecipe(Long recipeCode) {
        List<Image> images = imageRepository.findByRecipeRecipeCode(recipeCode);

        for (Image image : images) {
            try {
                String s3Key = extractS3KeyFromUrl(image.getImgS3url());
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build();

                s3Client.deleteObject(deleteObjectRequest);
            } catch (Exception e) {
                log.error("S3 파일 삭제 실패: {} ", e.getMessage());
                throw new RuntimeException("이미지 삭제 실패: {}", e);
            }
        }
        imageRepository.deleteByRecipeRecipeCode(recipeCode);
    }

    //    utility 메서드 =================================================

    private void validateFile(MultipartFile file) {
        if(file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
//        파일 크기 검증(10MB)
        if(file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
        }
//        파일 타입 검증
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")){
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
    }

    private String generateFileName(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }

    private String generateS3Url(String s3Key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, s3Key);
    }

    private String extractS3KeyFromUrl(String imgS3url) {
        // https://bucket.s3.region.amazonaws.com/key 에서 key 부분 추출
        return imgS3url.substring(imgS3url.lastIndexOf(".com/") + 5);
    }





}

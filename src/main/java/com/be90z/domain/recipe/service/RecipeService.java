package com.be90z.domain.recipe.service;

import com.be90z.domain.bookmark.repository.BookmarkRepository;
import com.be90z.domain.recipe.dto.RecipeAiResDTO;
import com.be90z.domain.recipe.dto.RecipeCreateFreeDTO;
import com.be90z.domain.recipe.dto.RecipeResDTO;
import com.be90z.domain.recipe.dto.RecipeUpdateDTO;
import com.be90z.domain.recipe.entity.Image;
import com.be90z.domain.recipe.entity.ImageCategory;
import com.be90z.domain.recipe.entity.Ingredients;
import com.be90z.domain.recipe.entity.Recipe;
import com.be90z.domain.recipe.repository.RecipeRepository;
import com.be90z.domain.recipeTag.dto.RecipeTagResDTO;
import com.be90z.domain.recipeTag.service.RecipeTagService;
import com.be90z.domain.tag.service.TagService;
import com.be90z.domain.user.entity.User;
import com.be90z.external.gemini.service.GeminiResParser;
import com.be90z.external.gemini.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final GeminiService geminiService;
    private final ImageService imageService;
    private final TagService tagService;
    private final GeminiResParser geminiResParser;
    private final RecipeTagService recipeTagService;
    private final BookmarkRepository bookmarkRepository;

    //    레시피 분석 비동기로
    @Transactional
    public Mono<RecipeAiResDTO> createRecipeWithAiAsync(RecipeCreateFreeDTO recipeCreateFreeDTO) throws IOException {
        return geminiService.analyzeRecipeAsync(
                recipeCreateFreeDTO.getRecipeName(),
                recipeCreateFreeDTO.getRecipeContent()
        ).map(geminiResJson -> {
            try {
                return geminiResParser.parseReponse(geminiResJson, RecipeAiResDTO.class);
            } catch (Exception e) {
                throw new RuntimeException("JSON 파싱 실패 : ", e);
            }
        });
    }

    //    레시피 등록 - ai 후
    @Transactional
    public void createRecipe(RecipeAiResDTO recipeAiResDTO, List<MultipartFile> images, User user) throws IOException {

//        요리방식 유효성 검증
        if (recipeAiResDTO.getRecipeCookMethod() != null &&
                !recipeAiResDTO.getRecipeCookMethod().trim().isEmpty() &&
                !tagService.isValidTag(recipeAiResDTO.getRecipeCookMethod())) {
            throw new RuntimeException("유효하지 않는 요리방식입니다.: " + recipeAiResDTO.getRecipeCookMethod());
        }

        Recipe recipe = new Recipe(
                recipeAiResDTO.getRecipeName(),
                recipeAiResDTO.getRecipeContent(),
                recipeAiResDTO.getRecipeCalories(),
                recipeAiResDTO.getRecipeCookMethod(),
                recipeAiResDTO.getRecipePeople(),
                recipeAiResDTO.getRecipeTime(),
                user
        );

        if (recipeAiResDTO.getIngredientsList() != null) {
            for (RecipeAiResDTO.IngredientsDTO ingredientDTO : recipeAiResDTO.getIngredientsList()) {
                Ingredients ingredient = new Ingredients(
                        ingredientDTO.getIngredientName(),
                        ingredientDTO.getIngredientsCount()
                );
                recipe.addIngredient(ingredient);
            }
        }

//        레시피 먼저 저장
        Recipe savedRecipe = recipeRepository.save(recipe);

//        이미지 업로드
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    imageService.uploadImage(image, savedRecipe, ImageCategory.RECIPE);
                }
            }
        }

        recipeRepository.save(recipe);
    }

    //    레시피 전체 조회
    @Transactional(readOnly = true)
    public List<RecipeResDTO> getAllRecipe() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    //    레시피 상세 조회
    @Transactional(readOnly = true)
    public RecipeResDTO getRecipe(Long recipeCode) {
        Recipe recipe = recipeRepository.findById(recipeCode)
                .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다 : " + recipeCode));
        return convertToResponseDTO(recipe);
    }

    // 레시피 수정
    @Transactional
    public void updateRecipe(Long recipeCode, RecipeUpdateDTO recipeUpdateDTO,
                             List<MultipartFile> newImages, List<Long> deleteImageIds) throws IOException {

        Recipe recipe = recipeRepository.findById(recipeCode)
                .orElseThrow(() -> new RuntimeException("수정할 레시피를 찾을 수 없습니다. : " + recipeCode));

        //        요리방식 유효성 검증
        if (recipeUpdateDTO.getRecipeCookMethod() != null &&
                !recipeUpdateDTO.getRecipeCookMethod().trim().isEmpty() &&
                !tagService.isValidTag(recipeUpdateDTO.getRecipeCookMethod())) {
            throw new RuntimeException("유효하지 않는 요리방식입니다.: " + recipeUpdateDTO.getRecipeCookMethod());
        }

        // 1. 레시피 기본 정보 수정
        recipe.updateRecipe(
                recipeUpdateDTO.getRecipeName(),
                recipeUpdateDTO.getRecipeContent(),
                recipeUpdateDTO.getRecipeCalories(),
                recipeUpdateDTO.getRecipeCookMethod(),
                recipeUpdateDTO.getRecipePeople(),
                recipeUpdateDTO.getRecipeTime()
        );

        // 2. 기존 재료들 모두 삭제
        recipe.getIngredients().clear();

        // 3. 새로운 재료 추가
        if (recipeUpdateDTO.getIngredientsList() != null) {
            for (RecipeUpdateDTO.IngredientsDTO ingredientDTO : recipeUpdateDTO.getIngredientsList()) {
                if (ingredientDTO.getIngredientsName() != null &&
                        !ingredientDTO.getIngredientsName().trim().isEmpty() &&
                        ingredientDTO.getIngredientsCount() != null
                        ) {

                    Ingredients ingredients = new Ingredients(
                            ingredientDTO.getIngredientsName().trim(),
                            ingredientDTO.getIngredientsCount()
                    );
                    recipe.addIngredient(ingredients);
                }
            }
        }

        // 4. 지정된 이미지들 삭제
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            for (Long imageId : deleteImageIds) {
                // 해당 이미지가 현재 레시피의 이미지인지 검증
                List<Image> currentImages = imageService.getImagesByRecipe(recipeCode);
                boolean isValidImage = currentImages.stream()
                        .anyMatch(img -> img.getImgCode().equals(imageId));

                if (isValidImage) {
                    imageService.deleteImage(imageId);
                } else {
                    throw new RuntimeException("레시피에 속하지 않는 이미지는 삭제할 수 없습니다: " + imageId);
                }
            }
        }

        // 5. 새로운 이미지 추가
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile image : newImages) {
                if (!image.isEmpty()) {
                    imageService.uploadImage(image, recipe, ImageCategory.RECIPE);
                }
            }
        }

        // 6. 이미지 삭제 후 이미지가 하나도 없는지 확인 (선택사항)
        List<Image> remainingImages = imageService.getImagesByRecipe(recipeCode);
        if (remainingImages.isEmpty() && (newImages == null || newImages.isEmpty())) {
            // 이미지가 하나도 없을 때의 처리 (필요에 따라)
            // throw new RuntimeException("레시피에는 최소 1개 이상의 이미지가 필요합니다.");
        }

        recipeRepository.save(recipe);
    }

    //    레시피 삭제
    @Transactional
    public void deleteRecipe(Long recipeCode) {
        Recipe recipe = recipeRepository.findById(recipeCode).orElseThrow(() -> new RuntimeException("삭제할 레시피를 찾을 수 없습니다. : " + recipeCode));

        recipeTagService.deleteRecipeTags(recipeCode);
        imageService.deleteAllImagesByRecipe(recipeCode);
        recipeRepository.delete(recipe);
    }

    //    Recipe Entity를 ResponseDTO로 변환하는 메서드
    public RecipeResDTO convertToResponseDTO(Recipe recipe) {
        RecipeResDTO recipeResDTO = new RecipeResDTO();
        recipeResDTO.setRecipeCode(recipe.getRecipeCode());
        recipeResDTO.setRecipeName(recipe.getRecipeName());
        recipeResDTO.setRecipeContent(recipe.getRecipeContent());
        recipeResDTO.setRecipeCalories(recipe.getRecipeCalories());
        recipeResDTO.setRecipeCookMethod(recipe.getRecipeCookMethod());
        recipeResDTO.setRecipePeople(recipe.getRecipePeople());
        recipeResDTO.setRecipeTime(recipe.getRecipeTime());
        recipeResDTO.setCreatedAt(recipe.getCreatedAt());

        // 작성자 정보 추가
        if (recipe.getUser() != null) {
            recipeResDTO.setAuthorId(recipe.getUser().getUserId());
            recipeResDTO.setAuthorNickname(recipe.getUser().getNickname());
        }

//        재료 리스트를 DTO 변환
        List<RecipeResDTO.IngredientsResDTO> ingrdientssList = recipe.getIngredients().stream()
                .map(ingredients -> {
                    RecipeResDTO.IngredientsResDTO ingredientDTO = new RecipeResDTO.IngredientsResDTO();
                    ingredientDTO.setIngredientsCode(ingredients.getIngredientsCode());
                    ingredientDTO.setIngredientsName(ingredients.getIngredientsName());
                    ingredientDTO.setIngredientsCount(ingredients.getIngredientsCount());
                    return ingredientDTO;
                })
                .collect(Collectors.toList());

        recipeResDTO.setIngredientsList(ingrdientssList);

        //    이미지 리스트 추가
        List<Image> images = imageService.getImagesByRecipe(recipe.getRecipeCode());
        List<RecipeResDTO.ImageResDTO> imagesList = images.stream()
                .map(image -> {
                    RecipeResDTO.ImageResDTO imageResDTO = new RecipeResDTO.ImageResDTO();
                    imageResDTO.setImgCode(image.getImgCode());
                    imageResDTO.setImgName(image.getImgName());
                    imageResDTO.setImgS3url(image.getImgS3url());
                    return imageResDTO;
                })
                .collect(Collectors.toList());

        recipeResDTO.setImagesList(imagesList);

        // 🔥 레시피 태그 정보 추가
        List<RecipeTagResDTO> recipeTags = recipeTagService.getRecipeTags(recipe.getRecipeCode());
        List<RecipeResDTO.RecipeTagResDTO> recipeTagList = recipeTags.stream()
                .map(tag -> new RecipeResDTO.RecipeTagResDTO(tag.getRecipeTagCode(), tag.getRecipeTagName()))
                .collect(Collectors.toList());

        recipeResDTO.setRecipeTagList(recipeTagList);

        return recipeResDTO;
    }

    //    레시피 검색
//    키워드(제목, 내용)나 재료로 검색
    @Transactional(readOnly = true)
    public List<RecipeResDTO> searchRecipe(String keyword, String ingredient) {
        if ((keyword == null || keyword.isEmpty()) && (ingredient == null || ingredient.isEmpty())) {
            return getAllRecipe();
        }
        List<Recipe> recipes = recipeRepository.searchRecipeByKeywordAndIngredient(keyword != null ? keyword.trim() : null,
                ingredient != null ? ingredient.trim() : null);
        return recipes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

    }

    //    키워드로 검색
    @Transactional(readOnly = true)
    public List<RecipeResDTO> searchRecipeByKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllRecipe();
        }
        List<Recipe> recipes = recipeRepository.searchByRecipeKeyword(keyword);
        return recipes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    //    재료로 검색
    @Transactional(readOnly = true)
    public List<RecipeResDTO> searchRecipeByIngredient(String ingredient) {
        if (ingredient == null || ingredient.trim().isEmpty()) {
            return getAllRecipe();
        }
        List<Recipe> recipes = recipeRepository.searchByIngredient(ingredient.trim());
        return recipes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    //    작성자 권환 확인 메서드
    @Transactional(readOnly = true)
    public boolean isRecipeOwner(Long recipeCode, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeCode)
                .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다.: " + recipeCode));

        boolean isOwner = recipe.getUser().getUserId().equals(userId);
        log.info("작성자 권한 확인 - 레시피: {}, 요청자: {}, 작성자: {}, 권한: {}", recipeCode, userId, recipe.getUser().getUserId(), isOwner);

        return isOwner;
    }
}

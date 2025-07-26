package com.be90z.domain.recipe.service;

import com.be90z.domain.recipe.dto.RecipeAiResDTO;
import com.be90z.domain.recipe.dto.RecipeCreateFreeDTO;
import com.be90z.domain.recipe.dto.RecipeResDTO;
import com.be90z.domain.recipe.dto.RecipeUpdateDTO;
import com.be90z.domain.recipe.entity.Ingredients;
import com.be90z.domain.recipe.entity.Recipe;
import com.be90z.domain.recipe.repository.RecipeRepository;
import com.be90z.external.gemini.service.GeminiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final GeminiService geminiService;

    //   AI로 레시피 분석
    @Transactional
    public RecipeAiResDTO createRecipeWithAi(RecipeCreateFreeDTO recipeCreateFreeDTO) throws IOException {
        String geminiResJson = geminiService.analyzeRecipe(
                recipeCreateFreeDTO.getRecipeName(),
                recipeCreateFreeDTO.getRecipeContent()
        );

//        AI 결과 Json > DTO 변환
        ObjectMapper objectMapper = new ObjectMapper();
        RecipeAiResDTO recipeAiResDTO = objectMapper.readValue(geminiResJson, RecipeAiResDTO.class);
        return recipeAiResDTO;
    }

    //    레시피 등록 - ai 후
    @Transactional
    public void createRecipe(RecipeAiResDTO recipeAiResDTO) {
        Recipe recipe = new Recipe(
                recipeAiResDTO.getRecipeName(),
                recipeAiResDTO.getRecipeContent(),
                recipeAiResDTO.getRecipeCalories(),
                recipeAiResDTO.getRecipeCookMethod(),
                recipeAiResDTO.getRecipePeople(),
                recipeAiResDTO.getRecipeTime()
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
        recipeRepository.save(recipe);
    }

//    레시피 전체 조회
    @Transactional(readOnly = true)
    public List<RecipeResDTO> getAllRecipe() {
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

//    레시피 상세 조회
    @Transactional(readOnly = true)
    public RecipeResDTO getRecipe(Long recipeCode) {
        Recipe recipe = recipeRepository.findById(recipeCode).orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다 : " + recipeCode));
        return convertToResponseDTO(recipe);
    }

//    레시피 수정
    @Transactional
    public void updateRecipe(Long recipeCode, RecipeUpdateDTO recipeUpdateDTO) {
        Recipe recipe = recipeRepository.findById(recipeCode).orElseThrow(() -> new RuntimeException("수정할 레시피를 찾을 수 없습니다. : " + recipeCode));

        recipe.updateRecipe(
                recipeUpdateDTO.getRecipeName(),
                recipeUpdateDTO.getRecipeContent(),
                recipeUpdateDTO.getRecipeCalories(),
                recipeUpdateDTO.getRecipeCookMethod(),
                recipeUpdateDTO.getRecipePeople(),
                recipeUpdateDTO.getRecipeTime()
        );

//        기존 재료들 모두 삭제
        recipe.getIngredients().clear();

//        새로운 재료 추가
        if(recipeUpdateDTO.getIngredientsList() != null) {
            for(RecipeUpdateDTO.IngredientsDTO ingredientDTO : recipeUpdateDTO.getIngredientsList()) {
                if(ingredientDTO.getIngredientsName() != null &&
                        !ingredientDTO.getIngredientsName().trim().isEmpty() &&
                        ingredientDTO.getIngredientsCount() != null &&
                        ingredientDTO.getIngredientsCount() > 0) {

                    Ingredients ingredients = new Ingredients(
                            ingredientDTO.getIngredientsName().trim(),
                            ingredientDTO.getIngredientsCount()
                    );
                    recipe.addIngredient(ingredients);
                }

            }
        }
        recipeRepository.save(recipe);

    }

    //    레시피 삭제
    @Transactional
    public void deleteRecipe(Long recipeCode) {
        Recipe recipe = recipeRepository.findById(recipeCode).orElseThrow(() -> new RuntimeException("삭제할 레시피를 찾을 수 없습니다. : " + recipeCode));
        recipeRepository.delete(recipe);
    }


//    Recipe Entity를 ResponseDTO로 변환하는 메서드
    private RecipeResDTO convertToResponseDTO(Recipe recipe) {
        RecipeResDTO recipeResDTO = new RecipeResDTO();
        recipeResDTO.setRecipeCode(recipe.getRecipeCode());
        recipeResDTO.setRecipeName(recipe.getRecipeName());
        recipeResDTO.setRecipeContent(recipe.getRecipeContent());
        recipeResDTO.setRecipeCalories(recipe.getRecipeCalories());
        recipeResDTO.setRecipeCookMethod(recipe.getRecipeCookMethod());
        recipeResDTO.setRecipePeople(recipe.getRecipePeople());
        recipeResDTO.setRecipeTime(recipe.getRecipeTime());
        recipeResDTO.setCreatedAt(recipe.getCreatedAt());

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
        return recipeResDTO;
    }


}

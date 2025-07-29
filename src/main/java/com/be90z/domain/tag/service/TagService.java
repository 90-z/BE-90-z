package com.be90z.domain.tag.service;

import com.be90z.domain.recipe.entity.Recipe;
import com.be90z.domain.recipe.repository.RecipeRepository;
import com.be90z.domain.tag.dto.TagDTO;
import com.be90z.domain.tag.entity.Tag;
import com.be90z.domain.tag.reposiroty.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final RecipeRepository recipeRepository;

    //    모든 태그(요리 방식) 조회
    @Transactional(readOnly = true)
    public List<TagDTO> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(tag -> new TagDTO(tag.getTagCode(), tag.getTagName()))
                .collect(Collectors.toList());
    }

    //    특정 요리 방식(태그)으로 레시피 조회
    @Transactional(readOnly = true)
    public List<Recipe> getRecipeByTag(String tagName) {
        return recipeRepository.findByRecipeCookMethodIgnoreCase(tagName);
    }

    //    태그 존재 확인
    @Transactional(readOnly = true)
    public boolean isValidTag(String tagName) {
        return tagRepository.findByTagName(tagName).isPresent();
    }
}

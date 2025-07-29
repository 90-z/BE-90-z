package com.be90z.domain.recipeTag.dto;

import lombok.Data;

import java.util.List;

// 사용자가 선택한 재료 3개 요청 DTO
@Data
public class RecipeTagSelectReqDTO {
    private List<String> selectedRecipeTags;

    public boolean isValid() {
        return selectedRecipeTags != null &&
                !selectedRecipeTags.isEmpty() &&
                selectedRecipeTags.size() <= 3;
    }
}

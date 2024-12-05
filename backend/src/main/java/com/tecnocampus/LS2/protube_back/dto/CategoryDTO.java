package com.tecnocampus.LS2.protube_back.dto;

import com.tecnocampus.LS2.protube_back.domain.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    private Long id;
    private String name;

    // Constructor to map from Category entity
    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}

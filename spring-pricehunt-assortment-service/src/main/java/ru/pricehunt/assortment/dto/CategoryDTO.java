package ru.pricehunt.assortment.dto;

import lombok.*;

import java.util.List;

@Data
public class CategoryDTO {
    private String slug;
    private String name;
    private String description;
    private CategoryParentDTO parent;
    private List<CategoryChildDTO> children;
}

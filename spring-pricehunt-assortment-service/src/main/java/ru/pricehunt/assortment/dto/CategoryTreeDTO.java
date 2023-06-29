package ru.pricehunt.assortment.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryTreeDTO {
    private String slug;
    private String name;
    private String description;
    private CategoryParentDTO parent;
    private List<CategoryChildWithChildsDTO> children;
}

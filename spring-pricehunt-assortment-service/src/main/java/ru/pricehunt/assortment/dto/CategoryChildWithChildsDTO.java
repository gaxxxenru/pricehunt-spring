package ru.pricehunt.assortment.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryChildWithChildsDTO {
    private String slug;
    private String name;
    private String description;
    private List<CategoryChildWithChildsDTO> children;
}

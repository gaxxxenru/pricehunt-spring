package ru.pricehunt.pleerru.dto;

import lombok.*;

import java.util.List;


@Data
@Builder
@ToString
public class ProductDTO {
    private String slug;
    private String name;
    private String description;
    private String url;
    private Float price;
    private CategoryDTO category;
    private List<ProductFeatureDTO> features;
    private List<ProductImageDTO> images;
}

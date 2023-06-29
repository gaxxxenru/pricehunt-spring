package ru.pricehunt.assortment.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BestProductDTO {
    private String slug;
    private String name;
    private String description;
    private List<FeatureDTO> features;
    private List<ProductImageDTO> images;
    private CategoryDTO category;
    private BestProductPriceDTO bestProductPrice;
}

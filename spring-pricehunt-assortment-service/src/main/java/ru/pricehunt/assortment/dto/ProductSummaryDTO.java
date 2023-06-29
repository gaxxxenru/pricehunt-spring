package ru.pricehunt.assortment.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductSummaryDTO {
    private String slug;
    private String name;
    private List<ProductImageDTO> images;
    private ProductPriceSummaryDTO bestProductPrice;
}

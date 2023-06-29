package ru.pricehunt.assortment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@ToString
public class ProductKafkaDTO {

    private String slug;
    private String name;
    private String description;
    private String url;
    private BigDecimal price;
    private CategoryDTO category;
    private List<FeatureDTO> features;
    private List<ProductImageDTO> images;


}

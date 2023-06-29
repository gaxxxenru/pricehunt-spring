package ru.pricehunt.assortment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ProductImageDTO {
    private String name;
    private String url;
}


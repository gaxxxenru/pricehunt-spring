package ru.pricehunt.pleerru.dto;


import lombok.*;

@Data
@Builder
@ToString
public class ProductFeatureDTO {
    private String name;
    private String value;
}

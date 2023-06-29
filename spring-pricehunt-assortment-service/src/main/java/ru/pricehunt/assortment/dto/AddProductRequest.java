package ru.pricehunt.assortment.dto;

import lombok.Data;

@Data
public class AddProductRequest {
    private String email;
    private String productSlug;
}

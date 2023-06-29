package ru.pricehunt.assortment.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserProductFavoriteDTO {
    private String userEmail;
    private List<ProductSummaryDTO> products;
}

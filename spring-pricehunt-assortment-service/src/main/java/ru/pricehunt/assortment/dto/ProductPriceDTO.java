package ru.pricehunt.assortment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class ProductPriceDTO {
    private Long id;
    private ProductDTO product;
    private String url;
    private RetailerDTO retailer;
    private BigDecimal price;
    private Date createdAt;
}

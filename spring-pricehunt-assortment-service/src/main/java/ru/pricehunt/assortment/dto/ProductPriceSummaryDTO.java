package ru.pricehunt.assortment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductPriceSummaryDTO {
    private Long id;
    private RetailerSummaryDTO retailer;
    private String url;
    private BigDecimal price;
    private Date createdAt;
}

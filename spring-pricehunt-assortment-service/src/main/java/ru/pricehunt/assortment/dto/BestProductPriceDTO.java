package ru.pricehunt.assortment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BestProductPriceDTO {
    private Long id;
    private RetailerDTO retailer;
    private String url;
    private BigDecimal price;
    private Date createdAt;
}

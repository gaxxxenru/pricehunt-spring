package ru.pricehunt.assortment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class RetailerDTO {
    private String slug;

    private String name;
    private String description;
    private String imageUrl;
    private List<PaymentMethodDTO> paymentMethods;
    private Boolean hasDelivery;
    private Boolean hasPickup;
    private BigDecimal deliveryPrice;
}

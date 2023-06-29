package ru.pricehunt.assortment.dto;


import lombok.Data;

@Data
public class PaymentMethodDTO {
    private String slug;
    private String name;
    private String description;
}

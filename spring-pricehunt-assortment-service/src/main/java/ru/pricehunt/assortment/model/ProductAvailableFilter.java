package ru.pricehunt.assortment.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
public class ProductAvailableFilter {
    private String name;
    private List<String> values;
}

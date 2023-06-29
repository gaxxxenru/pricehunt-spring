package ru.pricehunt.assortment.dto;

import org.springframework.data.domain.Page;
import ru.pricehunt.assortment.model.ProductAvailableFilter;

import java.util.List;

public class ProductPageDTO {
    private Page<ProductDTO> products;
    private List<ProductAvailableFilter> availableFilters;
}

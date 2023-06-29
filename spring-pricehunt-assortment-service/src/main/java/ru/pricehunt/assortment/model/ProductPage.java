package ru.pricehunt.assortment.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ProductPage<T> extends PageImpl<T> {
    @Getter
    @Setter
    private List<ProductAvailableFilter> availableFilters;

    public ProductPage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }


}

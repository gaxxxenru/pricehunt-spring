package ru.pricehunt.assortment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pricehunt.assortment.dto.ProductImageDTO;
import ru.pricehunt.assortment.model.ProductImage;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    ProductImageDTO productImageToProductImageDTO(ProductImage productImage);
    @Mapping(target = "id", ignore = true)
    ProductImage productImageDTOToProductImage(ProductImageDTO productImageDto);

}

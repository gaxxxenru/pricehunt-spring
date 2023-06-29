package ru.pricehunt.assortment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.pricehunt.assortment.dto.BestProductPriceDTO;
import ru.pricehunt.assortment.dto.ProductPriceSummaryDTO;
import ru.pricehunt.assortment.dto.ProductPriceDTO;
import ru.pricehunt.assortment.model.ProductPrice;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RetailerMapper.class, PaymentMethodMapper.class})
public interface ProductPriceMapper {
    @Mapping(target = "product.category.children", ignore = true)
    ProductPriceDTO productPriceToProductPriceDTO(ProductPrice productPrice);
    @Mapping(target = "product.category.children", ignore = true)
    ProductPrice productPriceDTOToProductPrice(ProductPriceDTO productPriceDto);

    @Named("productPriceToProductPriceSummaryDTO")
    ProductPriceSummaryDTO productPriceToProductPriceSummaryDTO(ProductPrice productPrice);

    @Named("productPriceSummaryDTOToProductPrice")
    ProductPrice productPriceSummaryDTOToProductPrice(ProductPriceSummaryDTO productPriceSummaryDto);

    List<ProductPriceSummaryDTO> productPricesToProductPriceSummaryDTOs(List<ProductPrice> productPrices);

    List<BestProductPriceDTO> productPricesToBestProductPriceDTOs(List<ProductPrice> productPrices);

    List<ProductPrice> productPriceSummaryDTOsToProductPrices(List<ProductPriceSummaryDTO> productPriceSummaryDtos);

    List<ProductPriceDTO> productPricesToProductPriceDTOs(List<ProductPrice> productPrices);
    List<ProductPrice> productPriceDTOsToProductPrices(List<ProductPriceDTO> productPriceDtos);
}

package ru.pricehunt.assortment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pricehunt.assortment.dto.*;
import ru.pricehunt.assortment.model.Feature;
import ru.pricehunt.assortment.model.Product;

import java.util.List;


@Mapper(componentModel = "spring", uses = {ProductPriceMapper.class, FeatureMapper.class, ProductImageMapper.class})
public interface ProductMapper {
    @Mapping(target = "bestProductPrice", qualifiedByName = "productPriceToProductPriceSummaryDTO")
    @Mapping(target = "category.children", ignore = true)
    ProductDTO productToProductDTO(Product product);

    @Mapping(target = "bestProductPrice", qualifiedByName = "productPriceSummaryDTOToProductPrice")
    @Mapping(target = "category.children", ignore = true)
    Product productDTOToProduct(ProductDTO productDto);

    @Mapping(target = "category.children", ignore = true)
    BestProductDTO productToBestProductDTO(Product product);

    @Mapping(target = "category.children", ignore = true)
    Product bestProductDTOToProduct(BestProductDTO bestProductDto);

    ProductSummaryDTO productToProductSummaryDTO(Product product);
    Product productSummaryDTOToProduct(ProductSummaryDTO productSummaryDto);

    List<ProductSummaryDTO> productsToProductSummaryDTOs(List<Product> products);

    List<Product> productSummaryDTOsToProducts(List<ProductSummaryDTO> productSummaryDtos);


    List<ProductDTO> productsToProductDTOs(List<Product> products);
    List<Product> productDTOsToProducts(List<ProductDTO> productDtos);
}

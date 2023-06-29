package ru.pricehunt.assortment.productprice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pricehunt.assortment.dto.BestProductPriceDTO;
import ru.pricehunt.assortment.dto.ProductPriceDTO;
import ru.pricehunt.assortment.dto.ProductPriceSummaryDTO;
import ru.pricehunt.assortment.mapper.ProductPriceMapper;
import ru.pricehunt.assortment.model.ProductPrice;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product-price")
@Tag(name = "Product Price Controller", description = "Контроллер для работы с ценами на товары")
public class ProductPriceController {
    private final ProductPriceService productPriceService;
    private final ProductPriceMapper productPriceMapper;

    @GetMapping
    @Operation(summary = "Список всех цен на товары", description = "Возвращает список всех цен на товары")
    public List<ProductPriceSummaryDTO> getAllProductPrices() {
        return productPriceMapper.productPricesToProductPriceSummaryDTOs(productPriceService.findAll());
    }

    @GetMapping("/min/{productSlug}")
    @Operation(summary = "Минимальная цена на товар по productSlug товара", description = "Возвращает минимальную цену на товар по productSlug товара")
    public ProductPriceSummaryDTO getMinProductPriceByProductSlug(@PathVariable String productSlug) {
        return productPriceMapper.productPriceToProductPriceSummaryDTO(productPriceService.findMinProductPriceByProductSlug(productSlug).orElseThrow());
    }

    @GetMapping("/{productSlug}")
    @Operation(summary = "Список цен на товар по productSlug товара", description = "Возвращает список цен на товар по productSlug товара")
    public List<BestProductPriceDTO> getProductPriceByProductSlug(@PathVariable String productSlug) {
        return productPriceMapper.productPricesToBestProductPriceDTOs(productPriceService.findProductPricesByProductSlugWithDetails(productSlug));
    }

    @GetMapping("/{productSlug}/{retailerSlug}")
    @Operation(summary = "Цена на товар по slug товара и slug ритейлера", description = "Возвращает цену на товар по slug товара и slug ритейлера")
    public ProductPriceSummaryDTO getProductPriceByProductSlugAndRetailerSlug(@PathVariable String productSlug, @PathVariable String retailerSlug) {
        return productPriceMapper.productPriceToProductPriceSummaryDTO(productPriceService.findByProductSlugAndRetailerSlugCreatedToday(productSlug, retailerSlug));
    }

    @PutMapping("/{productSlug}/{retailerSlug}")
    @Operation(summary = "Обновление цены на товар по slug товара и slug ритейлера", description = "Обновляет цену на товар по slug товара и slug ритейлера")
    public ProductPriceSummaryDTO updateProductPrice(@PathVariable String productSlug, @PathVariable String retailerSlug, @RequestBody ProductPriceDTO productPriceDTO) {
        ProductPrice productPrice = productPriceMapper.productPriceDTOToProductPrice(productPriceDTO);
        return productPriceMapper.productPriceToProductPriceSummaryDTO(productPriceService.update(productSlug, retailerSlug, productPrice));
    }

    @PostMapping
    @Operation(summary = "Создание цены на товар", description = "Создает цену на товар")
    public ProductPriceSummaryDTO createProductPrice(@RequestBody ProductPriceDTO productPriceDTO) {
        ProductPrice productPrice = productPriceMapper.productPriceDTOToProductPrice(productPriceDTO);
        return productPriceMapper.productPriceToProductPriceSummaryDTO(productPriceService.save(productPrice));
    }


    @DeleteMapping("/{productSlug}/{retailerSlug}")
    @Operation(summary = "Удаление цены на товар по slug товара и slug ритейлера", description = "Удаляет цену на товар по slug товара и slug ритейлера")
    public ProductPriceSummaryDTO deleteProductPrice(@PathVariable String productSlug, @PathVariable String retailerSlug) {
        return productPriceMapper.productPriceToProductPriceSummaryDTO(productPriceService.delete(productSlug, retailerSlug));
    }
}

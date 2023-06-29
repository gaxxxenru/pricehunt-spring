package ru.pricehunt.assortment.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.pricehunt.assortment.dto.BestProductDTO;
import ru.pricehunt.assortment.dto.FeatureDTO;
import ru.pricehunt.assortment.dto.ProductDTO;
import ru.pricehunt.assortment.dto.ProductSummaryDTO;
import ru.pricehunt.assortment.mapper.ProductMapper;
import ru.pricehunt.assortment.model.Feature;
import ru.pricehunt.assortment.model.Product;
import ru.pricehunt.assortment.model.ProductAvailableFilter;
import ru.pricehunt.assortment.model.ProductPage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
@Tag(name = "Product Controller", description = "Контроллер для работы с товарами")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;
    @GetMapping("/page")
    @Operation(summary = "Список всех товаров с пагинацией", description = "Возвращает список всех товаров с пагинацией")
    public Page<ProductSummaryDTO> getAllProductsPageDetails(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "36") int size,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String filters
    ) throws DecoderException, UnsupportedEncodingException {
        List<FeatureDTO> features = convertJsonToListOfFeatures(filters);

        return productService.pageFindAllByCategorySummaryWithFilters(page, size, name, category, features).map(productMapper::productToProductSummaryDTO);
    }

    @GetMapping
    @Operation(summary = "Список всех товаров", description = "Возвращает список всех товаров")
    public List<ProductDTO> getAllProducts() {
        return productMapper.productsToProductDTOs(productService.pageFindAllSummary());
    }

    @GetMapping("/available-filters")
    @Operation(summary = "Список всех доступных фильтров", description = "Возвращает список всех доступных фильтров")
    public List<ProductAvailableFilter> getAvailableFilters(@RequestParam String category) {
        return productService.findAllAvailableFiltersForProductsWithCategory(category);
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Товар по slug", description = "Возвращает товар по slug")
    public BestProductDTO getProductBySlug(@PathVariable String slug) {
        return productMapper.productToBestProductDTO(productService.findBySlug(slug));
    }

    @PutMapping()
    @Operation(summary = "Обновление товара", description = "Обновляет товар по slug")
    public ProductDTO updateProduct(@RequestBody ProductDTO productDTO) {
        Product product = productMapper.productDTOToProduct(productDTO);
        return productMapper.productToProductDTO(productService.update(product));
    }

    @PostMapping
    @Operation(summary = "Создание товара", description = "Создает товар")
    public ProductDTO createProduct(@RequestBody ProductDTO productDTO) {
        Product product = productMapper.productDTOToProduct(productDTO);
        return productMapper.productToProductDTO(productService.save(product));
    }

    @DeleteMapping("/{slug}")
    @Operation(summary = "Удаление товара", description = "Удаляет товар по slug")
    public ProductDTO deleteProduct(@PathVariable String slug) {
        return productMapper.productToProductDTO(productService.delete(slug));
    }

    private List<FeatureDTO> convertJsonToListOfFeatures(String json) throws DecoderException, UnsupportedEncodingException {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }

        URLCodec codec = new URLCodec();
        String decodedString = codec.decode(json, "UTF-8");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(decodedString, new TypeReference<>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}

package ru.pricehunt.assortment.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.pricehunt.assortment.dto.ProductDTO;
import ru.pricehunt.assortment.dto.ProductKafkaDTO;
import ru.pricehunt.assortment.dto.ProductPriceDTO;
import ru.pricehunt.assortment.dto.RetailerDTO;
import ru.pricehunt.assortment.mapper.ProductMapper;
import ru.pricehunt.assortment.mapper.ProductPriceMapper;
import ru.pricehunt.assortment.product.ProductService;
import ru.pricehunt.assortment.productprice.ProductPriceService;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class Consumer {

    @Value("${topic.name}")
    private final String pleerTopic = "${topic.name}";

    private final ObjectMapper objectMapper;
    private final ProductMapper productMapper;
    private final ProductPriceMapper productPriceMapper;

    private final ProductService productService;
    private final ProductPriceService productPriceService;

    @KafkaListener(topics = pleerTopic)
    public void consumeProduct(String message) throws JsonProcessingException {
        log.info("Consumed message: {}", message);
        ProductKafkaDTO productKafkaDTO = objectMapper.readValue(message, ProductKafkaDTO.class);

        ProductDTO productDTO = ProductDTO.builder()
            .slug(productKafkaDTO.getSlug())
            .name(productKafkaDTO.getName())
            .description(productKafkaDTO.getDescription())
            .features(productKafkaDTO.getFeatures())
            .category(productKafkaDTO.getCategory())
            .images(productKafkaDTO.getImages())
            .build();
        if (productService.existsBySlug(productKafkaDTO.getSlug())) {
            log.info("Product already exists: {}", productKafkaDTO.getSlug());
            productService.update(productMapper.productDTOToProduct(productDTO));
        } else
        {
            productService.save(productMapper.productDTOToProduct(productDTO));
        }

//        try {
//            productService.save(productMapper.productDTOToProduct(productDTO));
//            log.info("Product saved: {}", productDTO);
//
//        } catch (Exception e) {
//            log.error("Error while saving product: {}", e.getMessage());
//        }
        if (productPriceService.existsByProductSlugRetailerSlugCreatedDate(productKafkaDTO.getSlug(), "pleer", new Date())) {
            log.info("ProductPrice already exists: {}", productKafkaDTO.getSlug());
            return;
        }

        ProductPriceDTO productPriceDTO = ProductPriceDTO.builder()
            .product(ProductDTO.builder()
                .slug(productKafkaDTO.getSlug())
                .build())
            .price(productKafkaDTO.getPrice())
            .url(productKafkaDTO.getUrl())
            .retailer(RetailerDTO.builder()
                .slug("pleer")
                .build())
            .build();
        productPriceService.save(productPriceMapper.productPriceDTOToProductPrice(productPriceDTO));
    }
}

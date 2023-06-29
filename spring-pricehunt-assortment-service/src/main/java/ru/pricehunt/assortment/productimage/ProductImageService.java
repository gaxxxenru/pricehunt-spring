package ru.pricehunt.assortment.productimage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pricehunt.assortment.model.ProductImage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;

    public ProductImage save(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    public Optional<ProductImage> findByProductSlugAndUrl(String slug, String url) {
        return productImageRepository.findByProductSlugAndUrl(slug, url);
    }
}

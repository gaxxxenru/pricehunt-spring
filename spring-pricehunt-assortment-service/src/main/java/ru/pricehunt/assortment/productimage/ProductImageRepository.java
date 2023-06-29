package ru.pricehunt.assortment.productimage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pricehunt.assortment.model.ProductImage;

import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    Optional<ProductImage> findByProductSlugAndUrl(String slug, String url);

}

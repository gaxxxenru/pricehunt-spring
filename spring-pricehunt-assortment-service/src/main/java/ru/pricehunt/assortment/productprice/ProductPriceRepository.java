package ru.pricehunt.assortment.productprice;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.pricehunt.assortment.model.Product;
import ru.pricehunt.assortment.model.ProductPrice;
import ru.pricehunt.assortment.model.Retailer;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {

    @Query("select distinct p from ProductPrice p LEFT JOIN FETCH p.retailer.paymentMethods where p.product.slug = :productSlug")
    List<ProductPrice> findProductPricesByProductSlug(@Param("productSlug") String productSlug);

    Boolean existsByProductSlugAndRetailerSlugAndCreatedAt(String productSlug, String retailerSlug, Date date);

    @Query("select p from ProductPrice p LEFT JOIN FETCH p.retailer.paymentMethods where p.product.slug = :productSlug and p.createdAt = CURRENT_DATE and p.price = (select min(price) from ProductPrice where product.slug = :productSlug and createdAt = CURRENT_DATE)")
    Optional<ProductPrice> findMinProductPriceByProductSlug(@Param("productSlug") String productSlug);

    Optional<ProductPrice> findProductPriceByProductSlugAndRetailerSlugAndCreatedAt(String productSlug, String retailerSlug, Date date);

}

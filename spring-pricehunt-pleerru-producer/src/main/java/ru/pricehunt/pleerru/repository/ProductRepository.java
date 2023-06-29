package ru.pricehunt.pleerru.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.pricehunt.pleerru.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findBySlug(String slug);

    @Query(value = "SELECT * FROM product WHERE product.last_parsing_date < CURRENT_DATE OR product.last_parsing_date IS NULL", nativeQuery = true)
    List<Product> findProductsNotParsedToday();
}

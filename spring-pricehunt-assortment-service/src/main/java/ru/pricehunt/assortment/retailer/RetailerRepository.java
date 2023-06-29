package ru.pricehunt.assortment.retailer;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.pricehunt.assortment.model.Retailer;

import java.util.List;
import java.util.Optional;

public interface RetailerRepository extends JpaRepository<Retailer, String> {

    @EntityGraph(attributePaths = {"paymentMethods"})
    Optional<Retailer> findBySlug(String slug);

    @NonNull
    @EntityGraph(attributePaths = {"paymentMethods"})
    List<Retailer> findAll();
}

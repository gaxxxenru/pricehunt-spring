package ru.pricehunt.assortment.feature;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pricehunt.assortment.model.Feature;

import java.util.Optional;

public interface FeatureRepository extends JpaRepository<Feature, Long> {
    Optional<Feature> findByNameAndValue(String name, String value);
}

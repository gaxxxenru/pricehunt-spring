package ru.pricehunt.assortment.feature;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pricehunt.assortment.model.Feature;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FeatureService {
    private final FeatureRepository featureRepository;

    public Feature findById(Long id) {
        return featureRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Feature не найден"));
    }

    public Optional<Feature> findByNameAndValue(String name, String value) {
        return featureRepository.findByNameAndValue(name, value);
    }

    @Transactional
    public List<Feature> findAll() {
        return featureRepository.findAll();
    }

    @Transactional
    public Feature save(Feature feature) {
        return featureRepository.save(feature);
    }

    @Transactional
    public Feature update(Long id, Feature feature) {
        Feature existingFeature = featureRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Feature не найден"));
        existingFeature.setName(feature.getName());
        existingFeature.setValue(feature.getValue());
        return featureRepository.save(existingFeature);
    }

    @Transactional
    public void delete(Long id) {
        featureRepository.deleteById(id);
    }
}

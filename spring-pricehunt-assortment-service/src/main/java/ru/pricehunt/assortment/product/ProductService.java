package ru.pricehunt.assortment.product;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pricehunt.assortment.category.CategoryService;
import ru.pricehunt.assortment.dto.FeatureDTO;
import ru.pricehunt.assortment.feature.FeatureService;
import ru.pricehunt.assortment.model.*;
import ru.pricehunt.assortment.productimage.ProductImageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    private final CategoryService categoryService;
    private final FeatureService featureService;
    private final ProductImageService productImageService;

    public Product findBySlug(String slug) {
        return productRepository.findBySlugWithDetails(slug);
    }

    public boolean existsBySlug(String slug) {
        return productRepository.existsBySlug(slug);
    }

    public List<Product> pageFindAllSummary() {
        return productRepository.findAllWithDetails();
    }

//    public Page<Product> pageFindAllSummary(int page, int size, String name, String category) {
//        if (category != null && !category.isEmpty()) {
//            return productRepository.findAllByCategorySummary(PageRequest.of(page, size), category);
//        }
//        if (name == null || name.isEmpty()) {
//            return productRepository.findAllSummary(PageRequest.of(page, size));
//        }
//        return productRepository.findAllByNameFTSSummary(PageRequest.of(page, size), name);
//    }

    public List<ProductAvailableFilter> findAllAvailableFiltersForProductsWithCategory(String categorySlug) {
        return productRepository.findAllAvailableFiltersForProductsWithCategory(categorySlug);
    }

    public Page<Product> pageFindAllByCategorySummaryWithFilters(int page, int size, String name, String category, List<FeatureDTO> features) {
        if (category != null && !category.isEmpty() && features != null && !features.isEmpty()) {
            return productRepository.findAllByCategorySummaryWithFilters(PageRequest.of(page, size, Sort.by("name").ascending()), category, features);
        }
        if (category != null && !category.isEmpty()) {
            return productRepository.findAllByCategorySummary(PageRequest.of(page, size, Sort.by("name").ascending()), category);
        }
        if (name == null || name.isEmpty()) {
            return productRepository.findAllSummary(PageRequest.of(page, size, Sort.by("name").ascending()));
        }
        return productRepository.findAllByNameFTSSummary(PageRequest.of(page, size, Sort.by("name").ascending()), name);
    }


    @Transactional
    public Product update(Product product) {
        Product existingProduct = productRepository.findBySlug(product.getSlug())
            .orElseThrow(() -> new EntityNotFoundException("Product не найден"));

        if (product.getCategory() != null) {
            Category category = categoryService.findBySlug(product.getCategory().getSlug());
            existingProduct.setCategory(category);
        }

        List<Feature> updatedFeatures = new ArrayList<>();
        for (Feature feature : product.getFeatures()) {
            Feature existedOrCreatedFeature = featureService.findByNameAndValue(feature.getName(), feature.getValue()).orElse(null);
            if (existedOrCreatedFeature == null) {
                existedOrCreatedFeature = featureService.save(feature);
            }
            updatedFeatures.add(existedOrCreatedFeature);
        }

        existingProduct.getFeatures().clear();
        existingProduct.setFeatures(updatedFeatures);


        if (product.getFeatures() == null || product.getFeatures().isEmpty()) {
            throw new IllegalArgumentException("Features не могут быть пустыми");
        }

        Optional.ofNullable(product.getName()).ifPresent(existingProduct::setName);
        Optional.ofNullable(product.getDescription()).ifPresent(existingProduct::setDescription);
        Optional.ofNullable(product.getCategory()).ifPresent(existingProduct::setCategory);

        existingProduct.getImages().clear();
        existingProduct.getImages().addAll(product.getImages());
        existingProduct.getImages().forEach(image -> image.setProduct(existingProduct));

        return productRepository.save(existingProduct);
    }

    @Transactional
    public Product save(Product product) {
        if (product.getFeatures() == null || product.getFeatures().isEmpty()) {
            throw new IllegalArgumentException("Features не могут быть пустыми");
        }

        Category category = categoryService.findBySlug(product.getCategory().getSlug());
        product.setCategory(category);
        product.getImages().forEach(image -> image.setProduct(product));


        List<Feature> newFeatures = new ArrayList<>();
        for (Feature feature : product.getFeatures()) {
            Feature existedOrCreatedFeature = featureService.findByNameAndValue(feature.getName(), feature.getValue()).orElse(null);
            if (existedOrCreatedFeature == null) {
                existedOrCreatedFeature = featureService.save(feature);
            }
            newFeatures.add(existedOrCreatedFeature);
        }
        product.getFeatures().clear();
        product.setFeatures(newFeatures);
        return productRepository.save(product);
    }

    @Transactional
    public Product delete(String slug) {
        Product existingProduct = productRepository.findBySlug(slug)
            .orElseThrow(() -> new EntityNotFoundException("Product не найден"));
        productRepository.delete(existingProduct);
        return existingProduct;
    }
}

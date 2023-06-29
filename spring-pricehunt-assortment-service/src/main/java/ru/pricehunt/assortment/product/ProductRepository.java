package ru.pricehunt.assortment.product;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.pricehunt.assortment.dto.FeatureDTO;
import ru.pricehunt.assortment.model.Feature;
import ru.pricehunt.assortment.model.Product;
import ru.pricehunt.assortment.model.ProductAvailableFilter;
import ru.pricehunt.assortment.model.ProductPage;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    default Page<Product> findAllSummary(Pageable pageable) {
        Page<Product> products = findAllByBestProductPriceNotNull(pageable);
        List<Product> productsList = products.getContent();
        if (!products.isEmpty()) {
            productsList = loadProductsWithImages(productsList);
        }

        return new ProductPage<>(productsList, pageable, products.getTotalElements());
    }

    default List<ProductAvailableFilter> findAllAvailableFiltersForProductsWithCategory(String categorySlug) {
        //List<Product> products = findAllByCategorySlug(categorySlug);
        List<Feature> features = findAllFeaturesByCategorySlug(categorySlug);
        Map<String, Set<String>> availableFilters = new HashMap<>();

        if (!features.isEmpty()) {
            for (Feature feature : features) {
                        String featureName = feature.getName();
                        String featureValue = feature.getValue().toLowerCase();
                        availableFilters.computeIfAbsent(featureName, k -> new HashSet<>()).add(featureValue);
            }
            availableFilters.values().removeIf(values -> values.size() < 6);
        }
        return availableFilters.entrySet().stream()
            .map(entry -> {
                ProductAvailableFilter filter = ProductAvailableFilter.builder().build();
                filter.setName(entry.getKey());
                filter.setValues(new ArrayList<>(entry.getValue()));
                return filter;
            })
            .collect(Collectors.toList());

    }
    default Page<Product> findAllByCategorySummary(Pageable pageable, String categorySlug) {
        Page<Product> products = findAllByCategorySlug(categorySlug, pageable);
        List<Product> productsList = products.getContent();
        if (!products.isEmpty()) {
            productsList = loadProductsWithImages(productsList);
        }
        return new ProductPage<>(productsList, pageable, products.getTotalElements());
    }

    default Page<Product> findAllByCategorySummaryWithFilters(Pageable pageable, String categorySlug, List<FeatureDTO> features) {
        List<Product> productsList = findAllByCategorySlug(categorySlug);
        if (!features.isEmpty()) {
            productsList = productsList.stream()
                .filter(product -> product.getFeatures() != null && !product.getFeatures().isEmpty())
                .filter(product -> features.stream().anyMatch(feature ->
                    product.getFeatures().stream().anyMatch(productFeature ->
                        productFeature.getName().equalsIgnoreCase(feature.getName()) &&
                            productFeature.getValue().equalsIgnoreCase(feature.getValue()))))
                .collect(Collectors.toList());
        }
        if (!productsList.isEmpty()) {
            productsList = loadProductsWithImages(productsList);
        }
        return new ProductPage<>(productsList, pageable, productsList.size());
    }
//    default ProductPage<Product> findAllByCategorySummaryWithFilters(Pageable pageable, String categorySlug) {
//        Page<Product> products = findAllByCategorySlug(categorySlug, pageable);
//        List<Product> pageProductsList = products.getContent();
//        List<Product> allProductsList = findAllByCategorySlug(categorySlug);
//        Map<String, Set<String>> availableFilters = new HashMap<>();
//
//        if (!allProductsList.isEmpty()) {
//            allProductsList.stream()
//                .filter(product -> product.getFeatures() != null)
//                .flatMap(product -> product.getFeatures().stream())
//                .forEach(feature -> {
//                    String featureName = feature.getName();
//                    String featureValue = feature.getValue().toLowerCase();
//                    Set<String> values = availableFilters.getOrDefault(featureName, new HashSet<>());
//                    values.add(featureValue);
//                    availableFilters.put(featureName, values);
//                });
//            availableFilters.entrySet().removeIf(entry -> entry.getValue().size() < 6);
//        }
//
//        if (!products.isEmpty()) {
//            pageProductsList = loadProductsWithImages(pageProductsList);
//        }
//
//        List<ProductAvailableFilter> filters = availableFilters.entrySet().stream()
//            .map(entry -> ProductAvailableFilter.builder()
//                .name(entry.getKey())
//                .values(entry.getValue().toArray(new String[0]))
//                .build())
//            .collect(Collectors.toList());
//
//        ProductPage<Product> productPage = new ProductPage<>(pageProductsList, pageable, products.getTotalElements());
//        productPage.setAvailableFilters(filters);
//        return productPage;
//    }

    @Query("SELECT f FROM Product p JOIN p.features f WHERE p IN :products GROUP BY f.name HAVING COUNT(DISTINCT f.value) >= 2")
    List<Feature> findFilteredFeaturesForProducts(List<Product> products);

    @Query("SELECT p.features FROM Product p JOIN p.features f WHERE p IN :products")
    List<Feature> findFeaturesForProducts(List<Product> products);



    Page<Product> findAllByBestProductPriceNotNull(Pageable pageable);

    default List<Product> findAllWithDetails() {
        List<Product> products = findAll();
        if (!products.isEmpty()) {
            products = loadProductsWithRetailerPaymentMethods(products);
            products = loadProductsWithImages(products);
            products = loadProductsWithProductFeatures(products);
        }
        return products;
    }

    default Page<Product> findAllByNameFTSSummary(Pageable pageable, String name) {
        Page<Product> products = findAllByNameContainingIgnoreCaseAndBestProductPriceNotNull(name, pageable);
        List<Product> productsList = products.getContent();
        if (!products.isEmpty()) {
            productsList = loadProductsWithImages(productsList);
        }
        return new ProductPage<>(productsList, pageable, products.getTotalElements());
    }


    default Product findBySlugWithDetails(String slug) {
        Product product = findBySlug(slug).orElseThrow(() -> new EntityNotFoundException("Product не найден"));
        if (product != null) {
            product = loadProductWithRetailerPaymentMethods(product);
            product = loadProductWithImages(product);
            product = loadProductWithProductFeatures(product);
        }
        return product;
    }

    boolean existsBySlug(String slug);


    Page<Product> findAllByNameContainingIgnoreCaseAndBestProductPriceNotNull(String name, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE (c.parent.parent.slug = :categorySlug OR c.parent.slug =:categorySlug OR c.slug = :categorySlug) AND p.bestProductPrice IS NOT NULL")
    Page<Product> findAllByCategorySlug(String categorySlug, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE (c.parent.parent.slug = :categorySlug OR c.parent.slug =:categorySlug OR c.slug = :categorySlug) AND p.bestProductPrice IS NOT NULL")
    List<Product> findAllByCategorySlug(String categorySlug);

    @Query("SELECT p.features FROM Product p JOIN p.category c WHERE (c.parent.parent.slug = :categorySlug OR c.parent.slug =:categorySlug OR c.slug = :categorySlug) AND p.bestProductPrice IS NOT NULL")
    List<Feature> findAllFeaturesByCategorySlug(String categorySlug);


    @Query("SELECT p FROM Product p WHERE p.bestProductPrice IS NOT NULL AND p.features IN :featuresList")
    Page<Product> findAllByFeatures(Pageable pageable, List<Feature> featuresList);


    @NonNull
    @Query("select DISTINCT p from Product p LEFT JOIN FETCH p.category pc LEFT JOIN FETCH pc.parent where p.slug = :slug")
    Optional<Product> findBySlug(@Param("slug") String slug);

    @NonNull
    @Query("select distinct p from Product p left join fetch p.category pc left join fetch pc.parent left join fetch p.bestProductPrice bpp left join fetch bpp.retailer br left join fetch br.paymentMethods where p = :product")
    Product loadProductWithRetailerPaymentMethods(Product product);

    @NonNull
    @Query("select DISTINCT p from Product p LEFT JOIN FETCH p.features where p = :product")
    Product loadProductWithProductFeatures(Product product);

    @NonNull
    @Query("select DISTINCT p from Product p LEFT JOIN FETCH p.images where p = :product")
    Product loadProductWithImages(Product product);

    @NonNull
    @Query("select DISTINCT p from Product p LEFT JOIN FETCH p.category pc LEFT JOIN FETCH pc.parent")
    List<Product> findAll();

    @NonNull
    @Query("select distinct p from Product p left join fetch p.category pc left join fetch pc.parent left join fetch p.bestProductPrice bpp left join fetch bpp.retailer br left join fetch br.paymentMethods where p in :products")
    List<Product> loadProductsWithRetailerPaymentMethods(List<Product> products);

    @NonNull
    @Query("select DISTINCT p from Product p LEFT JOIN FETCH p.features where p in :products")
    List<Product> loadProductsWithProductFeatures(List<Product> products);

    @NonNull
    @Query("select DISTINCT p from Product p LEFT JOIN FETCH p.images where p in :products")
    List<Product> loadProductsWithImages(List<Product> products);

    @NonNull
    @Query("select DISTINCT p from Product p LEFT JOIN FETCH p.category pc LEFT JOIN FETCH pc.parent where p in :products")
    List<Product> loadProductsWithCategories(List<Product> products);
}

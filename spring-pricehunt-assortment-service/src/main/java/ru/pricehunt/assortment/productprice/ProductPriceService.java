package ru.pricehunt.assortment.productprice;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pricehunt.assortment.model.Product;
import ru.pricehunt.assortment.model.ProductPrice;
import ru.pricehunt.assortment.model.Retailer;
import ru.pricehunt.assortment.product.ProductService;
import ru.pricehunt.assortment.retailer.RetailerService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductPriceService {
    private final ProductPriceRepository productPriceRepository;
    private final ProductService productService;
    private final RetailerService retailerService;

    public Boolean existsByProductSlugRetailerSlugCreatedDate(String productSlug, String retailerSlug, Date date) {
        return productPriceRepository.existsByProductSlugAndRetailerSlugAndCreatedAt(productSlug, retailerSlug, date);
    }

    public List<ProductPrice> findProductPricesByProductSlugWithDetails(String productSlug) {
        return productPriceRepository.findProductPricesByProductSlug(productSlug);
    }

    public ProductPrice findByProductSlugAndRetailerSlugCreatedToday(String productSlug, String retailerSlug) {
        return productPriceRepository.findProductPriceByProductSlugAndRetailerSlugAndCreatedAt(productSlug, retailerSlug, new Date())
            .orElseThrow(() -> new EntityNotFoundException("ProductPrice не найден"));
    }

    @Transactional(readOnly = true)
    public List<ProductPrice> findAll() {
        return productPriceRepository.findAll();
    }

    public Optional<ProductPrice> findMinProductPriceByProductSlug(String productSlug) {
        return productPriceRepository.findMinProductPriceByProductSlug(productSlug);
    }

    @Transactional
    public ProductPrice update(String productSlug, String retailerSlug, ProductPrice productPrice) {
        ProductPrice existingProductPrice = findByProductSlugAndRetailerSlugCreatedToday(productSlug, retailerSlug);
        Product product = productService.findBySlug(productSlug);

        if (product.getBestProductPrice() == null ||
            product.getBestProductPrice().getPrice().compareTo(productPrice.getPrice()) > 0 ||
            existingProductPrice.getCreatedAt().before(new Date())) {
            existingProductPrice.setPrice(productPrice.getPrice());
            product.setBestProductPrice(existingProductPrice);
            productService.save(product);
        } else if (product.getBestProductPrice().equals(existingProductPrice) && productPrice.getPrice().compareTo(product.getBestProductPrice().getPrice()) > 0) {
            ProductPrice newBestProductPrice = productPriceRepository.findMinProductPriceByProductSlug(productSlug)
                .orElseThrow(() -> new EntityNotFoundException("ProductPrice не найден"));
            product.setBestProductPrice(newBestProductPrice);
            productService.save(product);
        }

        if (productPrice.getRetailer() != null) {
            Retailer retailer = retailerService.findBySlug(productPrice.getRetailer().getSlug());
            existingProductPrice.setRetailer(retailer);
        }

        if (productPrice.getProduct() != null) {
            existingProductPrice.setProduct(product);
        }

        Optional.ofNullable(productPrice.getPrice()).ifPresent(existingProductPrice::setPrice);

        return productPriceRepository.save(existingProductPrice);
    }
    @Transactional
    public ProductPrice save(ProductPrice productPrice) {
        Product product = productService.findBySlug(productPrice.getProduct().getSlug());
        Retailer retailer = retailerService.findBySlug(productPrice.getRetailer().getSlug());

        productPrice.setProduct(product);
        productPrice.setRetailer(retailer);

        ProductPrice savedBestProductPrice = product.getBestProductPrice();
        if (savedBestProductPrice == null || // if product doesn't have bestProductPrice
            savedBestProductPrice.getPrice().compareTo(productPrice.getPrice()) > 0 || // if productPrice is better than bestProductPrice
            savedBestProductPrice.getCreatedAt().before(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {

            product.setBestProductPrice(productPrice);
            productService.save(product);
        }

        return productPriceRepository.save(productPrice);
    }
    @Transactional
    public ProductPrice delete(String productSlug, String retailerSlug) {
        ProductPrice existingProductPrice = findByProductSlugAndRetailerSlugCreatedToday(productSlug, retailerSlug);
        productPriceRepository.delete(existingProductPrice);
        return existingProductPrice;
    }

}

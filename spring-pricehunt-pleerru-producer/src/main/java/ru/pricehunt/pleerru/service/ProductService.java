package ru.pricehunt.pleerru.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pricehunt.pleerru.dto.ProductDTO;
import ru.pricehunt.pleerru.model.Product;
import ru.pricehunt.pleerru.repository.ProductRepository;
import ru.pricehunt.pleerru.service.producer.Producer;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final Producer producer;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findProductsNotParsedToday() {
        return productRepository.findProductsNotParsedToday();
    }

    public Product findBySlug(String slug) {
        return productRepository.findBySlug(slug)
            .orElse(null);
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void update(Product product) {
        productRepository.save(product);
    }

    public String sendProductToKafka(ProductDTO productDTO) throws JsonProcessingException {
        return producer.sendMessage(productDTO);
    }

}

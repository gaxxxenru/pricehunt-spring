package ru.pricehunt.assortment.retailer;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.pricehunt.assortment.model.PaymentMethod;
import ru.pricehunt.assortment.model.Retailer;
import ru.pricehunt.assortment.paymentmethod.PaymentMethodService;

import java.util.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RetailerService {
    private final RetailerRepository retailerRepository;
    private final PaymentMethodService paymentMethodService;


    public Retailer findBySlug(String slug) {

        return retailerRepository.findBySlug(slug)
            .orElseThrow(() -> new EntityNotFoundException("Retailer не найден"));
    }


    @Transactional
    public List<Retailer> findAll() {
        return retailerRepository.findAll();
    }

    @Transactional
    public Retailer save(Retailer retailer) {
        List<PaymentMethod> paymentMethods = retailer.getPaymentMethods();
        if (paymentMethods == null) {
            throw new RuntimeException("PaymentMethods не могут быть пустыми");
        }
        List<PaymentMethod> newPaymentMethods = new ArrayList<>();
        for (PaymentMethod paymentMethod : paymentMethods) {
            PaymentMethod existingPaymentMethod = paymentMethodService.findBySlug(paymentMethod.getSlug());
            if (existingPaymentMethod != null && !retailer.getPaymentMethods().contains(existingPaymentMethod)) {
                newPaymentMethods.add(existingPaymentMethod);
            }
        }
        retailer.getPaymentMethods().clear();
        retailer.setPaymentMethods(newPaymentMethods);
        return retailerRepository.save(retailer);
    }
    @Transactional
    public Retailer update(String slug, Retailer category) {

        Retailer existingRetailer = retailerRepository.findBySlug(slug)
            .orElseThrow(() -> new EntityNotFoundException("Retailer не найден"));

        if (category.getPaymentMethods() != null) {
            List<PaymentMethod> paymentMethods = category.getPaymentMethods();
            existingRetailer.getPaymentMethods().clear();
            for (PaymentMethod paymentMethod : paymentMethods) {
                PaymentMethod existingPaymentMethod = paymentMethodService.findBySlug(paymentMethod.getSlug());
                existingRetailer.getPaymentMethods().add(existingPaymentMethod);
            }
        }

        Optional.ofNullable(category.getName()).ifPresent(existingRetailer::setName);
        Optional.ofNullable(category.getDescription()).ifPresent(existingRetailer::setDescription);
        Optional.ofNullable(category.getImageUrl()).ifPresent(existingRetailer::setImageUrl);
        Optional.ofNullable(category.getHasDelivery()).ifPresent(existingRetailer::setHasDelivery);
        Optional.ofNullable(category.getHasPickup()).ifPresent(existingRetailer::setHasPickup);
        Optional.ofNullable(category.getDeliveryPrice()).ifPresent(existingRetailer::setDeliveryPrice);

        return retailerRepository.save(existingRetailer);

    }
    @Transactional
    public Retailer delete(String slug) {

        Retailer existingRetailer = retailerRepository.findBySlug(slug)
            .orElseThrow(() -> new EntityNotFoundException("Retailer не найден"));

        retailerRepository.delete(existingRetailer);
        return existingRetailer;
    }

}

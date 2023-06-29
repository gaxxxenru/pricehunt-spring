package ru.pricehunt.assortment.paymentmethod;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pricehunt.assortment.model.PaymentMethod;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethod findBySlug(String slug) {
        return paymentMethodRepository.findBySlug(slug)
            .orElseThrow(() -> new EntityNotFoundException("PaymentMethod не найден"));
    }


    public List<PaymentMethod> findAll() {
        return paymentMethodRepository.findAll();
    }


    @Transactional
    public PaymentMethod save(PaymentMethod category) {
        return paymentMethodRepository.save(category);
    }

    @Transactional
    public PaymentMethod update(String slug, PaymentMethod category) {
        PaymentMethod existingPaymentMethod = paymentMethodRepository.findBySlug(slug)
            .orElseThrow(() -> new EntityNotFoundException("PaymentMethod не найден"));
        Optional.ofNullable(category.getName()).ifPresent(existingPaymentMethod::setName);
        Optional.ofNullable(category.getDescription()).ifPresent(existingPaymentMethod::setDescription);
        return paymentMethodRepository.save(existingPaymentMethod);
    }

    @Transactional
    public PaymentMethod delete(String slug) {
        PaymentMethod existingPaymentMethod = paymentMethodRepository.findBySlug(slug)
            .orElseThrow(() -> new EntityNotFoundException("PaymentMethod не найден"));
        paymentMethodRepository.delete(existingPaymentMethod);
        return existingPaymentMethod;
    }

}

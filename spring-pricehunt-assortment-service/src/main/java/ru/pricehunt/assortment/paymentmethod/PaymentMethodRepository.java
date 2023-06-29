package ru.pricehunt.assortment.paymentmethod;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pricehunt.assortment.model.PaymentMethod;

import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, String> {

    Optional<PaymentMethod> findBySlug(String slug);
}

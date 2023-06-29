package ru.pricehunt.assortment.paymentmethod;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pricehunt.assortment.dto.PaymentMethodDTO;
import ru.pricehunt.assortment.mapper.PaymentMethodMapper;
import ru.pricehunt.assortment.model.PaymentMethod;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payment-method")
@Tag(name = "Payment Method Controller", description = "Контроллер для работы с методами оплаты")
public class PaymentMethodController {
    private final PaymentMethodService paymentMethodService;
    private final PaymentMethodMapper paymentMethodMapper;

    @GetMapping
    @Operation(summary = "Список всех методов оплаты", description = "Возвращает список всех методов оплаты")
    public List<PaymentMethodDTO> getAllPaymentMethods() {
        return paymentMethodMapper.paymentMethodsToPaymentMethodDTOs(paymentMethodService.findAll());
    }


    @GetMapping("/{slug}")
    @Operation(summary = "Метод оплаты по slug", description = "Возвращает метод оплаты по slug")
    public PaymentMethodDTO getPaymentMethodBySlug(@PathVariable String slug) {
        return paymentMethodMapper.paymentMethodToPaymentMethodDTO(paymentMethodService.findBySlug(slug));
    }

    @PutMapping("/{slug}")
    @Operation(summary = "Обновление метода оплаты", description = "Обновляет метод оплаты по slug")
    public PaymentMethodDTO updatePaymentMethod(@PathVariable String slug, @RequestBody PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod paymentMethod = paymentMethodMapper.paymentMethodDTOToPaymentMethod(paymentMethodDTO);
        return paymentMethodMapper.paymentMethodToPaymentMethodDTO(paymentMethodService.update(slug, paymentMethod));
    }

    @PostMapping
    @Operation(summary = "Создание метода оплаты", description = "Создает метод оплаты")
    public PaymentMethodDTO createPaymentMethod(@RequestBody PaymentMethodDTO paymentMethodDTO) {
        PaymentMethod paymentMethod = paymentMethodMapper.paymentMethodDTOToPaymentMethod(paymentMethodDTO);
        return paymentMethodMapper.paymentMethodToPaymentMethodDTO(paymentMethodService.save(paymentMethod));
    }

    @DeleteMapping("/{slug}")
    @Operation(summary = "Удаление метода оплаты", description = "Удаляет метод оплаты по slug")
    public PaymentMethodDTO deletePaymentMethod(@PathVariable String slug) {
        return paymentMethodMapper.paymentMethodToPaymentMethodDTO(paymentMethodService.delete(slug));
    }
}

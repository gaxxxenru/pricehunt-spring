package ru.pricehunt.assortment.mapper;

import org.mapstruct.Mapper;
import ru.pricehunt.assortment.dto.PaymentMethodDTO;
import ru.pricehunt.assortment.model.PaymentMethod;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    PaymentMethod paymentMethodDTOToPaymentMethod(PaymentMethodDTO paymentMethodDto);
    PaymentMethodDTO paymentMethodToPaymentMethodDTO(PaymentMethod paymentMethod);

    List<PaymentMethodDTO> paymentMethodsToPaymentMethodDTOs(List<PaymentMethod> paymentMethods);

    List<PaymentMethod> paymentMethodDTOsToPaymentMethods(List<PaymentMethodDTO> paymentMethodDtos);

}

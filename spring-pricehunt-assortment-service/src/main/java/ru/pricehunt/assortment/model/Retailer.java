package ru.pricehunt.assortment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Retailer extends BaseEntity  {
    @Id
    @NotBlank
    @Size(max = 50, message = "Поле slug не может быть длиннее 50 символов")
    private String slug;

    @NotBlank(message = "Поле name не может быть пустым")
    @Size(max = 100, message = "Поле name не может быть длиннее 100 символов")
    private String name;
    @NotBlank(message = "Поле description не может быть пустым")
    @Size(max = 500, message = "Поле description не может быть длиннее 500 символов")
    private String description;
    @Pattern(regexp = "(http(s)?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ;,./?%&=]*)?", message = "Пожалуйста, введите корректный URL")
    private String imageUrl;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(
        name = "retailer_payment_method",
        joinColumns = @JoinColumn(name = "retailer_slug"),
        inverseJoinColumns = @JoinColumn(name = "payment_method_id")
    )
    @ToString.Exclude
    private List<PaymentMethod> paymentMethods = new ArrayList<>();

    private Boolean hasDelivery;
    private Boolean hasPickup;

    @PositiveOrZero(message = "Поле deliveryPrice не может быть отрицательным")
    private BigDecimal deliveryPrice;
}

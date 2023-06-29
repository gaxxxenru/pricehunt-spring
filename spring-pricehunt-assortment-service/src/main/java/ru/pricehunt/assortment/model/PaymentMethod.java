package ru.pricehunt.assortment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PaymentMethod extends BaseEntity {
    @Id
    @NotBlank(message = "Поле slug не может быть пустым")
    @Size(max = 50, message = "Поле slug не может быть длиннее 50 символов")
    private String slug;
    @NotBlank(message = "Поле name не может быть пустым")
    @Size(max = 100, message = "Поле name не может быть длиннее 50 символов")
    private String name;
    @Size(max = 500, message = "Поле description не может быть длиннее 1000 символов")
    private String description;
}

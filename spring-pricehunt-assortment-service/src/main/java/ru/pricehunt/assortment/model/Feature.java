package ru.pricehunt.assortment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Feature extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Поле name не может быть пустым")
    @Size(max = 255, message = "Поле name не может быть длиннее 255 символов")
    private String name;

    @NotBlank(message = "Поле value не может быть пустым")
    @Size(max = 255, message = "Поле value не может быть длиннее 255 символов")
    private String value;
}

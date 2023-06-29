package ru.pricehunt.assortment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product extends BaseEntity {
    @Id
    private String slug;

    @NotBlank(message = "Поле name не может быть пустым")
    @Size(max = 255, message = "Поле name не может быть длиннее 255 символов")
    private String name;

    @NotBlank(message = "Поле description не может быть пустым")
    @Size(max = 10000, message = "Поле description не может быть длиннее 10000 символов")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_slug", nullable = false)
    @ToString.Exclude
    private Category category;

    @OneToMany(fetch = FetchType.LAZY ,mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ProductImage> images = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "min_price_id", referencedColumnName = "id")
    private ProductPrice bestProductPrice;


    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "product_feature",
        joinColumns = @JoinColumn(name = "product_slug"),
        inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    @ToString.Exclude
    private List<Feature> features = new ArrayList<>();
}

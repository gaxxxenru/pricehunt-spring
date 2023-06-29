package ru.pricehunt.assortment.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserProductFavorite extends BaseEntity {
    @Id
    private String userEmail;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_favorite_product",
        joinColumns = @JoinColumn(name = "user_email"),
        inverseJoinColumns = @JoinColumn(name = "product_slug")
    )
    @ToString.Exclude
    private List<Product> products = new ArrayList<>();
}

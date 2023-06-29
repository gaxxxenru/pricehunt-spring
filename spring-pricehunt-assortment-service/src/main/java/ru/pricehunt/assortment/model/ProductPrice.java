package ru.pricehunt.assortment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    uniqueConstraints =
    @UniqueConstraint(columnNames = {"product_slug", "retailer_slug", "createdAt"})
)
public class ProductPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "product_slug", nullable = false)
    @ToString.Exclude
    private Product product;

    @ManyToOne
    @JoinColumn(name = "retailer_slug", nullable = false)
    @ToString.Exclude
    private Retailer retailer;

    @PositiveOrZero(message = "Поле price не может быть отрицательным")
    private BigDecimal price;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    private Date createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}

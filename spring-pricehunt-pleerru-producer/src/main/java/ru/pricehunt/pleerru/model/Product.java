package ru.pricehunt.pleerru.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
public class Product {
    @Id
    @Column(nullable = false)
    private String slug;
    @Column(nullable = false)
    private String url;
    @Column(nullable = false)
    private String categorySlug;
    @Temporal(TemporalType.DATE)
    private Date lastParsingDate;
}

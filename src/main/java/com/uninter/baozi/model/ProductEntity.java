package com.uninter.baozi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "baozi_products")
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "has_stock", nullable = false)
    private boolean hasStock;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    public static BigDecimal normalizePrice(BigDecimal price) {
        if (BigDecimal.ZERO.compareTo(price) > 0) {
            price = BigDecimal.ZERO;
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }

}

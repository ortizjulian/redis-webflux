package co.com.shop.r2dbc;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class ProductEntity {
    @Id
    private Long id;
    private String name;
    private String description;
    private double price;
}


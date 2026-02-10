package co.com.shop.r2dbc;

import co.com.shop.model.product.Product;
import co.com.shop.model.product.gateways.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class ProductReactiveRepositoryAdapter implements ProductPersistencePort {

    private final ProductRepository repository;

    @Override
    public Flux<Product> findTopMostCheaper() {
        return repository.findTop10ByOrderByPriceAsc()
                .map(this::toDomain);
    }

    private Product toDomain(ProductEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .build();
    }
}


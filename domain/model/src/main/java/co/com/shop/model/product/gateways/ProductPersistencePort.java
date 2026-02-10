package co.com.shop.model.product.gateways;

import co.com.shop.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductPersistencePort {
    Flux<Product> findTopMostCheaper();

    Mono<Product> findById(Long id);

    Mono<Product> save(Product product);

    Mono<Void> deleteById(Long id);
}
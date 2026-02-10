package co.com.shop.model.product.gateways;

import co.com.shop.model.product.Product;
import reactor.core.publisher.Flux;

public interface ProductPersistencePort {
    Flux<Product> findTopMostCheaper();
}
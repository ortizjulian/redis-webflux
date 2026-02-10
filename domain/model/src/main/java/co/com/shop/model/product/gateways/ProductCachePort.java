package co.com.shop.model.product.gateways;

import co.com.shop.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductCachePort {
    Flux<Product> findTopMostCheaper();
    Mono<Void> saveTopMostCheaper(List<Product> products);
}


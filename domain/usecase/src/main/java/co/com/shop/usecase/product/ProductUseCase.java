package co.com.shop.usecase.product;

import co.com.shop.model.product.Product;
import co.com.shop.model.product.gateways.ProductCachePort;
import co.com.shop.model.product.gateways.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Slf4j
public class ProductUseCase {

    private final ProductCachePort cachePort;
    private final ProductPersistencePort persistencePort;

    public Flux<Product> findMostCheaper() {
        long start = System.currentTimeMillis();

        return cachePort.findTopMostCheaper()
                .doOnComplete(() -> {
                    long end = System.currentTimeMillis();
                    log.info(">>> ORIGEN: REDIS | TIEMPO: {}ms", (end - start));
                })
                .switchIfEmpty(
                        persistencePort.findTopMostCheaper()
                                .collectList()
                                .flatMapMany(list ->
                                        cachePort.saveTopMostCheaper(list)
                                                .thenMany(Flux.fromIterable(list))
                                )
                                .doOnComplete(() -> {
                                    long end = System.currentTimeMillis();
                                    log.info(">>> ORIGEN: DB | TIEMPO: {}ms", (end - start));
                                })
                );
    }
}

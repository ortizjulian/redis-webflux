package co.com.shop.usecase.product;

import co.com.shop.model.product.Product;
import co.com.shop.model.product.gateways.ProductCachePort;
import co.com.shop.model.product.gateways.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Mono<Void> warmUpProductCache() {
        log.info(">>> CACHE WARMING | Iniciando precarga...");
        long start = System.currentTimeMillis();

        return persistencePort.findTopMostCheaper()
                .collectList()
                .flatMap(products ->
                        cachePort.saveTopMostCheaper(products)
                                .doOnSuccess(v -> log.info(">>> CACHE WARMING | {} productos cargados | TIEMPO: {}ms",
                                        products.size(), System.currentTimeMillis() - start))
                );
    }


    public Mono<Product> saveProductWriteThrough(Product product) {
        long start = System.currentTimeMillis();

        return persistencePort.save(product)
                .flatMap(savedProduct ->
                        cachePort.save(savedProduct)
                                .thenReturn(savedProduct)
                )
                .doOnSuccess(p -> log.info(">>> WRITE-THROUGH | ID: {} | TIEMPO: {}ms",
                        p.getId(), System.currentTimeMillis() - start));
    }

    public Mono<Product> findById(Long id) {
        long start = System.currentTimeMillis();

        return cachePort.findById(id)
                .doOnSuccess(p -> {
                    if (p != null) {
                        log.info(">>> ORIGEN: REDIS | ID: {} | TIEMPO: {}ms", id, System.currentTimeMillis() - start);
                    }
                })
                .switchIfEmpty(
                        persistencePort.findById(id)
                                .flatMap(product ->
                                        cachePort.save(product)
                                                .thenReturn(product)
                                )
                                .doOnSuccess(p -> {
                                    if (p != null) {
                                        log.info(">>> ORIGEN: DB | ID: {} | TIEMPO: {}ms", id, System.currentTimeMillis() - start);
                                    }
                                })
                );
    }

    public Mono<Void> deleteProduct(Long id) {
        return persistencePort.deleteById(id)
                .then(cachePort.delete(id))
                .doOnSuccess(deleted -> log.info(">>> CACHE INVALIDATION | ID: {} | Eliminado: {}", id, deleted))
                .then();
    }


}

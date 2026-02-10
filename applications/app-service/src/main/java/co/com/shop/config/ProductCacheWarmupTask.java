package co.com.shop.config;

import co.com.shop.usecase.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ProductCacheWarmupTask {

    private final ProductUseCase productUseCase;

    @Scheduled(
        fixedRateString = "20000"
    )
    public void warmupMostCheaperProducts() {
        log.info("Ejecutando warmup de productos más baratos");

        productUseCase.warmUpProductCache()
                .subscribe();
    }
}

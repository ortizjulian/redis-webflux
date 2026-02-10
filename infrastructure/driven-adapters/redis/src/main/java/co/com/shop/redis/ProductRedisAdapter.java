package co.com.shop.redis;

import co.com.shop.model.product.Product;
import co.com.shop.model.product.gateways.ProductCachePort;
import co.com.shop.redis.template.helper.ReactiveTemplateAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;


@Component
public class ProductRedisAdapter
        extends ReactiveTemplateAdapterOperations<Product, String, Product>
        implements ProductCachePort {

    private static final String CACHE_KEY = "top_products";
    private static final String PRODUCT_PREFIX = "product:";
    private static final Long DEFAULT_TTL = Duration.ofMinutes(5).toMillis();

    public ProductRedisAdapter(ReactiveRedisConnectionFactory factory, ObjectMapper mapper) {
        super(factory, mapper, d -> d);
    }

    @Override
    public Flux<Product> findTopMostCheaper() {
        return findAllList(CACHE_KEY);
    }

    @Override
    public Mono<Void> saveTopMostCheaper(List<Product> products) {
        return saveList(CACHE_KEY, products, Duration.ofSeconds(40));
    }

    @Override
    public Mono<Void> save(Product product) {
        return save(PRODUCT_PREFIX + product.getId(), product, DEFAULT_TTL).then();
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return delete(PRODUCT_PREFIX + id).hasElement();
    }

    @Override
    public Mono<Product> findById(Long id) {
        return findById(PRODUCT_PREFIX + id);
    }

}

package co.com.shop.api.handler;

import co.com.shop.model.product.Product;
import co.com.shop.usecase.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductUseCase useCase;

    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(useCase.findMostCheaper(), Product.class);
    }
}


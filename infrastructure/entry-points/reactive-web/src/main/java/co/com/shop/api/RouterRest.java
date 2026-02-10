package co.com.shop.api;

import co.com.shop.api.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> route(ProductHandler handler) {
        return RouterFunctions.route()
                .GET("/products/cheapest", handler::getMostCheaper)
                .GET("/products/{id}", handler::getById)
                .POST("/products", handler::saveProduct)
                .DELETE("/products/{id}", handler::deleteProduct)
                .build();
    }
}

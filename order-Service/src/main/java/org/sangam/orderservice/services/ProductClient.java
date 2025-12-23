package org.sangam.orderservice.services;

import lombok.RequiredArgsConstructor;
import org.sangam.orderservice.dtos.ProductResponseDTO;
import org.sangam.orderservice.dtos.StockUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final WebClient.Builder webClient;

    // GET product by id
    public ProductResponseDTO getProductById(Long productId) {


        return webClient
                .baseUrl("http://PRODUCT-SERVICE")
                .build()
                .get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .bodyToMono(ProductResponseDTO.class)
                .block();
    }

    // PATCH update stock
    public void updateStock(Long productId, int quantity) {

        webClient
                .baseUrl("http://PRODUCT-SERVICE")
                .build()
                .patch()
                .uri("/api/products/{id}/stock?quantity={q}", productId,quantity)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}

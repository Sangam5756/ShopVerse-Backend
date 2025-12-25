package org.ecommerce.orderservice.dtos;


import lombok.Data;

@Data
public class OrderItemRequestDTO {

    private Long productId;
    private int quantity;


}

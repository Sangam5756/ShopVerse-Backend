package org.sangam.orderservice.dtos;


import lombok.Data;

@Data
public class OrderItemRequestDTO {

    private Long productId;
    private int quantity;


}

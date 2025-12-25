package org.ecommerce.orderservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecommerce.orderservice.entities.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderResponseDTO {
    private Long orderId;
    private Long customerId;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private OrderStatus orderStatus;
    private List<OrderItem> items;
//    private ProductResponseDTO product;
}

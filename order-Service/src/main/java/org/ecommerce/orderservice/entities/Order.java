package org.ecommerce.orderservice.entities;


import jakarta.persistence.*;
import lombok.*;
import org.ecommerce.orderservice.dtos.OrderStatus;

import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    private Long id;
    private Long customerId;
    private LocalDateTime orderDate;
    private double totalAmount;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}

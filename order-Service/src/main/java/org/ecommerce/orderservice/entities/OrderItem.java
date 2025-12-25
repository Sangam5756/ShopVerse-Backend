package org.ecommerce.orderservice.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "orderItems")
public class OrderItem {
    @Id
    private Long orderItemId;
    private Long productId;
    private Long orderId;
    private int quantity;
    private double price;

}

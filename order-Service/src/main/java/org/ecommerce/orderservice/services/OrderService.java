package org.ecommerce.orderservice.services;

import org.ecommerce.orderservice.dtos.OrderRequestDTO;
import org.ecommerce.orderservice.dtos.OrderResponseDTO;
import org.ecommerce.orderservice.dtos.OrderStatus;

import java.util.List;


public interface OrderService {

    OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO);
    OrderResponseDTO getOrderById(long orderId);
    List<OrderResponseDTO> getOrdersByCustomerId(long customerId);
  void updateOrderStatus(Long orderId, OrderStatus orderStatus);

}

package org.sangam.orderservice.services;

import org.sangam.orderservice.dtos.OrderRequestDTO;
import org.sangam.orderservice.dtos.OrderResponseDTO;
import org.sangam.orderservice.dtos.OrderStatus;

import java.util.List;


public interface OrderService {

    OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO);
    OrderResponseDTO getOrderById(long orderId);
    List<OrderResponseDTO> getOrdersByCustomerId(long customerId);
  void updateOrderStatus(Long orderId, OrderStatus orderStatus);

}

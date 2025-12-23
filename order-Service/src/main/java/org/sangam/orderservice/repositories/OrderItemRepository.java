    package org.sangam.orderservice.repositories;

import org.sangam.orderservice.entities.Order;
import org.sangam.orderservice.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

    public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {


    List<OrderItem> findByOrderId(Long orderId);

        List<OrderItem> findAllByOrderId(Long orderId);
    }

package org.ecommerce.orderservice.repositories;

import org.ecommerce.orderservice.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {


    List<Order> findByCustomerId(Long customerId);
}

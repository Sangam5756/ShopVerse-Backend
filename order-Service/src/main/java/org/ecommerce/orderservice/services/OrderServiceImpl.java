package org.ecommerce.orderservice.services;


import lombok.AllArgsConstructor;
import org.ecommerce.orderservice.dtos.*;
import org.ecommerce.orderservice.entities.Order;
import org.ecommerce.orderservice.entities.OrderItem;
import org.ecommerce.orderservice.producer.OrderEventPublisher;
import org.ecommerce.orderservice.repositories.OrderItemRepository;
import org.ecommerce.orderservice.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class OrderServiceImpl {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;
    private final OrderEventPublisher orderEventPublisher;


    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO, String userEmail) {
//                generate orderId
        Long orderId = generateOrderId();
        Long orderItemId = generateOrderItemId();
//            validate the product  in the order
        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequestDTO itemRequest : orderRequestDTO.getItems()) {


            ProductResponseDTO product = productClient.getProductById(itemRequest.getProductId());
            System.out.println(product.getName());
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }


//                now will calculate total item amount for each
            double itemTotal = itemRequest.getQuantity() * product.getPrice();
            totalAmount += itemTotal;

//                 now for each item will have the orderItem

            OrderItem orderItem =
                    new OrderItem(generateOrderItemId(), itemRequest.getProductId(), orderId, itemRequest.getQuantity(), product.getPrice());
            orderItems.add(orderItem);

        }
//create the new Order
        Order order = new Order(orderId, orderRequestDTO.getCustomerId(), LocalDateTime.now(), totalAmount, OrderStatus.PENDING);


//            save the orders
        orderRepository.save(order);
//            save all the orderitems

        orderItemRepository.saveAll(orderItems);

        orderEventPublisher.orderPlaced(userEmail, totalAmount, orderId);

        return new OrderResponseDTO(order.getId(), order.getCustomerId(), order.getOrderDate(), order.getTotalAmount(), order.getStatus(), orderItems);
    }


    public OrderResponseDTO getOrderById(Long orderId) {
//            check order exist
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
//                    then list all items
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        return new OrderResponseDTO(order.getId(), order.getCustomerId(), order.getOrderDate(), order.getTotalAmount(), order.getStatus(), items);
    }

    //        get orderby customer id
    public List<OrderResponseDTO> getOrdersByCustomerId(Long customerId) {
//            find the order of customer
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            responseList.add(new OrderResponseDTO(order.getId(), order.getCustomerId(), order.getOrderDate(), order.getTotalAmount(), order.getStatus(), items));
        }

        return responseList;
    }

    public List<OrderResponseDTO> getAllOrdersForAdmin() {
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            responseList.add(new OrderResponseDTO(order.getId(), order.getCustomerId(), order.getOrderDate(), order.getTotalAmount(), order.getStatus(), items));
        }

        return responseList;
    }

    public void updateOrderStatus(Long orderId, OrderStatus orderStatus, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(orderStatus);
        orderRepository.save(order);
        orderEventPublisher.orderStatusUpdated(userEmail, orderStatus.name(), orderId);
    }


    public Long generateOrderItemId() {
        return UUID.randomUUID().getLeastSignificantBits();
    }

    public Long generateOrderId() {
        return UUID.randomUUID().getMostSignificantBits();
    }
}




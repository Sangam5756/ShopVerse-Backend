package org.ecommerce.orderservice.consumer;

import org.ecommerce.orderservice.dtos.NotificationEvent;
import org.ecommerce.orderservice.dtos.OrderStatus;
import org.ecommerce.orderservice.entities.Order;
import org.ecommerce.orderservice.entities.OrderItem;
import org.ecommerce.orderservice.repositories.OrderItemRepository;
import org.ecommerce.orderservice.repositories.OrderRepository;
import org.ecommerce.orderservice.services.OrderServiceImpl;
import org.ecommerce.orderservice.services.ProductClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentEventConsumerTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ProductClient productClient;
    @Mock
    private OrderServiceImpl orderService;

    @InjectMocks
    private PaymentEventConsumer paymentEventConsumer;

    @Test
    void testConsumePaymentSuccess() {
        NotificationEvent event = NotificationEvent.builder()
                .eventType("PAYMENT_SUCCESS")
                .orderId(1L)
                .userEmail("test@example.com")
                .build();

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        OrderItem item = new OrderItem();
        item.setProductId(101L);
        item.setQuantity(2);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Collections.singletonList(item));

        paymentEventConsumer.consumePaymentEvent(event);

        verify(orderService).updateOrderStatus(1L, OrderStatus.CONFIRMED, "test@example.com");
        verify(productClient).updateStock(101L, -2);
    }
}

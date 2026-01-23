package org.ecommerce.orderservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ecommerce.orderservice.dtos.NotificationEvent;
import org.ecommerce.orderservice.dtos.OrderStatus;
import org.ecommerce.orderservice.entities.Order;
import org.ecommerce.orderservice.entities.OrderItem;
import org.ecommerce.orderservice.repositories.OrderItemRepository;
import org.ecommerce.orderservice.repositories.OrderRepository;
import org.ecommerce.orderservice.services.OrderServiceImpl;
import org.ecommerce.orderservice.services.ProductClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;
    private final OrderServiceImpl orderService;

    @KafkaListener(topics = "notification-topic", groupId = "order-group")
    @Transactional
    public void consumePaymentEvent(NotificationEvent event) {
        log.info("Consumed payment event: {}", event);

        if ("PAYMENT_SUCCESS".equals(event.getEventType())) {
            handlePaymentSuccess(event);
        } else if ("PAYMENT_FAILED".equals(event.getEventType())) {
            handlePaymentFailed(event);
        }
    }

    private void handlePaymentSuccess(NotificationEvent event) {
        Long orderId = extractOrderIdFromMessage(event.getMessage());
        log.info("Processing payment success for order: {}", orderId);

        orderRepository.findById(orderId).ifPresent(order -> {
            if (order.getStatus() == OrderStatus.PENDING) {
                // Update Order Status
                orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED, event.getUserEmail());

                // Reduce Stock
                List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
                for (OrderItem item : items) {
                    try {
                        productClient.updateStock(item.getProductId(), -item.getQuantity());
                        log.info("Reduced stock for product: {} by {}", item.getProductId(), item.getQuantity());
                    } catch (Exception e) {
                        log.error("Failed to update stock for product: {}", item.getProductId(), e);
                        // In a real system, you'd handle this with a compensation transaction or manual intervention
                    }
                }
            }
        });
    }

    private void handlePaymentFailed(NotificationEvent event) {
        Long orderId = extractOrderIdFromMessage(event.getMessage());
        log.info("Processing payment failure for order: {}", orderId);
        orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED, event.getUserEmail());
    }

    private Long extractOrderIdFromMessage(String message) {
        if (message == null) {
            log.error("Message is null, cannot extract order ID");
            return null;
        }
        
        // Extract order ID from message like "Payment successful for Order #123"
        Pattern pattern = Pattern.compile("Order #(\\d+)");
        Matcher matcher = pattern.matcher(message);
        
        if (matcher.find()) {
            try {
                return Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                log.error("Failed to parse order ID from message: {}", message, e);
                return null;
            }
        }
        
        log.error("No order ID found in message: {}", message);
        return null;
    }
}

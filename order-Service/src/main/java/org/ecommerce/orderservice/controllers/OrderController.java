package org.ecommerce.orderservice.controllers;


import lombok.AllArgsConstructor;
import org.ecommerce.orderservice.dtos.OrderRequestDTO;
import org.ecommerce.orderservice.dtos.OrderResponseDTO;
import org.ecommerce.orderservice.dtos.OrderStatus;
import org.ecommerce.orderservice.services.OrderServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {

    private  final OrderServiceImpl orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestBody OrderRequestDTO request,
            Authentication auth
    ) {
        String userEmail = auth.getName();
        return ResponseEntity.ok(orderService.placeOrder(request, userEmail));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable long orderId){
        return  ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getOrdersByCustomerId(@PathVariable long customerId){
        return  ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable long orderId,
            @RequestParam OrderStatus orderStatus,
            Authentication auth
    ) {
        String userEmail = auth.getName();
        orderService.updateOrderStatus(orderId, orderStatus, userEmail);
        return ResponseEntity.ok("Order status updated");
    }


}

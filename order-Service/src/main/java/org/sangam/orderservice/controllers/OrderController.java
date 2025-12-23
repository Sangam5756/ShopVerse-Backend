package org.sangam.orderservice.controllers;


import lombok.AllArgsConstructor;
import org.sangam.orderservice.dtos.OrderRequestDTO;
import org.sangam.orderservice.dtos.OrderResponseDTO;
import org.sangam.orderservice.dtos.OrderStatus;
import org.sangam.orderservice.services.OrderServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {

    private  final OrderServiceImpl orderService;



    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO request){
        OrderResponseDTO  orderResponseDTO = orderService.placeOrder(request);

        return  ResponseEntity.ok(orderResponseDTO);
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
    public ResponseEntity<?> updateOrderStatus(@PathVariable long orderId, @RequestParam OrderStatus orderStatus){
         orderService.updateOrderStatus(orderId,orderStatus);
        return ResponseEntity.ok("Order status updated successfully to "+orderStatus.name());
    }

}

package org.ecommerce.paymentservice.controllers;


import lombok.RequiredArgsConstructor;
import org.ecommerce.paymentservice.dtos.PaymentRequestDTO;
import org.ecommerce.paymentservice.dtos.RazorpayCallbackDTO;
import org.ecommerce.paymentservice.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;



    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequestDTO payment) throws Exception {
        return ResponseEntity.ok(paymentService.createPayment(payment));
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback( @RequestBody RazorpayCallbackDTO callback, Authentication auth) {
        String userEmail = auth.getName();

        paymentService.markPaymentSuccess(
                callback.getRazorpayOrderId(),
                callback.getRazorpayPaymentId(),
                userEmail
        );

        return ResponseEntity.ok("Payment Success");
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getAllPaymentByCustomerId(@PathVariable Long customerId){
        return ResponseEntity.ok(paymentService.getPaymentsByCustomerId(customerId));
    }



}

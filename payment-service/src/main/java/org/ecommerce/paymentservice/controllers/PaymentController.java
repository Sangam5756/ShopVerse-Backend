package org.ecommerce.paymentservice.controllers;


import lombok.RequiredArgsConstructor;
import org.ecommerce.paymentservice.dtos.PaymentRequestDTO;
import org.ecommerce.paymentservice.dtos.RazorpayCallbackDTO;
import org.ecommerce.paymentservice.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequestDTO payment) throws Exception {
        return ResponseEntity.ok(paymentService.createPayment(payment));
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody RazorpayCallbackDTO callback) {
        paymentService.markPaymentSuccess(callback.getRazorpayOrderId(),callback.getRazorpayPaymentId());
        return ResponseEntity.ok("Payment Success");
    }


}

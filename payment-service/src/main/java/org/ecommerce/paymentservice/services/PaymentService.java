package org.ecommerce.paymentservice.services;


import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.AllArgsConstructor;
import org.ecommerce.paymentservice.dtos.PaymentRequestDTO;
import org.ecommerce.paymentservice.dtos.PaymentResponseDTO;
import org.ecommerce.paymentservice.entities.Payment;
import org.ecommerce.paymentservice.entities.PaymentStatus;
import org.ecommerce.paymentservice.repositories.PaymentRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PaymentService {


    private final PaymentRepository paymentRepository;

    private final RazorpayClient razorpayClient;

//        now we create the order and then we directly used that in the

    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO) throws Exception {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount",paymentRequestDTO.getAmount());
        orderRequest.put("currency","INR");
        orderRequest.put("receipt","order"+paymentRequestDTO.getOrderId());

//        now here call the razorpayclient
        Order razorpayOrder = razorpayClient.orders.create(orderRequest);

//        now build the payment response
        Payment payment = Payment.builder()
                .orderId(paymentRequestDTO.getOrderId())
                .customerId(paymentRequestDTO.getCustomerId())
                .amount(paymentRequestDTO.getAmount())
                .razorpayOrderId(razorpayOrder.get("id"))
                .razorpayPaymentId(razorpayOrder.get("paymentId"))
                .paymentStatus(PaymentStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

//        save the payment in the db
//        reutrn response of the payment
        return PaymentResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .razorpayOrderId(payment.getRazorpayPaymentId())
                .amount(payment.getAmount())
                .status(payment.getPaymentStatus().name())
                .build();

    }

//    done with create the order
public void markPaymentSuccess(String razorpayOrderId, String razorpayPaymentId) {
    Payment payment = paymentRepository
            .findByRazorpayOrderId(razorpayOrderId)
            .orElseThrow();

    payment.setRazorpayPaymentId(razorpayPaymentId);
    payment.setPaymentStatus(PaymentStatus.SUCCESS);

    paymentRepository.save(payment);
}


}

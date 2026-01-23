package org.ecommerce.paymentservice.services;


import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.AllArgsConstructor;
import org.ecommerce.paymentservice.dtos.NotificationEvent;
import org.ecommerce.paymentservice.dtos.PaymentRequestDTO;
import org.ecommerce.paymentservice.dtos.PaymentResponseDTO;
import org.ecommerce.paymentservice.dtos.PaymentSummaryDTO;
import org.ecommerce.paymentservice.entities.Payment;
import org.ecommerce.paymentservice.entities.PaymentStatus;
import org.ecommerce.paymentservice.producer.PaymentEventPublisher;
import org.ecommerce.paymentservice.repositories.PaymentRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {


    private final PaymentRepository paymentRepository;

    private final RazorpayClient razorpayClient;

    private final PaymentEventPublisher paymentEventPublisher;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

//        now we create the order and then we directly used that in the

    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO) throws Exception {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount",paymentRequestDTO.getAmount()*100);//as razorpay treat inr as paisa so multiplu by 100
        orderRequest.put("currency","INR");
        orderRequest.put("receipt","order"+paymentRequestDTO.getOrderId());

//        now here call the razorpayclient
        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        System.out.println("razorpayorders object"+razorpayOrder);
//        now build the payment response
        Payment payment = Payment.builder()
                .orderId(paymentRequestDTO.getOrderId())
                .customerId(paymentRequestDTO.getCustomerId())
                .amount(paymentRequestDTO.getAmount())
                .razorpayOrderId(razorpayOrder.get("id"))
                .paymentStatus(PaymentStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

//        save the payment in the db

        paymentRepository.save(payment);
        return PaymentResponseDTO.builder()
                .razorpayOrderId(payment.getRazorpayOrderId())
                .amount(payment.getAmount())
                .status(payment.getPaymentStatus().name())
                .build();

    }

        public void markPaymentSuccess(org.ecommerce.paymentservice.dtos.RazorpayCallbackDTO callback, String userEmail) throws Exception {
        // Verify signature
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", callback.getRazorpayOrderId());
        options.put("razorpay_payment_id", callback.getRazorpayPaymentId());
        options.put("razorpay_signature", callback.getRazorpaySignature());

        boolean isValid = Utils.verifyPaymentSignature(options, razorpaySecret);


        Payment payment = paymentRepository
                .findByRazorpayOrderId(callback.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setRazorpayPaymentId(callback.getRazorpayPaymentId());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        paymentRepository.save(payment);
        paymentEventPublisher.paymentSuccess(userEmail, payment.getOrderId());
    }

    public List<PaymentSummaryDTO> getPaymentsByCustomerId(Long customerId){
        return paymentRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toSummaryDTO)
                .toList();
    }

    public List<PaymentSummaryDTO> getAllPaymentsForAdmin(){
        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toSummaryDTO)
                .toList();
    }



    private PaymentSummaryDTO toSummaryDTO(Payment payment) {
        return PaymentSummaryDTO.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getPaymentStatus().name())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}





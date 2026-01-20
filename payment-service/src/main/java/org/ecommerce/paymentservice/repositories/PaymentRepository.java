package org.ecommerce.paymentservice.repositories;

import org.ecommerce.paymentservice.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PaymentRepository extends JpaRepository<Payment,String> {


    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}

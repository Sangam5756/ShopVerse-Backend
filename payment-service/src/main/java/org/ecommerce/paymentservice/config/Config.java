package org.ecommerce.paymentservice.config;


import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${razorpay.key.id}")
    private  String keyId;
    @Value("${razorpay.key.secret}")
    private String secret;

    @Bean
    RazorpayClient razorpayClieat() throws Exception {
        return new RazorpayClient(keyId,secret);
    }


}

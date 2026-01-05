package com.ecommerce.recommendation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user_interactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInteraction {

    @Id
    private String id;

    @Indexed
    private String userEmail;

    @Indexed
    private String productId;

    private String eventType;   // VIEW, CART, ORDER
    private LocalDateTime timestamp;
}

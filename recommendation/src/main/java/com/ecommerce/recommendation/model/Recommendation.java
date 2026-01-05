package com.ecommerce.recommendation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "recommendations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    @Id
    private String id;

    @Indexed
    private String userEmail;

    private String productId;
    private Double score;        // ranking score
    private String source;       // POPULAR / PERSONAL
    private LocalDateTime createdAt;
}


package com.ecommerce.recommendation.repository;

import com.ecommerce.recommendation.model.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RecommendationRepository
        extends MongoRepository<Recommendation, String> {

    List<Recommendation> findByUserEmailOrderByScoreDesc(String userEmail);

    List<Recommendation> findAllByOrderByCreatedAtDesc();
}

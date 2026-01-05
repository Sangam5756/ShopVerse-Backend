package com.ecommerce.recommendation.service;

import com.ecommerce.recommendation.model.Recommendation;
import com.ecommerce.recommendation.repository.RecommendationRepository;
import com.ecommerce.recommendation.repository.UserInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserInteractionRepository interactionRepo;
    private final RecommendationRepository recommendationRepo;

    public List<Recommendation> generate(String userEmail) {

        List<String> viewedProducts =
                interactionRepo.findTopViewedProductsByUser(userEmail);

        List<String> similarUsers =
                interactionRepo.findUsersWithSameProducts(viewedProducts);

        return interactionRepo.findPopularProductsAmongUsers(similarUsers)
                .stream()
                .limit(10)
                .map(p ->
                        Recommendation.builder()
                                .userEmail(userEmail)
                                .productId(p)
                                .score(Math.random() * 10)   // simple ranking
                                .source("PERSONAL")
                                .createdAt(LocalDateTime.now())
                                .build()
                )
                .toList();
    }
}

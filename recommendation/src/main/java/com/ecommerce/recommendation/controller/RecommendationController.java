package com.ecommerce.recommendation.controller;

import com.ecommerce.recommendation.model.Recommendation;
import com.ecommerce.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService service;

    @GetMapping
    public List<Recommendation> get(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role
    ) {
        if ("ADMIN".equals(role)) {
            throw new RuntimeException("Admin cannot receive recommendations");
        }
        return service.generate(email);
    }
}

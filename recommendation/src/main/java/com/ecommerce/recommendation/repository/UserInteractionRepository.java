package com.ecommerce.recommendation.repository;

import com.ecommerce.recommendation.model.UserInteraction;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserInteractionRepository
        extends MongoRepository<UserInteraction, String> {

    // 1️⃣ Products viewed by a user (sorted by frequency)
    @Aggregation(pipeline = {
            "{ $match: { userEmail: ?0 } }",
            "{ $group: { _id: '$productId', count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }",
            "{ $limit: 20 }",
            "{ $project: { _id: 0, productId: '$_id' } }"
    })
    List<String> findTopViewedProductsByUser(String userEmail);

    // 2️⃣ Users who viewed same products
    @Aggregation(pipeline = {
            "{ $match: { productId: { $in: ?0 } } }",
            "{ $group: { _id: '$userEmail' } }",
            "{ $project: { _id: 0, userEmail: '$_id' } }"
    })
    List<String> findUsersWithSameProducts(List<String> productIds);

    // 3️⃣ Popular products among similar users
    @Aggregation(pipeline = {
            "{ $match: { userEmail: { $in: ?0 } } }",
            "{ $group: { _id: '$productId', count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }",
            "{ $limit: 20 }",
            "{ $project: { _id: 0, productId: '$_id' } }"
    })
    List<String> findPopularProductsAmongUsers(List<String> userEmails);
}

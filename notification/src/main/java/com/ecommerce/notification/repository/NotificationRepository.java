package com.ecommerce.notification.repository;

import com.ecommerce.notification.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByUserEmailOrderByTimestampDesc(String userEmail);

    List<Notification> findAllByOrderByTimestampDesc();
}

package com.ecommerce.notification.controller;

import com.ecommerce.notification.model.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mongo-test")
public class MongoTestController {

    private final NotificationRepository repository;

    public MongoTestController(NotificationRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/save-test")
    public String saveTestNotification() {
        try {
            Notification testNotification = Notification.builder()
                    .userEmail("test@example.com")
                    .eventType("MONGO_TEST")
                    .message("Direct MongoDB test at " + LocalDateTime.now())
                    .timestamp(LocalDateTime.now())
                    .read(false)
                    .build();

            Notification saved = repository.save(testNotification);
            return "✅ Successfully saved to MongoDB with ID: " + saved.getId();
        } catch (Exception e) {
            return "❌ Error saving to MongoDB: " + e.getMessage();
        }
    }

    @GetMapping("/count")
    public String countNotifications() {
        try {
            long count = repository.count();
            List<Notification> all = repository.findAll();
            return "📊 Total notifications in MongoDB: " + count + 
                   "\n📋 Recent notifications: " + all.size();
        } catch (Exception e) {
            return "❌ Error counting notifications: " + e.getMessage();
        }
    }

    @GetMapping("/list")
    public List<Notification> listNotifications() {
        try {
            return repository.findAllByOrderByTimestampDesc();
        } catch (Exception e) {
            throw new RuntimeException("Error listing notifications: " + e.getMessage());
        }
    }
}

package com.ecommerce.notification.controller;

import com.ecommerce.notification.model.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository repository;

    @GetMapping
    public List<Notification> getNotifications(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role)
    {
        if ("ADMIN".equals(role)) {
            return repository.findAllByOrderByTimestampDesc();
        }

        return repository.findByUserEmailOrderByTimestampDesc(email);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable String id) {
        Notification n = repository.findById(id).orElseThrow();
        n.setRead(true);
        repository.save(n);
    }
}

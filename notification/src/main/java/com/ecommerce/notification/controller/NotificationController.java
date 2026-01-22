package com.ecommerce.notification.controller;

import com.ecommerce.notification.model.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        return repository.findById(id)
                .map(n -> {
                    n.setRead(true);
                    repository.save(n);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role)
    {
        List<Notification> unread;
        if ("ADMIN".equals(role)) {
            unread = repository.findByReadFalse();
        } else {
            unread = repository.findByUserEmailAndReadFalse(email);
        }

        unread.forEach(n -> n.setRead(true));
        repository.saveAll(unread);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable String id) {
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
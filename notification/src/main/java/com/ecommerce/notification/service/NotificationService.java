package com.ecommerce.notification.service;

import com.ecommerce.notification.model.Notification;
import com.ecommerce.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    public List<Notification> getUserNotifications(String email) {
        return repository.findByUserEmailOrderByTimestampDesc(email);
    }

    public List<Notification> getAdminNotifications() {
        return repository.findByRoleOrderByTimestampDesc("ADMIN");
    }

    public void markAsRead(String id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        repository.save(notification);
    }
}

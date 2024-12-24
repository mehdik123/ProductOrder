package com.test.notification.service;

import com.test.notification.model.Notification;
import com.test.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public Notification findById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public Notification create(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Notification update(Long id, Notification newData) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    existing.setMessage(newData.getMessage());
                    existing.setRecipient(newData.getRecipient());
                    existing.setSeen(newData.isSeen());
                    return notificationRepository.save(existing);
                })
                .orElse(null);
    }

    public boolean delete(Long id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

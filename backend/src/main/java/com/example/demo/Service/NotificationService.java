package com.example.demo.Service;

import com.example.demo.Model.Notification;
import com.example.demo.Repository.NotificationRepository;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void createNotification(Long userId, String message) {
        Notification notification = new Notification(userId, message);
        notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return Collections.emptyList();
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return Collections.emptyList();
    }

    public void markAllAsRead(Long userId) {
      
    }

    public String markAsRead(Long notificationId) {

        return "Notification not found";
    }
}


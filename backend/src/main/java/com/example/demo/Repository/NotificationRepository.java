package com.example.demo.Repository;

import com.example.demo.Model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    List<Notification> findByUserId(Long userId);
}

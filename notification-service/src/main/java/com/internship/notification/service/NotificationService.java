package com.internship.notification.service;

import com.internship.notification.model.Notification;
import com.internship.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    public List<Notification> getStudentNotifications(Long studentId) {
        log.info("Fetching notifications for student: {}", studentId);
        return notificationRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }
    
    public Notification sendNotification(Long studentId, String type, String message) {
        log.info("Sending notification to student {}: {}", studentId, type);
        Notification notification = new Notification();
        notification.setStudentId(studentId);
        notification.setType(type);
        notification.setMessage(message);
        return notificationRepository.save(notification);
    }
    
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }
}

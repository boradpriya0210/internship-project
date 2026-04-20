package com.internship.notification.controller;

import com.internship.notification.model.Notification;
import com.internship.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private final NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to Notification Service. Access /health for status.");
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long studentId) {
        log.info("Getting notifications for student: {}", studentId);
        return ResponseEntity.ok(notificationService.getStudentNotifications(studentId));
    }
    
    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@RequestBody Map<String, Object> payload) {
        Long studentId = Long.valueOf(payload.get("studentId").toString());
        String type = payload.get("type").toString();
        String message = payload.get("message").toString();
        
        Notification notification = notificationService.sendNotification(studentId, type, message);
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is running!");
    }

    /**
     * Handler for favicon.ico to prevent errors from path variable mismatch
     */
    @GetMapping("favicon.ico")
    @ResponseBody
    public void disableFavicon() {
        // Do nothing
    }
}

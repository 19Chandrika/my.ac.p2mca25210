package com.student.studentdemo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.student.studentdemo.model.Notification;
import com.student.studentdemo.service.NotificationService;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/notifications")
    public Notification createNotification(
            @RequestBody Notification notification) {

        return notificationService.createNotification(notification);
    }

    @GetMapping("/students/{studentId}/notifications")
    public List<Notification> getAllNotifications(
            @PathVariable Long studentId) {

        return notificationService.getAllNotifications(studentId);
    }

    @GetMapping("/students/{studentId}/notifications/unread")
    public List<Notification> getUnreadNotifications(
            @PathVariable Long studentId) {

        return notificationService.getUnreadNotifications(studentId);
    }

    @PutMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id) {

        return notificationService.markAsRead(id);
    }

    @DeleteMapping("/notifications/{id}")
    public String deleteNotification(@PathVariable Long id) {

        return notificationService.deleteNotification(id);
    }
}
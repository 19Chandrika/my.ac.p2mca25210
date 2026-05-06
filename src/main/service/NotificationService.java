package com.student.studentdemo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.student.studentdemo.model.Notification;

@Service
public class NotificationService {

    private final List<Notification> notifications = new ArrayList<>();

    public Notification createNotification(Notification notification) {

        notification.setId((long) (notifications.size() + 1));
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        notifications.add(notification);

        return notification;
    }

    public List<Notification> getAllNotifications(Long studentId) {

        List<Notification> result = new ArrayList<>();

        for (Notification notification : notifications) {

            if (notification.getStudentId().equals(studentId)) {
                result.add(notification);
            }
        }

        return result;
    }

    public List<Notification> getUnreadNotifications(Long studentId) {

        List<Notification> result = new ArrayList<>();

        for (Notification notification : notifications) {

            if (notification.getStudentId().equals(studentId)
                    && !notification.isRead()) {

                result.add(notification);
            }
        }

        return result;
    }

    public String markAsRead(Long id) {

        for (Notification notification : notifications) {

            if (notification.getId().equals(id)) {

                notification.setRead(true);
                return "Notification marked as read";
            }
        }

        return "Notification not found";
    }

    public String deleteNotification(Long id) {

        notifications.removeIf(notification ->
                notification.getId().equals(id));

        return "Notification deleted successfully";
    }
}
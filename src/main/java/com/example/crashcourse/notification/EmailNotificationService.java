package com.example.crashcourse.notification;

public class EmailNotificationService implements NotificationService {

    @Override
    public void send(String to, String message) {
        System.out.println("[EMAIL to " + to + "] " + message);
    }
}

package com.example.crashcourse.notification;

// Needs a constructor argument (the sender name) that Spring can't guess on its own,
// so AppConfig creates it with "new" inside a @Bean method.
public class SmsNotificationService implements NotificationService {

    private final String senderName;

    public SmsNotificationService(String senderName) {
        this.senderName = senderName;
    }

    @Override
    public void send(String to, String message) {
        System.out.println("[SMS from " + senderName + " to " + to + "] " + message);
    }
}

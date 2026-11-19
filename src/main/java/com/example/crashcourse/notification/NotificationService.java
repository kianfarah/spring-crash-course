package com.example.crashcourse.notification;

// Depend on an interface, not a concrete class: the container decides which implementation you get.
public interface NotificationService {

    void send(String to, String message);
}

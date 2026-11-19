package com.example.crashcourse.notification;

import java.time.Clock;
import java.time.LocalTime;

public class WelcomeService {

    private final NotificationService notificationService;
    private final Clock clock;
    private final String welcomeMessage;

    public WelcomeService(NotificationService notificationService, Clock clock, String welcomeMessage) {
        this.notificationService = notificationService;
        this.clock = clock;
        this.welcomeMessage = welcomeMessage;
    }

    public void welcome(String email) {
        String greeting = LocalTime.now(clock).getHour() < 12 ? "Good morning" : "Hello";
        notificationService.send(email, greeting + ". " + welcomeMessage);
    }
}

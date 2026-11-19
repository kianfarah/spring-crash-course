package com.example.crashcourse.notification;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalTime;

@Service
public class WelcomeService {

    private final NotificationService notificationService;
    private final Clock clock;

    // Constructor injection: Spring sees this constructor, finds beans matching the parameter
    // types, and passes them in. No "new", no @Autowired needed (single constructor).
    public WelcomeService(NotificationService notificationService, Clock clock) {
        this.notificationService = notificationService;
        this.clock = clock;
    }

    public void welcome(String email) {
        String greeting = LocalTime.now(clock).getHour() < 12 ? "Good morning" : "Hello";
        notificationService.send(email, greeting + " and welcome aboard!");
    }
}

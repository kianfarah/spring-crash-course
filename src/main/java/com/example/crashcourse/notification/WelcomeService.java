package com.example.crashcourse.notification;

import com.example.crashcourse.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalTime;

@Service
public class WelcomeService {

    private static final Logger log = LoggerFactory.getLogger(WelcomeService.class);

    private final NotificationService notificationService;
    private final Clock clock;
    private final AppProperties properties;

    // Constructor injection: Spring sees this constructor, finds beans matching the parameter
    // types, and passes them in. No "new", no @Autowired needed (single constructor).
    public WelcomeService(NotificationService notificationService, Clock clock, AppProperties properties) {
        this.notificationService = notificationService;
        this.clock = clock;
        this.properties = properties;
    }

    public void welcome(String email) {
        String greeting = LocalTime.now(clock).getHour() < 12 ? "Good morning" : "Hello";
        log.debug("Sending welcome mail from {}", properties.senderEmail()); // only visible with the dev profile
        notificationService.send(email, greeting + ". " + properties.welcomeMessage());
    }
}

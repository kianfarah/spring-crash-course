package com.example.crashcourse;

import com.example.crashcourse.notification.EmailNotificationService;
import com.example.crashcourse.notification.NotificationService;
import com.example.crashcourse.notification.SmsNotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

// JAVA-BASED configuration: a @Configuration class whose @Bean methods return objects for the container.
// Use it when you can't put @Component on the class (JDK / library classes),
// or when creating the object needs logic (a choice, constructor arguments, setup code).
@Configuration
public class AppConfig {

    // Clock is a JDK class: we can't annotate it, so we create the bean here.
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    // Logic that annotations alone can't express: choose the implementation from a setting.
    // The bean is named after the method: "chosenNotificationService".
    // @Value injects ONE setting: notification.channel from application.yml ("email" after the colon is the
    // default if it's missing). The dev profile switches it to "sms".
    @Bean
    public NotificationService chosenNotificationService(@Value("${notification.channel:email}") String channel) {
        return switch (channel) {
            case "sms" -> new SmsNotificationService("CrashCourse");
            default -> new EmailNotificationService();
        };
    }
}

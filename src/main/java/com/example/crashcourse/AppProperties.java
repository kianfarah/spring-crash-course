package com.example.crashcourse;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Binds everything under "app:" in application.yml to this record (type-safe, IDE auto-completion).
// app.welcome-message -> welcomeMessage, app.sender-email -> senderEmail (relaxed binding).
@ConfigurationProperties(prefix = "app")
public record AppProperties(String welcomeMessage, String senderEmail) {
}

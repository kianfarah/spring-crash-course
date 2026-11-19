package com.example.crashcourse.user;

public class EmailAlreadyTakenException extends RuntimeException {

    public EmailAlreadyTakenException(String email) {
        super("Email " + email + " is already registered");
    }
}

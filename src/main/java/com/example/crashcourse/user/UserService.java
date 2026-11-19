package com.example.crashcourse.user;

import com.example.crashcourse.notification.WelcomeService;

import java.util.List;

public class UserService {

    private final UserRepository userRepository;
    private final WelcomeService welcomeService;

    public UserService(UserRepository userRepository, WelcomeService welcomeService) {
        this.userRepository = userRepository;
        this.welcomeService = welcomeService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User create(String username, String email) {
        User user = userRepository.save(username, email);
        welcomeService.welcome(user.email());
        return user;
    }
}

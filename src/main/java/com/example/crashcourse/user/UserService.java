package com.example.crashcourse.user;

import com.example.crashcourse.notification.WelcomeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// Business logic lives in a @Service. For now users are kept in memory; step 05 swaps this for a database.
@Service
public class UserService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);
    private final WelcomeService welcomeService;

    public UserService(WelcomeService welcomeService) {
        this.welcomeService = welcomeService;
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User create(User user) {
        User saved = new User(nextId.getAndIncrement(), user.username(), user.email());
        users.put(saved.id(), saved);
        welcomeService.welcome(saved.email());
        return saved;
    }

    public void delete(Long id) {
        users.remove(id);
    }
}
